package com.lucast.vetcare.fiscal.certificado;


import java.math.BigInteger;
import java.security.Provider;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import com.lucast.vetcare.fiscal.exception.FiscalException;

public class Certificado {
	
	private static final String TLSV_1_2 = "TLSv1.2";

    private String nome;
    private LocalDate vencimento;
    private LocalDateTime dataHoraVencimento;
    private Long diasRestantes;
    private String arquivo;
    private byte[] arquivoBytes;
    private String senha;
    private String cnpjCpf;
    private String tipoCertificado;
    private boolean valido;
    private String sslProtocol;
    private BigInteger numeroSerie;
    private Provider provider;
    
    public Certificado() {
        this.setSslProtocol(TLSV_1_2);
    }
    
    public Certificado getCertificado(byte[] certificado, String senha) throws FiscalException, Exception {
    	Certificado cert = CertificadoService.certificadoPfxBytes(certificado, senha);
		return cert;
    }

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public LocalDate getVencimento() {
		return vencimento;
	}

	public void setVencimento(LocalDate vencimento) {
		this.vencimento = vencimento;
	}

	public LocalDateTime getDataHoraVencimento() {
		return dataHoraVencimento;
	}

	public void setDataHoraVencimento(LocalDateTime dataHoraVencimento) {
		this.dataHoraVencimento = dataHoraVencimento;
	}

	public Long getDiasRestantes() {
		return diasRestantes;
	}

	public void setDiasRestantes(Long diasRestantes) {
		this.diasRestantes = diasRestantes;
	}

	public String getArquivo() {
		return arquivo;
	}

	public void setArquivo(String arquivo) {
		this.arquivo = arquivo;
	}

	public byte[] getArquivoBytes() {
		return arquivoBytes;
	}

	public void setArquivoBytes(byte[] arquivoBytes) {
		this.arquivoBytes = arquivoBytes;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getCnpjCpf() {
		return cnpjCpf;
	}

	public void setCnpjCpf(String cnpjCpf) {
		this.cnpjCpf = cnpjCpf;
	}

	public String getTipoCertificado() {
		return tipoCertificado;
	}

	public void setTipoCertificado(String tipoCertificado) {
		this.tipoCertificado = tipoCertificado;
	}

	public boolean isValido() {
		return valido;
	}

	public void setValido(boolean valido) {
		this.valido = valido;
	}

	public String getSslProtocol() {
		return sslProtocol;
	}

	public void setSslProtocol(String sslProtocol) {
		this.sslProtocol = sslProtocol;
	}

	public BigInteger getNumeroSerie() {
		return numeroSerie;
	}

	public void setNumeroSerie(BigInteger numeroSerie) {
		this.numeroSerie = numeroSerie;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public static String getTlsv12() {
		return TLSV_1_2;
	}

	@Override
	public String toString() {
		return "Certificado [nome=" + nome + ", vencimento=" + vencimento + ", dataHoraVencimento=" + dataHoraVencimento
				+ ", diasRestantes=" + diasRestantes + ", arquivo=" + arquivo + ", arquivoBytes="
				+ Arrays.toString(arquivoBytes) + ", senha=" + senha + ", cnpjCpf=" + cnpjCpf + ", tipoCertificado="
				+ tipoCertificado + ", valido=" + valido + ", sslProtocol=" + sslProtocol + ", numeroSerie="
				+ numeroSerie + ", provider=" + provider + "]";
	}
}