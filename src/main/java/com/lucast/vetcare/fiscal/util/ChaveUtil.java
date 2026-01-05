package com.lucast.vetcare.fiscal.util;

import java.time.LocalDateTime;

public class ChaveUtil {

	private static String chave;
	
	private static String digitoVerificador;
	
	private static FiscalUtil fiscalUtil = new FiscalUtil();
	
	public ChaveUtil() {
		
	}

	public static String montaChave(String uf, LocalDateTime dataEmissao, String cnpj, String modeloNf, String serieNF, String documento, String tipoEmissao) {
		chave = fiscalUtil.ufToCodUf(uf) +
				   fiscalUtil.formatDate(dataEmissao) +
				   fiscalUtil.removeString(cnpj, "./- ") +
				   modeloNf.trim() +
				   fiscalUtil.strZero(Integer.parseInt(serieNF.trim()), 3)   + 
				   fiscalUtil.strZero(Integer.parseInt(documento.trim()), 9) + 
				   tipoEmissao + 
				   fiscalUtil.strZero(Integer.parseInt(documento.trim()), 8);
				  
		chave = fiscalUtil.removeString(chave, "./- ");
		
		digitoVerificador = fiscalUtil.modulo11(chave);
		
		return chave + digitoVerificador;
	}

	public static String montaChaveCTe(String uf, LocalDateTime dataEmissao, String cnpj, String modeloNf, String serieNF, String documento, String tipoEmissao) {
		chave = fiscalUtil.ufToCodUf(uf) +
				fiscalUtil.formatDate(dataEmissao) +
				fiscalUtil.removeString(cnpj, "./- ") +
				modeloNf.trim() +
				fiscalUtil.strZero(Integer.parseInt(serieNF.trim()), 3)   +
				fiscalUtil.strZero(Integer.parseInt(documento.trim()), 9) +
				tipoEmissao +
				fiscalUtil.strZero(Integer.parseInt(documento.trim()) + 1, 8);

		chave = fiscalUtil.removeString(chave, "./- ");

		digitoVerificador = fiscalUtil.modulo11(chave);

		return chave + digitoVerificador;
	}

	public static String montaChaveNFCe(String uf, LocalDateTime dataEmissao, String cnpj, String modeloNf, String serieNF, String documento, String tipoEmissao) {
		chave = fiscalUtil.ufToCodUf(uf) +
				fiscalUtil.formatDate(dataEmissao) +
				fiscalUtil.removeString(cnpj, "./- ") +
				modeloNf.trim() +
				fiscalUtil.strZero(Integer.parseInt(serieNF.trim()), 3)   +
				fiscalUtil.strZero(Integer.parseInt(documento.trim()), 9) +
				fiscalUtil.strZero(Integer.parseInt(documento.trim()), 8)+
				tipoEmissao;

		chave = fiscalUtil.removeString(chave, "./- ");

		digitoVerificador = fiscalUtil.modulo11(chave);

		return chave + digitoVerificador;
	}
	
	public static String getChave() {
		return chave;
	}
	
	public static String getDigitoVerificador() {
		return digitoVerificador;
	}
}