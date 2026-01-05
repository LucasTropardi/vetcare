package com.lucast.vetcare.fiscal.nfe.xml;

import java.util.List;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "enviNFe", namespace = "http://www.portalfiscal.inf.br/nfe")
public class EnviNFe {

    private String idLote;

    private String indSinc;
    
    private List<NFe> nFe;
    
    private String versao;
    
    @XmlElement(name = "idLote", namespace = "http://www.portalfiscal.inf.br/nfe")
	public String getIdLote() {
		return idLote;
	}
    
	public void setIdLote(String idLote) {
		this.idLote = idLote;
	}
	
	@XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
	public String getIndSinc() {
		return indSinc;
	}
	
	public void setIndSinc(String indSinc) {
		this.indSinc = indSinc;
	}
	
	@XmlElement(name = "NFe", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
	public List<NFe> getnFe() {
		return nFe;
	}
	
	public void setnFe(List<NFe> nFe) {
		this.nFe = nFe;
	}
	
	@XmlAttribute(name = "versao", required = true)
	public String getVersao() {
		return versao;
	}
	
	public void setVersao(String versao) {
		this.versao = versao;
	}
}