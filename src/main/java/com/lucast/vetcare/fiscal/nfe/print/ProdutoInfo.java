package com.lucast.vetcare.fiscal.nfe.print;


import java.math.BigDecimal;

public class ProdutoInfo {

	private String proCodigo;
	
	private String proDescricao;
	
	private String ncm;
	
	private String cst;
	
	private String cfop;
	
	private String unidade;
	
	private BigDecimal quantidade;
	
	private BigDecimal vlUnitario;
	
	private BigDecimal vlTotal;
	
	private BigDecimal bcIcms;
	
	private BigDecimal vlIcms;
	
	private BigDecimal vlIpi;
	
	private BigDecimal aliqIcms;
	
	private BigDecimal aliqIpi;
	
	private String infAdicional;

	public ProdutoInfo(String proCodigo, String proDescricao, String ncm, String cst, String cfop, String unidade,
			BigDecimal quantidade, BigDecimal vlUnitario, BigDecimal vlTotal, BigDecimal bcIcms, BigDecimal vlIcms,
			BigDecimal vlIpi, BigDecimal aliqIcms, BigDecimal aliqIpi, String infAdicional) {
		super();
		this.proCodigo = proCodigo;
		this.proDescricao = proDescricao;
		this.ncm = ncm;
		this.cst = cst;
		this.cfop = cfop;
		this.unidade = unidade;
		this.quantidade = quantidade;
		this.vlUnitario = vlUnitario;
		this.vlTotal = vlTotal;
		this.bcIcms = bcIcms;
		this.vlIcms = vlIcms;
		this.vlIpi = vlIpi;
		this.aliqIcms = aliqIcms;
		this.aliqIpi = aliqIpi;
		this.infAdicional = infAdicional;
	}

	public String getProCodigo() {
		return proCodigo;
	}

	public void setProCodigo(String proCodigo) {
		this.proCodigo = proCodigo;
	}

	public String getProDescricao() {
		return proDescricao;
	}

	public void setProDescricao(String proDescricao) {
		this.proDescricao = proDescricao;
	}

	public String getNcm() {
		return ncm;
	}

	public void setNcm(String ncm) {
		this.ncm = ncm;
	}

	public String getCst() {
		return cst;
	}

	public void setCst(String cst) {
		this.cst = cst;
	}

	public String getCfop() {
		return cfop;
	}

	public void setCfop(String cfop) {
		this.cfop = cfop;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public BigDecimal getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(BigDecimal quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getVlUnitario() {
		return vlUnitario;
	}

	public void setVlUnitario(BigDecimal vlUnitario) {
		this.vlUnitario = vlUnitario;
	}

	public BigDecimal getVlTotal() {
		return vlTotal;
	}

	public void setVlTotal(BigDecimal vlTotal) {
		this.vlTotal = vlTotal;
	}

	public BigDecimal getBcIcms() {
		return bcIcms;
	}

	public void setBcIcms(BigDecimal bcIcms) {
		this.bcIcms = bcIcms;
	}

	public BigDecimal getVlIcms() {
		return vlIcms;
	}

	public void setVlIcms(BigDecimal vlIcms) {
		this.vlIcms = vlIcms;
	}

	public BigDecimal getVlIpi() {
		return vlIpi;
	}

	public void setVlIpi(BigDecimal vlIpi) {
		this.vlIpi = vlIpi;
	}

	public BigDecimal getAliqIcms() {
		return aliqIcms;
	}

	public void setAliqIcms(BigDecimal aliqIcms) {
		this.aliqIcms = aliqIcms;
	}

	public BigDecimal getAliqIpi() {
		return aliqIpi;
	}

	public void setAliqIpi(BigDecimal aliqIpi) {
		this.aliqIpi = aliqIpi;
	}

	public String getInfAdicional() {
		return infAdicional;
	}

	public void setInfAdicional(String infAdicional) {
		this.infAdicional = infAdicional;
	}
}

