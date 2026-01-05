package com.lucast.vetcare.fiscal.enums;

public enum ServicesMDFe {

	ENVIO_MDFE("mdfe_v3.00.xsd"),
	CONSULTA_XML("consSitMDFe_v3.00.xsd"),
	RETORNO_RECEPCAO("consReciMDFe_v3.00.xsd"),
	CANCELAMENTO("evCancMDFe_v3.00.xsd"),
	ENCERRAR("evEncMDFe_v3.00.xsd"),
	EVENTO("eventoMDFe_v3.00.xsd");
	
	private final String xds;
	
	ServicesMDFe(String xds) {
		this.xds = xds;
	}
	
	public String getXds() {
		return xds;
	}
	
	public static ServicesMDFe getService(String service) {
        for (ServicesMDFe e : values()) {
            if (e.name().equals(service)) return e;
        }
        throw new IllegalArgumentException();
    }
}