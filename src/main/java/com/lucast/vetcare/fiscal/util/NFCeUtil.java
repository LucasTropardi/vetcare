package com.lucast.vetcare.fiscal.util;

import com.lucast.vetcare.fiscal.certificado.CertificadoService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class NFCeUtil {

    public static String getCodeQRCodeNFCe(String chave, String ambiente, String idToken, String CSC, String urlConsulta) throws Exception {

        StringBuilder value = new StringBuilder();
        value.append(chave);
        value.append("|").append("2");
        value.append("|").append(ambiente);
        value.append("|").append(Integer.valueOf(idToken));
        String cHashQRCode = getHexa(getHash(value.toString() + CSC)).toUpperCase();
//
//        return urlConsulta + "?p=" + value + "|" + cHashQRCode;

//        String cHashQRCode = generateHmacSHA1(value.toString(), CSC).toUpperCase();

        return urlConsulta + "?p=" + value + "|" + cHashQRCode;

    }

//    private static String generateHmacSHA1(String value, String key) throws Exception {
//        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
//        Mac mac = Mac.getInstance("HmacSHA1");
//        mac.init(signingKey);
//        byte[] rawHmac = mac.doFinal(value.getBytes());
//        return bytesToHex(rawHmac);
//    }
//
//    private static String bytesToHex(byte[] bytes) {
//        Formatter formatter = new Formatter();
//        for (byte b : bytes) {
//            formatter.format("%02x", b);
//        }
//        String result = formatter.toString();
//        formatter.close();
//        return result;
//    }

    public static String getCodeQRCodeContingencia(String chave, String ambiente, String dhEmi, String valorNF, String digVal, String idToken, String CSC, String urlConsulta) throws NoSuchAlgorithmException {

        StringBuilder value = new StringBuilder();
        value.append(chave);
        value.append("|").append("2");
        value.append("|").append(ambiente);
        value.append("|").append(dhEmi, 8, 10);
        value.append("|").append(valorNF);
        value.append("|").append(getHexa(digVal));
        value.append("|").append(Integer.valueOf(idToken));
        String cHashQRCode = getHexa(getHash(value.toString() + CSC)).toUpperCase();

        return urlConsulta + "?p=" + value + "|" + cHashQRCode;
    }

    public static byte[] geraHashCSRT(String chave, String csrt) throws NoSuchAlgorithmException {
        CertificadoService.verifica(chave).orElseThrow(() -> new InvalidParameterException("Chave não deve ser nula ou vazia"));
        CertificadoService.verifica(csrt).orElseThrow(() -> new InvalidParameterException("CSRT não deve ser nulo ou vazio"));
        if (chave.length() != 44) {
            throw new InvalidParameterException("Chave deve conter 44 caracteres.");
        }

        return getHash(csrt + chave);
    }

    private static byte[] getHash(String valor) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(valor.getBytes());
        return md.digest();
    }

    private static String getHexa(String valor) {
        return getHexa(valor.getBytes());
    }

    private static String getHexa(byte[] bytes) {
        StringBuilder s = new StringBuilder();
        for (byte aByte : bytes) {
            int parteAlta = ((aByte >> 4) & 0xf) << 4;
            int parteBaixa = aByte & 0xf;
            if (parteAlta == 0) {
                s.append('0');
            }
            s.append(Integer.toHexString(parteAlta | parteBaixa));
        }
        return s.toString();
    }
}
