package com.lucast.vetcare.fiscal.nfe.print;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.lucast.vetcare.fiscal.nfe.xml.NFeProc;
import com.lucast.vetcare.fiscal.util.FiscalUtil;

public class NFeCCePrint {
	
	private String xml;
	
	private String xmlEvento;
	
	private FiscalUtil fiscalUtil = new FiscalUtil();

	public NFeCCePrint(String xml, String xmlEventoCCe) {
		this.xml = xml;
		this.xmlEvento = xmlEventoCCe;
	}
	
	public String getDataEmissao() {
		String dhEmi =fiscalUtil.pegaTag(xml, "dhEmi");
		if (dhEmi != null && !dhEmi.isEmpty()) {
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
				LocalDate localDate = LocalDate.parse(dhEmi, formatter);
				return localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			} catch (DateTimeParseException e) {
				e.printStackTrace();
				return null;
			}
		}else {
			return null;
		}
	}
	
	public String getNumeroNf() {
		return fiscalUtil.pegaTag(xml, "nNF");
	}
	
	public String getModelo() {
		return fiscalUtil.pegaTag(xml, "mod");
	}
	
	public String getSerie() {
		return fiscalUtil.pegaTag(xml, "serie");
	}
	
	public String getCnpj() {
		String cnpj = fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "emit"), "CNPJ");
		return cnpj.substring(0, 2) + "." + cnpj.substring(2, 5) + "." + cnpj.substring(5, 8) + "/" + cnpj.substring(8, 12) + "-" + cnpj.substring(12);
	}
	
	public String getRazaoSocial() {
		return fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "emit"), "xNome");
	}
	
	public String getEndereco() {
		return fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "emit"), "xLgr")  + ", " + fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "emit"), "nro");
	}
	
	public String getBairro() {
		return fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "emit"), "xBairro");
	}
	
	public String getCidade() {
		return fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "emit"), "xMun");
	}
	
	public String getUf() {
		return fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "emit"), "UF");
	}
	
	public String getCep() {
		String cep = fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "emit"), "CEP");
		return cep.substring(0, 5) + "-" + cep.substring(5);
	}
	
	public String getTelefone() {
		return fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "emit"), "fone") != null ? fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "emit"), "fone") : "";
	}
	
	public String getInscricaoEstadual() {
		return fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "emit"), "IE");
	}
	
	public String getTipoAmbiente() {
		return fiscalUtil.pegaTag(xml, "toAmb");
	}
	
	public String getDataRegistro() {
		return fiscalUtil.pegaTag(xmlEvento, "dhEvento");
	}
	
	public String getOrgao() {
		return fiscalUtil.pegaTag(xmlEvento, "cOrgao");
	}
	
	public String getTipoEvento() {
		return fiscalUtil.pegaTag(xmlEvento, "tpEvento");
	}
	
	public String getSequencia() {
		return fiscalUtil.pegaTag(xmlEvento, "nSeqEvento");
	}
	
	public String getChave() {
		return fiscalUtil.pegaTag(xmlEvento, "chNFe");
	}
	
	public String getVersao() {
		return fiscalUtil.pegaTag(xmlEvento, "verEvento");
	}
	
	public String getDescricaoEvento() {
		return fiscalUtil.pegaTag(xmlEvento, "descEvento");
	}
	
	public String getCorrecao() {
		return fiscalUtil.pegaTag(xmlEvento, "xCorrecao");
	}
	
	public String getCondicaoUso() {
		return fiscalUtil.pegaTag(xmlEvento, "xCondUso");
	}
	
	public String getCstat() {
		return fiscalUtil.pegaTag(xmlEvento, "cStat");
	}
	
	public String getMotivo() {
		return fiscalUtil.pegaTag(xmlEvento, "xMotivo");
	}

	public String getProtocolo() {
		return  fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "protNFe"), "nProt");
	}
	
	public String getRazaoSocialDest() {
		return fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "dest"), "xNome");
	}

	public String getCnpjDest() {
		String cnpj = fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "dest"), "CNPJ");

		if (cnpj == null || cnpj.isEmpty()) {
			return "";
		}

		return cnpj.substring(0, 2) + "." + cnpj.substring(2, 5) + "." + cnpj.substring(5, 8) + "/" + cnpj.substring(8, 12) + "-" + cnpj.substring(12);
	}

	public String getCnpjCpfDest() {
		String tagDest = fiscalUtil.pegaTag(xml, "dest");
		String cnpj = fiscalUtil.pegaTag(tagDest, "CNPJ");

		// Se não encontrou CNPJ, busca CPF
		if (cnpj == null || cnpj.isEmpty()) {
			String cpf = fiscalUtil.pegaTag(tagDest, "CPF");
			if (cpf != null && !cpf.isEmpty()) {
				// Formata CPF: 000.000.000-00
				return cpf.substring(0, 3) + "." +
						cpf.substring(3, 6) + "." +
						cpf.substring(6, 9) + "-" +
						cpf.substring(9);
			}
			return "";
		}

		// Formata CNPJ: 00.000.000/0000-00
		return cnpj.substring(0, 2) + "." +
				cnpj.substring(2, 5) + "." +
				cnpj.substring(5, 8) + "/" +
				cnpj.substring(8, 12) + "-" +
				cnpj.substring(12);
	}
	
	public String getEnderecoDest() {
		return fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "dest"), "xLgr");
	}
	
	public String getEnderecoNumeroDest() {
		return fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "dest"), "nro");
	}
	
	public String getTelefoneDest() {
		return fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "dest"), "fone") != null ? fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "dest"), "fone") : "";
	}
	
	public String getBairroDest() {
		return fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "dest"), "xBairro");
	}
	
	public String getCidadeDest() {
		return fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "dest"), "xMun");
	}
	
	public String getInscricaoEstadualDest() {
		return fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "dest"), "IE");
	}
	
	public String getUfDest() {
		return fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "dest"), "UF");
	}

	public String getCepDest() {
		String cep = fiscalUtil.pegaTag(
				fiscalUtil.pegaTag(xml, "dest"),
				"CEP"
		);

		if (cep == null || cep.trim().isEmpty()) {
			return "";
		}

		cep = cep.replaceAll("\\D", "");

		if (cep.length() != 8) {
			return cep;
		}

		return cep.substring(0, 5) + "-" + cep.substring(5);
	}

}