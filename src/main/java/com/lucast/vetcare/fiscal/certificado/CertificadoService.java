package com.lucast.vetcare.fiscal.certificado;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lucast.vetcare.fiscal.exception.FiscalException;

public class CertificadoService {

	private static final String SENHA_NAO_PODE_SER_NULA = "Senha não pode ser nula.";
    private static final String ERRO_AO_CARREGAR_INFORMACOES_DO_CERTIFICADO = "Erro ao carregar informações do certificado:";
    private static final String CERTIFICADO_NAO_PODE_SER_NULO = "Certificado não pode ser nulo.";
    private static final String CPF_INDICATOR = "\u0001";
    private static final String CPF_TERMINATOR = "\u0017";
    private static final Pattern PATTERN_CPF = Pattern.compile("(?<!\\d)\\d{11}(?!\\d)");
    private static final int CPF_LENGTH = 11;
    private static final int CPF_OFFSET = 15;
    private static final Pattern PATTERN_CNPJ = Pattern.compile("\\d{14}");
    private static final String CNPJ_INDICATOR = "\u0006\u0005`L\u0001\u0003\u0003";
    private static final int CNPJ_OFFSET = 6;
    private static final int CNPJ_LENGTH = 25;
    
    public static Certificado certificadoPfx(String caminhoCertificado, String senha) throws FiscalException, FileNotFoundException {

        if (!Files.exists(Paths.get(
                Optional.ofNullable(caminhoCertificado).orElseThrow(() -> new FiscalException("Caminho do Certificado não pode ser nulo.")))))
            throw new FileNotFoundException("Arquivo " +
                    caminhoCertificado +
                    " não existe");

        Certificado certificado = new Certificado();

        try {
            certificado.setArquivo(caminhoCertificado);
            certificado.setSenha(Optional.ofNullable(senha).orElseThrow(() -> new FiscalException(SENHA_NAO_PODE_SER_NULA)));
            certificado.setTipoCertificado("ARQUIVO");
            setDadosCertificado(certificado, null);
        } catch (KeyStoreException e) {
            throw new FiscalException("Erro",ERRO_AO_CARREGAR_INFORMACOES_DO_CERTIFICADO + e.getMessage());
        }

        return certificado;
    }
    
    public static Certificado certificadoPfxBytes(byte[] certificadoBytes, String senha) throws FiscalException, IOException {

        Certificado certificado = new Certificado();
        try {
            certificado.setArquivoBytes(Optional.ofNullable(certificadoBytes).orElseThrow(() -> new FiscalException(CERTIFICADO_NAO_PODE_SER_NULO)));
            certificado.setSenha(Optional.ofNullable(senha).orElseThrow(() -> new FiscalException(SENHA_NAO_PODE_SER_NULA)));
            certificado.setTipoCertificado("ARQUIVO_BYTES");
            
            File tempFile = File.createTempFile("certificado", ".pfx");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(certificado.getArquivoBytes());
            }

            certificado.setArquivo(tempFile.getAbsolutePath());
           
            setDadosCertificado(certificado, null);
        } catch (KeyStoreException e) {
            throw new FiscalException("Erro", ERRO_AO_CARREGAR_INFORMACOES_DO_CERTIFICADO + e.getMessage());
        }

        return certificado;
    }
   
    public static List<Certificado> listaCertificadosWindows() throws FiscalException {
        return listaCertificadosRepositorio("REPOSITORIO_WINDOWS");
    }
    
    private static List<Certificado> listaCertificadosRepositorio(String tipo) throws FiscalException {

        List<Certificado> listaCert = new ArrayList<>();
        Certificado cert = new Certificado();
        cert.setTipoCertificado(tipo);
        try {
            KeyStore ks = getKeyStore(cert);
            Enumeration<String> aliasEnum = ks.aliases();
            while (aliasEnum.hasMoreElements()) {
                String aliasKey = aliasEnum.nextElement();
                if (aliasKey != null) {
                    Certificado certificado = new Certificado();
                    certificado.setTipoCertificado(tipo);
                    certificado.setNome(aliasKey);
                    setDadosCertificado(certificado, ks);
                    listaCert.add(certificado);
                }
            }
        } catch (KeyStoreException ex) {
            throw new FiscalException("Erro ao Carregar Certificados:" + ex.getMessage());
        }
        return listaCert;
    }
    
    private static void setDadosCertificado(Certificado certificado, KeyStore keyStore) throws FiscalException, KeyStoreException {

        if (keyStore == null) {
            keyStore = getKeyStore(certificado);
            Enumeration<String> aliasEnum = keyStore.aliases();
            String aliasKey = aliasEnum.nextElement();
            certificado.setNome(aliasKey);
        }

        X509Certificate certificate = getCertificate(certificado, keyStore);
        certificado.setCnpjCpf(
                Optional.ofNullable(certificate.getExtensionValue("2.5.29.17"))
                        .flatMap(CertificadoService::getDocumentoFromCertificado)
                        .orElse(""));
        Date dataValidade = dataValidade(certificate);
        certificado.setVencimento(dataValidade.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        certificado.setDataHoraVencimento(dataValidade.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        certificado.setDiasRestantes(diasRestantes(certificado));
        certificado.setValido(valido(certificado));
        certificado.setNumeroSerie(certificate.getSerialNumber());
    }
    
    private static Date dataValidade(X509Certificate certificate) {
        return Optional.ofNullable(certificate.getNotAfter())
                .orElse(Date.from(LocalDate.of(2020, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }
    
    private static Long diasRestantes(Certificado certificado) {
        return LocalDate.now().until(certificado.getVencimento(), ChronoUnit.DAYS);
    }

    private static boolean valido(Certificado certificado) {
        return LocalDate.now().isBefore(certificado.getVencimento());
    }
    
    public static X509Certificate getCertificate(Certificado certificado, KeyStore keystore) throws FiscalException {
        try {

            return (X509Certificate) keystore.getCertificate(certificado.getNome());

        } catch (KeyStoreException e) {
            throw new FiscalException("Erro Ao pegar X509Certificate: " + e.getMessage());
        }
    }
    
    public static KeyStore getKeyStore(Certificado certificado) throws FiscalException {
        try {

            switch (certificado.getTipoCertificado()) {
                case "REPOSITORIO_WINDOWS":
                    return getKeyStoreRepositorioWindows();
                case "ARQUIVO":
                    return getKeyStoreArquivo(certificado);
                case "ARQUIVO_BYTES":
                    return getKeyStoreArquivoByte(certificado.getArquivoBytes(), certificado);
                default:
                    throw new FiscalException("Tipo de certificado não Configurado: " +
                            certificado.getTipoCertificado());
            }
        } catch (Exception e) {
            if (Optional.ofNullable(e.getMessage()).orElse("").startsWith("keystore password was incorrect"))
                throw new FiscalException("Senha do Certificado inválida.");

            e.printStackTrace();
            throw new FiscalException("Erro ao obter as informações do certificado");
        }
    }
    
    static KeyStore getKeyStoreRepositorioWindows() throws KeyStoreException, FiscalException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException, IOException {
        KeyStore keyStore;
        keyStore = KeyStore.getInstance("Windows-MY", "SunMSCAPI");
        keyStore.load(null, null);
        return keyStore;
    }
    
    static KeyStore getKeyStoreArquivo(Certificado certificado) throws FiscalException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        File file = new File(certificado.getArquivo());
        if (!file.exists()) {
            throw new FiscalException("Certificado Digital não Encontrado");
        }
        return getKeyStoreArquivoByte(Files.readAllBytes(file.toPath()), certificado);
    }
    
    static KeyStore getKeyStoreArquivoByte(byte[] certificado, Certificado certificado1) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (ByteArrayInputStream bs = new ByteArrayInputStream(certificado)) {
            keyStore.load(bs, certificado1.getSenha().toCharArray());
        }
        return keyStore;
    }
    
    public static Optional<String> getDocumentoFromCertificado(byte[] extensionValue) {
        String valor = new String(extensionValue);
        return Optional.ofNullable(
                processaCNPJ(valor)
                        .orElse(processaCPF(valor)
                                .orElse(null)));
    }
    
    private static Optional<String> processaCNPJ(String extensionValue) {
        int cnpjIndex = extensionValue.indexOf(CNPJ_INDICATOR);
        if (cnpjIndex != -1) {
            String cnpj = extrairNumeros(extensionValue.substring(cnpjIndex + CNPJ_OFFSET, cnpjIndex + CNPJ_LENGTH));
            return validarDocumento(cnpj);
        }
        return Optional.empty();
    }

    private static String extrairNumeros(String valor) {
        return valor.replaceAll("[^\\d]", "");
    }
    
    private static Optional<String> processaCPF(String extensionValue) {
        int cpfStartIndex = extensionValue.indexOf(CPF_INDICATOR);
        if (cpfStartIndex != -1) {
            cpfStartIndex += CPF_OFFSET;
            int cpfEndIndex = extensionValue.indexOf(CPF_TERMINATOR, cpfStartIndex);
            if (cpfEndIndex != -1) {
                String cpf = extrairNumeros(extensionValue.substring(cpfStartIndex, cpfStartIndex + CPF_LENGTH));
                return validarDocumento(cpf);
            }
        }
        return Optional.empty();
    }
    
    private static Optional<String> validarDocumento(String documento) {
        Matcher matcherCNPJ = PATTERN_CNPJ.matcher(documento);
        if (matcherCNPJ.find()) {
            return Optional.of(matcherCNPJ.group());
        }

        Matcher matcherCPF = PATTERN_CPF.matcher(documento);
        if (matcherCPF.find()) {
            return Optional.of(matcherCPF.group());
        }

        return Optional.empty();
    }
    
    public static <T> Optional<T> verifica(T obj) {
        if (obj == null)
            return Optional.empty();
        if (obj instanceof Collection)
            return ((Collection<?>) obj).size() == 0 ? Optional.empty() : Optional.of(obj);

        final String s = String.valueOf(obj).trim();

        return s.length() == 0 || s.equalsIgnoreCase("null") ? Optional.empty() : Optional.of(obj);
    }  
}