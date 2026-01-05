package com.lucast.vetcare.fiscal.nfe.xml;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.*;

import com.lucast.vetcare.fiscal.nfe.xml.location.EnderEmi;
import com.lucast.vetcare.fiscal.nfe.xml.location.Endereco;
import com.lucast.vetcare.fiscal.nfe.xml.location.Local;
import com.lucast.vetcare.fiscal.nfe.xml.location.Uf;
import com.lucast.vetcare.fiscal.nfe.xml.location.UfEmi;
import com.lucast.vetcare.fiscal.nfe.xml.location.Veiculo;
import com.lucast.vetcare.fiscal.nfe.xml.signature.SignatureType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NFe", namespace = "http://www.portalfiscal.inf.br/nfe", propOrder = {
    "infNFe",
    "infNFeSupl",
    "signature"
})
@XmlRootElement(name = "NFe",namespace = "http://www.portalfiscal.inf.br/nfe")
public class NFe {

    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
    protected NFe.InfNFe infNFe;
    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
    protected NFe.InfNFeSupl infNFeSupl;
    @XmlElement(name = "Signature", namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected SignatureType signature;
    @XmlAttribute(name = "xmlns", required = true)
    protected String xmlns;

    public NFe.InfNFe getInfNFe() {
        return infNFe;
    }

    public void setInfNFe(NFe.InfNFe value) {
        this.infNFe = value;
    }

    public NFe.InfNFeSupl getInfNFeSupl() {
        return infNFeSupl;
    }

    public void setInfNFeSupl(NFe.InfNFeSupl value) {
        this.infNFeSupl = value;
    }

    public SignatureType getSignature() {
        return signature;
    }

    public void setSignature(SignatureType value) {
        this.signature = value;
    }

	public String getXmlns() {
		return xmlns;
	}

	public void setXmlns(String xmlns) {
		this.xmlns = xmlns;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "ide",
        "emit",
        "avulsa",
        "dest",
        "retirada",
        "entrega",
        "autXML",
        "det",
        "total",
        "transp",
        "cobr",
        "pag",
        "infIntermed",
        "infAdic",
        "exporta",
        "compra",
        "cana",
        "infRespTec",
        "infSolicNFF"
    })
    public static class InfNFe {

        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
        protected NFe.InfNFe.Ide ide;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
        protected NFe.InfNFe.Emit emit;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
        protected NFe.InfNFe.Avulsa avulsa;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
        protected NFe.InfNFe.Dest dest;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
        protected Local retirada;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
        protected Local entrega;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
        protected List<NFe.InfNFe.AutXML> autXML;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
        protected List<NFe.InfNFe.Det> det;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
        protected NFe.InfNFe.Total total;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
        protected NFe.InfNFe.Transp transp;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
        protected NFe.InfNFe.Cobr cobr;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
        protected NFe.InfNFe.Pag pag;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
        protected NFe.InfNFe.InfIntermed infIntermed;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
        protected NFe.InfNFe.InfAdic infAdic;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
        protected NFe.InfNFe.Exporta exporta;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
        protected NFe.InfNFe.Compra compra;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
        protected NFe.InfNFe.Cana cana;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
        protected InfRespTec infRespTec;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
        protected NFe.InfNFe.InfSolicNFF infSolicNFF;
        @XmlAttribute(name = "versao", required = true)
        protected String versao;
        @XmlAttribute(name = "Id", required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        protected String id;

        public NFe.InfNFe.Ide getIde() {
            return ide;
        }

        public void setIde(NFe.InfNFe.Ide value) {
            this.ide = value;
        }

        public NFe.InfNFe.Emit getEmit() {
            return emit;
        }

        public void setEmit(NFe.InfNFe.Emit value) {
            this.emit = value;
        }

        public NFe.InfNFe.Avulsa getAvulsa() {
            return avulsa;
        }

        public void setAvulsa(NFe.InfNFe.Avulsa value) {
            this.avulsa = value;
        }

        public NFe.InfNFe.Dest getDest() {
            return dest;
        }

        public void setDest(NFe.InfNFe.Dest value) {
            this.dest = value;
        }

        public Local getRetirada() {
            return retirada;
        }

        public void setRetirada(Local value) {
            this.retirada = value;
        }

        public Local getEntrega() {
            return entrega;
        }

        public void setEntrega(Local value) {
            this.entrega = value;
        }

		public List<NFe.InfNFe.AutXML> getAutXML() {
            if (autXML == null) {
                autXML = new ArrayList<NFe.InfNFe.AutXML>();
            }
            return this.autXML;
        }
		
        public void setAutXML(List<NFe.InfNFe.AutXML> autXML) {
			this.autXML = autXML;
		}

        public List<NFe.InfNFe.Det> getDet() {
            if (det == null) {
                det = new ArrayList<NFe.InfNFe.Det>();
            }
            return this.det;
        }
        
		public void setDet(List<NFe.InfNFe.Det> det) {
			this.det = det;
		}
   
        public NFe.InfNFe.Total getTotal() {
            return total;
        }

        public void setTotal(NFe.InfNFe.Total value) {
            this.total = value;
        }

        public NFe.InfNFe.Transp getTransp() {
            return transp;
        }

        public void setTransp(NFe.InfNFe.Transp value) {
            this.transp = value;
        }

        public NFe.InfNFe.Cobr getCobr() {
            return cobr;
        }

        public void setCobr(NFe.InfNFe.Cobr value) {
            this.cobr = value;
        }

        public NFe.InfNFe.Pag getPag() {
            return pag;
        }

        public void setPag(NFe.InfNFe.Pag value) {
            this.pag = value;
        }

        public NFe.InfNFe.InfIntermed getInfIntermed() {
            return infIntermed;
        }

        public void setInfIntermed(NFe.InfNFe.InfIntermed value) {
            this.infIntermed = value;
        }

        public NFe.InfNFe.InfAdic getInfAdic() {
            return infAdic;
        }

        public void setInfAdic(NFe.InfNFe.InfAdic value) {
            this.infAdic = value;
        }

        public NFe.InfNFe.Exporta getExporta() {
            return exporta;
        }

        public void setExporta(NFe.InfNFe.Exporta value) {
            this.exporta = value;
        }

        public NFe.InfNFe.Compra getCompra() {
            return compra;
        }

        public void setCompra(NFe.InfNFe.Compra value) {
            this.compra = value;
        }

        public NFe.InfNFe.Cana getCana() {
            return cana;
        }

        public void setCana(NFe.InfNFe.Cana value) {
            this.cana = value;
        }

        public InfRespTec getInfRespTec() {
            return infRespTec;
        }

        public void setInfRespTec(InfRespTec value) {
            this.infRespTec = value;
        }

        public NFe.InfNFe.InfSolicNFF getInfSolicNFF() {
            return infSolicNFF;
        }

        public void setInfSolicNFF(NFe.InfNFe.InfSolicNFF value) {
            this.infSolicNFF = value;
        }
        
        public String getVersao() {
            return versao;
        }

        public void setVersao(String value) {
            this.versao = value;
        }

        public String getId() {
            return id;
        }

        public void setId(String value) {
            this.id = value;
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "cnpj",
            "cpf"
        })
        public static class AutXML {

            @XmlElement(name = "CNPJ", namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String cnpj;
            @XmlElement(name = "CPF", namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String cpf;

            public String getCNPJ() {
                return cnpj;
            }

            public void setCNPJ(String value) {
                this.cnpj = value;
            }

            public String getCPF() {
                return cpf;
            }

            public void setCPF(String value) {
                this.cpf = value;
            }
        }

        
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "cnpj",
            "xOrgao",
            "matr",
            "xAgente",
            "fone",
            "uf",
            "ndar",
            "dEmi",
            "vdar",
            "repEmi",
            "dPag"
        })
        public static class Avulsa {

            @XmlElement(name = "CNPJ", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String cnpj;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String xOrgao;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String matr;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String xAgente;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String fone;
            @XmlElement(name = "UF", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            @XmlSchemaType(name = "string")
            protected UfEmi uf;
            @XmlElement(name = "nDAR", namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String ndar;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String dEmi;
            @XmlElement(name = "vDAR", namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String vdar;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String repEmi;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String dPag;

            public String getCNPJ() {
                return cnpj;
            }

            public void setCNPJ(String value) {
                this.cnpj = value;
            }

            public String getXOrgao() {
                return xOrgao;
            }

            public void setXOrgao(String value) {
                this.xOrgao = value;
            }

            public String getMatr() {
                return matr;
            }

            public void setMatr(String value) {
                this.matr = value;
            }

            public String getXAgente() {
                return xAgente;
            }

            public void setXAgente(String value) {
                this.xAgente = value;
            }

            public String getFone() {
                return fone;
            }

            public void setFone(String value) {
                this.fone = value;
            }

            public UfEmi getUF() {
                return uf;
            }

            public void setUF(UfEmi value) {
                this.uf = value;
            }

            public String getNDAR() {
                return ndar;
            }

            public void setNDAR(String value) {
                this.ndar = value;
            }

            public String getDEmi() {
                return dEmi;
            }

            public void setDEmi(String value) {
                this.dEmi = value;
            }

            public String getVDAR() {
                return vdar;
            }

            public void setVDAR(String value) {
                this.vdar = value;
            }

            public String getRepEmi() {
                return repEmi;
            }

            public void setRepEmi(String value) {
                this.repEmi = value;
            }

            public String getDPag() {
                return dPag;
            }

            public void setDPag(String value) {
                this.dPag = value;
            }
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "safra",
            "ref",
            "forDia",
            "qTotMes",
            "qTotAnt",
            "qTotGer",
            "deduc",
            "vFor",
            "vTotDed",
            "vLiqFor"
        })
        public static class Cana {

            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String safra;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String ref;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected List<NFe.InfNFe.Cana.ForDia> forDia;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String qTotMes;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String qTotAnt;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String qTotGer;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected List<NFe.InfNFe.Cana.Deduc> deduc;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String vFor;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String vTotDed;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String vLiqFor;

            public String getSafra() {
                return safra;
            }

            public void setSafra(String value) {
                this.safra = value;
            }

            public String getRef() {
                return ref;
            }

            public void setRef(String value) {
                this.ref = value;
            }

            public List<NFe.InfNFe.Cana.ForDia> getForDia() {
                if (forDia == null) {
                    forDia = new ArrayList<NFe.InfNFe.Cana.ForDia>();
                }
                return this.forDia;
            }

            public String getQTotMes() {
                return qTotMes;
            }
            
            public void setQTotMes(String value) {
                this.qTotMes = value;
            }
            
            public String getQTotAnt() {
                return qTotAnt;
            }
            
            public void setQTotAnt(String value) {
                this.qTotAnt = value;
            }

            public String getQTotGer() {
                return qTotGer;
            }
            
            public void setQTotGer(String value) {
                this.qTotGer = value;
            }

            public List<NFe.InfNFe.Cana.Deduc> getDeduc() {
                if (deduc == null) {
                    deduc = new ArrayList<NFe.InfNFe.Cana.Deduc>();
                }
                return this.deduc;
            }

            
            public String getVFor() {
                return vFor;
            }

            public void setVFor(String value) {
                this.vFor = value;
            }

            public String getVTotDed() {
                return vTotDed;
            }
            
            public void setVTotDed(String value) {
                this.vTotDed = value;
            }

            public String getVLiqFor() {
                return vLiqFor;
            }

            public void setVLiqFor(String value) {
                this.vLiqFor = value;
            }


            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "xDed",
                "vDed"
            })
            public static class Deduc {

                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String xDed;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vDed;

                public String getXDed() {
                    return xDed;
                }

                public void setXDed(String value) {
                    this.xDed = value;
                }
                
                public String getVDed() {
                    return vDed;
                }
                
                public void setVDed(String value) {
                    this.vDed = value;
                }

            }


            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "qtde"
            })
            public static class ForDia {

                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String qtde;
                @XmlAttribute(name = "dia", required = true)
                protected String dia;

                public String getQtde() {
                    return qtde;
                }
                
                public void setQtde(String value) {
                    this.qtde = value;
                }
                
                public String getDia() {
                    return dia;
                }
                
                public void setDia(String value) {
                    this.dia = value;
                }

            }

        }

        
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "fat",
            "dup"
        })
        public static class Cobr {

            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected NFe.InfNFe.Cobr.Fat fat;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected List<NFe.InfNFe.Cobr.Dup> dup;
            
            public NFe.InfNFe.Cobr.Fat getFat() {
                return fat;
            }

            public void setFat(NFe.InfNFe.Cobr.Fat value) {
                this.fat = value;
            }

            public List<NFe.InfNFe.Cobr.Dup> getDup() {
                if (dup == null) {
                    dup = new ArrayList<NFe.InfNFe.Cobr.Dup>();
                }
                return this.dup;
            }

            public void setDup(List<NFe.InfNFe.Cobr.Dup> dup) {
				this.dup = dup;
			}


			@XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "nDup",
                "dVenc",
                "vDup"
            })
            public static class Dup {

                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String nDup;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String dVenc;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vDup;

                public String getNDup() {
                    return nDup;
                }

                public void setNDup(String value) {
                    this.nDup = value;
                }
                
                public String getDVenc() {
                    return dVenc;
                }
                
                public void setDVenc(String value) {
                    this.dVenc = value;
                }
                
                public String getVDup() {
                    return vDup;
                }
                
                public void setVDup(String value) {
                    this.vDup = value;
                }

            }

            
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "nFat",
                "vOrig",
                "vDesc",
                "vLiq"
            })
            public static class Fat {

                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String nFat;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vOrig;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vDesc;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vLiq;

                public String getNFat() {
                    return nFat;
                }

                public void setNFat(String value) {
                    this.nFat = value;
                }

                public String getVOrig() {
                    return vOrig;
                }

                public void setVOrig(String value) {
                    this.vOrig = value;
                }

                public String getVDesc() {
                    return vDesc;
                }

                public void setVDesc(String value) {
                    this.vDesc = value;
                }

                public String getVLiq() {
                    return vLiq;
                }
                
                public void setVLiq(String value) {
                    this.vLiq = value;
                }

            }

        }

        
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "xnEmp",
            "xPed",
            "xCont"
        })
        public static class Compra {

            @XmlElement(name = "xNEmp", namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String xnEmp;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String xPed;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String xCont;

            public String getXNEmp() {
                return xnEmp;
            }

            public void setXNEmp(String value) {
                this.xnEmp = value;
            }

            public String getXPed() {
                return xPed;
            }
            
            public void setXPed(String value) {
                this.xPed = value;
            }
            
            public String getXCont() {
                return xCont;
            }
            
            public void setXCont(String value) {
                this.xCont = value;
            }

        }


        
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "cnpj",
            "cpf",
            "idEstrangeiro",
            "xNome",
            "enderDest",
            "indIEDest",
            "ie",
            "isuf",
            "im",
            "email"
        })
        public static class Dest {

            @XmlElement(name = "CNPJ", namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String cnpj;
            @XmlElement(name = "CPF", namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String cpf;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String idEstrangeiro;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String xNome;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected Endereco enderDest;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String indIEDest;
            @XmlElement(name = "IE", namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String ie;
            @XmlElement(name = "ISUF", namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String isuf;
            @XmlElement(name = "IM", namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String im;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String email;

            public String getCNPJ() {
                return cnpj;
            }

            public void setCNPJ(String value) {
                this.cnpj = value;
            }
            
            public String getCPF() {
                return cpf;
            }

            public void setCPF(String value) {
                this.cpf = value;
            }

            public String getIdEstrangeiro() {
                return idEstrangeiro;
            }

            public void setIdEstrangeiro(String value) {
                this.idEstrangeiro = value;
            }
            
            public String getXNome() {
                return xNome;
            }

            public void setXNome(String value) {
                this.xNome = value;
            }

            public Endereco getEnderDest() {
                return enderDest;
            }

            public void setEnderDest(Endereco value) {
                this.enderDest = value;
            }

            public String getIndIEDest() {
                return indIEDest;
            }
            
            public void setIndIEDest(String value) {
                this.indIEDest = value;
            }

            public String getIE() {
                return ie;
            }
            
            public void setIE(String value) {
                this.ie = value;
            }

            public String getISUF() {
                return isuf;
            }
            
            public void setISUF(String value) {
                this.isuf = value;
            }

            public String getIM() {
                return im;
            }
            
            public void setIM(String value) {
                this.im = value;
            }
            
            public String getEmail() {
                return email;
            }

            public void setEmail(String value) {
                this.email = value;
            }

        }


        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "prod",
            "imposto",
            "impostoDevol",
            "infAdProd",
            "obsItem"
        })
        public static class Det {

            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected NFe.InfNFe.Det.Prod prod;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected NFe.InfNFe.Det.Imposto imposto;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected NFe.InfNFe.Det.ImpostoDevol impostoDevol;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String infAdProd;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected NFe.InfNFe.Det.ObsItem obsItem;
            @XmlAttribute(name = "nItem", required = true)
            protected String nItem;
            
            public NFe.InfNFe.Det.Prod getProd() {
                return prod;
            }
            
            public void setProd(NFe.InfNFe.Det.Prod value) {
                this.prod = value;
            }

            public NFe.InfNFe.Det.Imposto getImposto() {
                return imposto;
            }
            
            public void setImposto(NFe.InfNFe.Det.Imposto value) {
                this.imposto = value;
            }
            
            public NFe.InfNFe.Det.ImpostoDevol getImpostoDevol() {
                return impostoDevol;
            }
            
            public void setImpostoDevol(NFe.InfNFe.Det.ImpostoDevol value) {
                this.impostoDevol = value;
            }
            
            public String getInfAdProd() {
                return infAdProd;
            }
            
            public void setInfAdProd(String value) {
                this.infAdProd = value;
            }

            public NFe.InfNFe.Det.ObsItem getObsItem() {
                return obsItem;
            }
            
            public void setObsItem(NFe.InfNFe.Det.ObsItem value) {
                this.obsItem = value;
            }
            
            public String getNItem() {
                return nItem;
            }
            
            public void setNItem(String value) {
                this.nItem = value;
            }

            
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "vTotTrib",
                "II",
                "COFINSST",
                "ICMS",
                "IPI",
                "ISSQN",
                "PIS",
                "COFINS",
                "ICMSUFDest"
            })
            public static class Imposto {
            	
            	@XmlElement(name = "vTotTrib", namespace = "http://www.portalfiscal.inf.br/nfe", required = false)
            	protected String vTotTrib;
            	@XmlElement(name = "II", namespace = "http://www.portalfiscal.inf.br/nfe")
            	protected NFe.InfNFe.Det.Imposto.II II;
            	@XmlElement(name = "COFINSST", namespace = "http://www.portalfiscal.inf.br/nfe", required = false)
            	protected NFe.InfNFe.Det.Imposto.COFINSST COFINSST;
            	@XmlElement(name = "ICMS", namespace = "http://www.portalfiscal.inf.br/nfe")
            	protected NFe.InfNFe.Det.Imposto.ICMS ICMS;
            	@XmlElement(name = "IPI", namespace = "http://www.portalfiscal.inf.br/nfe")
            	protected NFe.InfNFe.Det.Imposto.IPI IPI;  
            	@XmlElement(name = "ISSQN", namespace = "http://www.portalfiscal.inf.br/nfe")
            	protected NFe.InfNFe.Det.Imposto.ISSQN ISSQN;  
            	@XmlElement(name = "PIS", namespace = "http://www.portalfiscal.inf.br/nfe")
            	protected NFe.InfNFe.Det.Imposto.PIS PIS;     	
            	@XmlElement(name = "COFINS", namespace = "http://www.portalfiscal.inf.br/nfe")
            	protected NFe.InfNFe.Det.Imposto.COFINS COFINS;         	
            	@XmlElement(name = "ICMSUFDest", namespace = "http://www.portalfiscal.inf.br/nfe", required = false)
            	protected NFe.InfNFe.Det.Imposto.ICMSUFDest ICMSUFDest;

                public String getvTotTrib() {
					return vTotTrib;
				}

				public void setvTotTrib(String vTotTrib) {
					this.vTotTrib = vTotTrib;
				}

				public NFe.InfNFe.Det.Imposto.II getII() {
					return II;
				}

				public void setII(NFe.InfNFe.Det.Imposto.II iI) {
					II = iI;
				}

				public NFe.InfNFe.Det.Imposto.COFINSST getCOFINSST() {
					return COFINSST;
				}

				public void setCOFINSST(NFe.InfNFe.Det.Imposto.COFINSST cOFINSST) {
					COFINSST = cOFINSST;
				}

				public NFe.InfNFe.Det.Imposto.ICMS getICMS() {
					return ICMS;
				}

				public void setICMS(NFe.InfNFe.Det.Imposto.ICMS iCMS) {
					ICMS = iCMS;
				}

				public NFe.InfNFe.Det.Imposto.IPI getIPI() {
					return IPI;
				}

				public void setIPI(NFe.InfNFe.Det.Imposto.IPI iPI) {
					IPI = iPI;
				}

				public NFe.InfNFe.Det.Imposto.ISSQN getISSQN() {
					return ISSQN;
				}

				public void setISSQN(NFe.InfNFe.Det.Imposto.ISSQN iSSQN) {
					ISSQN = iSSQN;
				}

				public NFe.InfNFe.Det.Imposto.ICMSUFDest getICMSUFDest() {
					return ICMSUFDest;
				}

				public void setICMSUFDest(NFe.InfNFe.Det.Imposto.ICMSUFDest iCMSUFDest) {
					ICMSUFDest = iCMSUFDest;
				}

				public NFe.InfNFe.Det.Imposto.PIS getPIS() {
					return PIS;
				}

				public void setPIS(NFe.InfNFe.Det.Imposto.PIS pIS) {
					PIS = pIS;
				}

				public NFe.InfNFe.Det.Imposto.COFINS getCOFINS() {
					return COFINS;
				}

				public void setCOFINS(NFe.InfNFe.Det.Imposto.COFINS cOFINS) {
					COFINS = cOFINS;
				}

				@XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "cofinsAliq",
                    "cofinsQtde",
                    "cofinsnt",
                    "cofinsOutr"
                })
                public static class COFINS {

                    @XmlElement(name = "COFINSAliq", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.COFINS.COFINSAliq cofinsAliq;
                    @XmlElement(name = "COFINSQtde", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.COFINS.COFINSQtde cofinsQtde;
                    @XmlElement(name = "COFINSNT", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.COFINS.COFINSNT cofinsnt;
                    @XmlElement(name = "COFINSOutr", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.COFINS.COFINSOutr cofinsOutr;
                    
                    public NFe.InfNFe.Det.Imposto.COFINS.COFINSAliq getCOFINSAliq() {
                        return cofinsAliq;
                    }

                    public void setCOFINSAliq(NFe.InfNFe.Det.Imposto.COFINS.COFINSAliq value) {
                        this.cofinsAliq = value;
                    }

                    public NFe.InfNFe.Det.Imposto.COFINS.COFINSQtde getCOFINSQtde() {
                        return cofinsQtde;
                    }

                    public void setCOFINSQtde(NFe.InfNFe.Det.Imposto.COFINS.COFINSQtde value) {
                        this.cofinsQtde = value;
                    }

                    public NFe.InfNFe.Det.Imposto.COFINS.COFINSNT getCOFINSNT() {
                        return cofinsnt;
                    }

                    public void setCOFINSNT(NFe.InfNFe.Det.Imposto.COFINS.COFINSNT value) {
                        this.cofinsnt = value;
                    }

                    public NFe.InfNFe.Det.Imposto.COFINS.COFINSOutr getCOFINSOutr() {
                        return cofinsOutr;
                    }
                    
                    public void setCOFINSOutr(NFe.InfNFe.Det.Imposto.COFINS.COFINSOutr value) {
                        this.cofinsOutr = value;
                    }

                    
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "cst",
                        "vbc",
                        "pcofins",
                        "vcofins"
                    })
                    public static class COFINSAliq {

                        @XmlElement(name = "CST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String cst;
                        @XmlElement(name = "vBC", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vbc;
                        @XmlElement(name = "pCOFINS", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String pcofins;
                        @XmlElement(name = "vCOFINS", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vcofins;

                        public String getCST() {
                            return cst;
                        }

                        public void setCST(String value) {
                            this.cst = value;
                        }
                        
                        public String getVBC() {
                            return vbc;
                        }

                        public void setVBC(String value) {
                            this.vbc = value;
                        }

                        public String getPCOFINS() {
                            return pcofins;
                        }
                        
                        public void setPCOFINS(String value) {
                            this.pcofins = value;
                        }

                        public String getVCOFINS() {
                            return vcofins;
                        }
                        
                        public void setVCOFINS(String value) {
                            this.vcofins = value;
                        }

                    }

                    
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "cst"
                    })
                    public static class COFINSNT {

                        @XmlElement(name = "CST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String cst;

                        
                        public String getCST() {
                            return cst;
                        }

                        
                        public void setCST(String value) {
                            this.cst = value;
                        }

                    }

                    
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "cst",
                        "vbc",
                        "pcofins",
                        "qbcProd",
                        "vAliqProd",
                        "vcofins"
                    })
                    public static class COFINSOutr {

                        @XmlElement(name = "CST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String cst;
                        @XmlElement(name = "vBC", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbc;
                        @XmlElement(name = "pCOFINS", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pcofins;
                        @XmlElement(name = "qBCProd", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String qbcProd;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vAliqProd;
                        @XmlElement(name = "vCOFINS", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vcofins;

                        
                        public String getCST() {
                            return cst;
                        }

                        public void setCST(String value) {
                            this.cst = value;
                        }
                        
                        public String getVBC() {
                            return vbc;
                        }
                        
                        public void setVBC(String value) {
                            this.vbc = value;
                        }

                        public String getPCOFINS() {
                            return pcofins;
                        }

                        public void setPCOFINS(String value) {
                            this.pcofins = value;
                        }

                        public String getQBCProd() {
                            return qbcProd;
                        }

                        public void setQBCProd(String value) {
                            this.qbcProd = value;
                        }

                        public String getVAliqProd() {
                            return vAliqProd;
                        }

                        public void setVAliqProd(String value) {
                            this.vAliqProd = value;
                        }

                        public String getVCOFINS() {
                            return vcofins;
                        }

                        public void setVCOFINS(String value) {
                            this.vcofins = value;
                        }

                    }


                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "cst",
                        "qbcProd",
                        "vAliqProd",
                        "vcofins"
                    })
                    public static class COFINSQtde {

                        @XmlElement(name = "CST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String cst;
                        @XmlElement(name = "qBCProd", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String qbcProd;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vAliqProd;
                        @XmlElement(name = "vCOFINS", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vcofins;
                        
                        public String getCST() {
                            return cst;
                        }
                        
                        public void setCST(String value) {
                            this.cst = value;
                        }
                        
                        public String getQBCProd() {
                            return qbcProd;
                        }
                        
                        public void setQBCProd(String value) {
                            this.qbcProd = value;
                        }
                        
                        public String getVAliqProd() {
                            return vAliqProd;
                        }
                        
                        public void setVAliqProd(String value) {
                            this.vAliqProd = value;
                        }

                        public String getVCOFINS() {
                            return vcofins;
                        }
                        
                        public void setVCOFINS(String value) {
                            this.vcofins = value;
                        }

                    }

                }

                
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "vbc",
                    "pcofins",
                    "qbcProd",
                    "vAliqProd",
                    "vcofins",
                    "indSomaCOFINSST"
                })
                public static class COFINSST {

                    @XmlElement(name = "vBC", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String vbc;
                    @XmlElement(name = "pCOFINS", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String pcofins;
                    @XmlElement(name = "qBCProd", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String qbcProd;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String vAliqProd;
                    @XmlElement(name = "vCOFINS", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String vcofins;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String indSomaCOFINSST;
                    
                    public String getVBC() {
                        return vbc;
                    }
                    
                    public void setVBC(String value) {
                        this.vbc = value;
                    }

                    public String getPCOFINS() {
                        return pcofins;
                    }

                    public void setPCOFINS(String value) {
                        this.pcofins = value;
                    }
                    
                    public String getQBCProd() {
                        return qbcProd;
                    }

                    public void setQBCProd(String value) {
                        this.qbcProd = value;
                    }

                    public String getVAliqProd() {
                        return vAliqProd;
                    }
                    
                    public void setVAliqProd(String value) {
                        this.vAliqProd = value;
                    }
                    
                    public String getVCOFINS() {
                        return vcofins;
                    }

                    public void setVCOFINS(String value) {
                        this.vcofins = value;
                    }
                    
                    public String getIndSomaCOFINSST() {
                        return indSomaCOFINSST;
                    }

                    public void setIndSomaCOFINSST(String value) {
                        this.indSomaCOFINSST = value;
                    }

                }

                
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "icms00",
                    "icms02",
                    "icms10",
                    "icms15",
                    "icms20",
                    "icms30",
                    "icms40",
                    "icms51",
                    "icms53",
                    "icms60",
                    "icms61",
                    "icms70",
                    "icms90",
                    "icmsPart",
                    "icmsst",
                    "icmssn101",
                    "icmssn102",
                    "icmssn201",
                    "icmssn202",
                    "icmssn500",
                    "icmssn900"
                })
                public static class ICMS {

                    @XmlElement(name = "ICMS00", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.ICMS.ICMS00 icms00;
                    @XmlElement(name = "ICMS02", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.ICMS.ICMS02 icms02;
                    @XmlElement(name = "ICMS10", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.ICMS.ICMS10 icms10;
                    @XmlElement(name = "ICMS15", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.ICMS.ICMS15 icms15;
                    @XmlElement(name = "ICMS20", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.ICMS.ICMS20 icms20;
                    @XmlElement(name = "ICMS30", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.ICMS.ICMS30 icms30;
                    @XmlElement(name = "ICMS40", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.ICMS.ICMS40 icms40;
                    @XmlElement(name = "ICMS51", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.ICMS.ICMS51 icms51;
                    @XmlElement(name = "ICMS53", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.ICMS.ICMS53 icms53;
                    @XmlElement(name = "ICMS60", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.ICMS.ICMS60 icms60;
                    @XmlElement(name = "ICMS61", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.ICMS.ICMS61 icms61;
                    @XmlElement(name = "ICMS70", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.ICMS.ICMS70 icms70;
                    @XmlElement(name = "ICMS90", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.ICMS.ICMS90 icms90;
                    @XmlElement(name = "ICMSPart", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.ICMS.ICMSPart icmsPart;
                    @XmlElement(name = "ICMSST", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.ICMS.ICMSST icmsst;
                    @XmlElement(name = "ICMSSN101", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.ICMS.ICMSSN101 icmssn101;
                    @XmlElement(name = "ICMSSN102", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.ICMS.ICMSSN102 icmssn102;
                    @XmlElement(name = "ICMSSN201", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.ICMS.ICMSSN201 icmssn201;
                    @XmlElement(name = "ICMSSN202", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.ICMS.ICMSSN202 icmssn202;
                    @XmlElement(name = "ICMSSN500", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.ICMS.ICMSSN500 icmssn500;
                    @XmlElement(name = "ICMSSN900", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.ICMS.ICMSSN900 icmssn900;
                    
                    public NFe.InfNFe.Det.Imposto.ICMS.ICMS00 getICMS00() {
                        return icms00;
                    }
                    
                    public void setICMS00(NFe.InfNFe.Det.Imposto.ICMS.ICMS00 value) {
                        this.icms00 = value;
                    }

                    public NFe.InfNFe.Det.Imposto.ICMS.ICMS02 getICMS02() {
                        return icms02;
                    }
                    
                    public void setICMS02(NFe.InfNFe.Det.Imposto.ICMS.ICMS02 value) {
                        this.icms02 = value;
                    }

                    public NFe.InfNFe.Det.Imposto.ICMS.ICMS10 getICMS10() {
                        return icms10;
                    }

                    public void setICMS10(NFe.InfNFe.Det.Imposto.ICMS.ICMS10 value) {
                        this.icms10 = value;
                    }

                    public NFe.InfNFe.Det.Imposto.ICMS.ICMS15 getICMS15() {
                        return icms15;
                    }
                    
                    public void setICMS15(NFe.InfNFe.Det.Imposto.ICMS.ICMS15 value) {
                        this.icms15 = value;
                    }

                    public NFe.InfNFe.Det.Imposto.ICMS.ICMS20 getICMS20() {
                        return icms20;
                    }

                    public void setICMS20(NFe.InfNFe.Det.Imposto.ICMS.ICMS20 value) {
                        this.icms20 = value;
                    }

                    public NFe.InfNFe.Det.Imposto.ICMS.ICMS30 getICMS30() {
                        return icms30;
                    }
                    
                    public void setICMS30(NFe.InfNFe.Det.Imposto.ICMS.ICMS30 value) {
                        this.icms30 = value;
                    }
                    
                    public NFe.InfNFe.Det.Imposto.ICMS.ICMS40 getICMS40() {
                        return icms40;
                    }
                    
                    public void setICMS40(NFe.InfNFe.Det.Imposto.ICMS.ICMS40 value) {
                        this.icms40 = value;
                    }
                    
                    public NFe.InfNFe.Det.Imposto.ICMS.ICMS51 getICMS51() {
                        return icms51;
                    }
                    
                    public void setICMS51(NFe.InfNFe.Det.Imposto.ICMS.ICMS51 value) {
                        this.icms51 = value;
                    }

                    public NFe.InfNFe.Det.Imposto.ICMS.ICMS53 getICMS53() {
                        return icms53;
                    }
                    
                    public void setICMS53(NFe.InfNFe.Det.Imposto.ICMS.ICMS53 value) {
                        this.icms53 = value;
                    }
                    
                    public NFe.InfNFe.Det.Imposto.ICMS.ICMS60 getICMS60() {
                        return icms60;
                    }

                    public void setICMS60(NFe.InfNFe.Det.Imposto.ICMS.ICMS60 value) {
                        this.icms60 = value;
                    }

                    public NFe.InfNFe.Det.Imposto.ICMS.ICMS61 getICMS61() {
                        return icms61;
                    }
                    
                    public void setICMS61(NFe.InfNFe.Det.Imposto.ICMS.ICMS61 value) {
                        this.icms61 = value;
                    }

                    public NFe.InfNFe.Det.Imposto.ICMS.ICMS70 getICMS70() {
                        return icms70;
                    }
                    
                    public void setICMS70(NFe.InfNFe.Det.Imposto.ICMS.ICMS70 value) {
                        this.icms70 = value;
                    }
                    
                    public NFe.InfNFe.Det.Imposto.ICMS.ICMS90 getICMS90() {
                        return icms90;
                    }
                    
                    public void setICMS90(NFe.InfNFe.Det.Imposto.ICMS.ICMS90 value) {
                        this.icms90 = value;
                    }

                    public NFe.InfNFe.Det.Imposto.ICMS.ICMSPart getICMSPart() {
                        return icmsPart;
                    }
                    
                    public void setICMSPart(NFe.InfNFe.Det.Imposto.ICMS.ICMSPart value) {
                        this.icmsPart = value;
                    }
                    
                    public NFe.InfNFe.Det.Imposto.ICMS.ICMSST getICMSST() {
                        return icmsst;
                    }

                    public void setICMSST(NFe.InfNFe.Det.Imposto.ICMS.ICMSST value) {
                        this.icmsst = value;
                    }

                    public NFe.InfNFe.Det.Imposto.ICMS.ICMSSN101 getICMSSN101() {
                        return icmssn101;
                    }

                    public void setICMSSN101(NFe.InfNFe.Det.Imposto.ICMS.ICMSSN101 value) {
                        this.icmssn101 = value;
                    }

                    public NFe.InfNFe.Det.Imposto.ICMS.ICMSSN102 getICMSSN102() {
                        return icmssn102;
                    }
                    
                    public void setICMSSN102(NFe.InfNFe.Det.Imposto.ICMS.ICMSSN102 value) {
                        this.icmssn102 = value;
                    }
                    
                    public NFe.InfNFe.Det.Imposto.ICMS.ICMSSN201 getICMSSN201() {
                        return icmssn201;
                    }
                    
                    public void setICMSSN201(NFe.InfNFe.Det.Imposto.ICMS.ICMSSN201 value) {
                        this.icmssn201 = value;
                    }
                    
                    public NFe.InfNFe.Det.Imposto.ICMS.ICMSSN202 getICMSSN202() {
                        return icmssn202;
                    }

                    public void setICMSSN202(NFe.InfNFe.Det.Imposto.ICMS.ICMSSN202 value) {
                        this.icmssn202 = value;
                    }

                    public NFe.InfNFe.Det.Imposto.ICMS.ICMSSN500 getICMSSN500() {
                        return icmssn500;
                    }
                    
                    public void setICMSSN500(NFe.InfNFe.Det.Imposto.ICMS.ICMSSN500 value) {
                        this.icmssn500 = value;
                    }

                    public NFe.InfNFe.Det.Imposto.ICMS.ICMSSN900 getICMSSN900() {
                        return icmssn900;
                    }
                    
                    public void setICMSSN900(NFe.InfNFe.Det.Imposto.ICMS.ICMSSN900 value) {
                        this.icmssn900 = value;
                    }

                    
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "orig",
                        "cst",
                        "modBC",
                        "vbc",
                        "picms",
                        "vicms",
                        "pfcp",
                        "vfcp"
                    })
                    public static class ICMS00 {

                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String orig;
                        @XmlElement(name = "CST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String cst;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String modBC;
                        @XmlElement(name = "vBC", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vbc;
                        @XmlElement(name = "pICMS", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String picms;
                        @XmlElement(name = "vICMS", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vicms;
                        @XmlElement(name = "pFCP", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pfcp;
                        @XmlElement(name = "vFCP", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vfcp;

                        public String getOrig() {
                            return orig;
                        }
                        
                        public void setOrig(String value) {
                            this.orig = value;
                        }
                        
                        public String getCST() {
                            return cst;
                        }

                        public void setCST(String value) {
                            this.cst = value;
                        }

                        public String getModBC() {
                            return modBC;
                        }

                        public void setModBC(String value) {
                            this.modBC = value;
                        }
                        
                        public String getVBC() {
                            return vbc;
                        }

                        public void setVBC(String value) {
                            this.vbc = value;
                        }

                        public String getPICMS() {
                            return picms;
                        }

                        public void setPICMS(String value) {
                            this.picms = value;
                        }

                        public String getVICMS() {
                            return vicms;
                        }

                        public void setVICMS(String value) {
                            this.vicms = value;
                        }
                        
                        public String getPFCP() {
                            return pfcp;
                        }
                        
                        public void setPFCP(String value) {
                            this.pfcp = value;
                        }

                        public String getVFCP() {
                            return vfcp;
                        }
                        
                        public void setVFCP(String value) {
                            this.vfcp = value;
                        }

                    }

                    
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "orig",
                        "cst",
                        "qbcMono",
                        "adRemICMS",
                        "vicmsMono"
                    })
                    public static class ICMS02 {

                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String orig;
                        @XmlElement(name = "CST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String cst;
                        @XmlElement(name = "qBCMono", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String qbcMono;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String adRemICMS;
                        @XmlElement(name = "vICMSMono", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vicmsMono;
                        
                        public String getOrig() {
                            return orig;
                        }

                        public void setOrig(String value) {
                            this.orig = value;
                        }

                        public String getCST() {
                            return cst;
                        }
                        
                        public void setCST(String value) {
                            this.cst = value;
                        }
                        
                        public String getQBCMono() {
                            return qbcMono;
                        }

                        public void setQBCMono(String value) {
                            this.qbcMono = value;
                        }
                        
                        public String getAdRemICMS() {
                            return adRemICMS;
                        }
                        
                        public void setAdRemICMS(String value) {
                            this.adRemICMS = value;
                        }

                        public String getVICMSMono() {
                            return vicmsMono;
                        }
                        
                        public void setVICMSMono(String value) {
                            this.vicmsMono = value;
                        }

                    }

                    
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "orig",
                        "cst",
                        "modBC",
                        "vbc",
                        "picms",
                        "vicms",
                        "vbcfcp",
                        "pfcp",
                        "vfcp",
                        "modBCST",
                        "pmvast",
                        "pRedBCST",
                        "vbcst",
                        "picmsst",
                        "vicmsst",
                        "vbcfcpst",
                        "pfcpst",
                        "vfcpst",
                        "vicmsstDeson",
                        "motDesICMSST"
                    })
                    public static class ICMS10 {

                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String orig;
                        @XmlElement(name = "CST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String cst;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String modBC;
                        @XmlElement(name = "vBC", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vbc;
                        @XmlElement(name = "pICMS", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String picms;
                        @XmlElement(name = "vICMS", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vicms;
                        @XmlElement(name = "vBCFCP", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbcfcp;
                        @XmlElement(name = "pFCP", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pfcp;
                        @XmlElement(name = "vFCP", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vfcp;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String modBCST;
                        @XmlElement(name = "pMVAST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pmvast;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pRedBCST;
                        @XmlElement(name = "vBCST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vbcst;
                        @XmlElement(name = "pICMSST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String picmsst;
                        @XmlElement(name = "vICMSST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vicmsst;
                        @XmlElement(name = "vBCFCPST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbcfcpst;
                        @XmlElement(name = "pFCPST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pfcpst;
                        @XmlElement(name = "vFCPST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vfcpst;
                        @XmlElement(name = "vICMSSTDeson", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vicmsstDeson;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String motDesICMSST;
                        
                        public String getOrig() {
                            return orig;
                        }
                        
                        public void setOrig(String value) {
                            this.orig = value;
                        }
                        
                        public String getCST() {
                            return cst;
                        }
                        
                        public void setCST(String value) {
                            this.cst = value;
                        }
                        
                        public String getModBC() {
                            return modBC;
                        }
                        
                        public void setModBC(String value) {
                            this.modBC = value;
                        }
                        
                        public String getVBC() {
                            return vbc;
                        }

                        public void setVBC(String value) {
                            this.vbc = value;
                        }
                        
                        public String getPICMS() {
                            return picms;
                        }

                        public void setPICMS(String value) {
                            this.picms = value;
                        }

                        public String getVICMS() {
                            return vicms;
                        }
                        
                        public void setVICMS(String value) {
                            this.vicms = value;
                        }
                        
                        public String getVBCFCP() {
                            return vbcfcp;
                        }
                        
                        public void setVBCFCP(String value) {
                            this.vbcfcp = value;
                        }

                        public String getPFCP() {
                            return pfcp;
                        }

                        public void setPFCP(String value) {
                            this.pfcp = value;
                        }

                        public String getVFCP() {
                            return vfcp;
                        }

                        public void setVFCP(String value) {
                            this.vfcp = value;
                        }

                        public String getModBCST() {
                            return modBCST;
                        }
                        
                        public void setModBCST(String value) {
                            this.modBCST = value;
                        }

                        public String getPMVAST() {
                            return pmvast;
                        }
                        
                        public void setPMVAST(String value) {
                            this.pmvast = value;
                        }

                        public String getPRedBCST() {
                            return pRedBCST;
                        }

                        public void setPRedBCST(String value) {
                            this.pRedBCST = value;
                        }
                        
                        public String getVBCST() {
                            return vbcst;
                        }

                        public void setVBCST(String value) {
                            this.vbcst = value;
                        }

                        public String getPICMSST() {
                            return picmsst;
                        }

                        public void setPICMSST(String value) {
                            this.picmsst = value;
                        }
                        
                        public String getVICMSST() {
                            return vicmsst;
                        }

                        public void setVICMSST(String value) {
                            this.vicmsst = value;
                        }

                        public String getVBCFCPST() {
                            return vbcfcpst;
                        }

                        public void setVBCFCPST(String value) {
                            this.vbcfcpst = value;
                        }
                        
                        public String getPFCPST() {
                            return pfcpst;
                        }

                        public void setPFCPST(String value) {
                            this.pfcpst = value;
                        }

                        public String getVFCPST() {
                            return vfcpst;
                        }

                        public void setVFCPST(String value) {
                            this.vfcpst = value;
                        }
                        
                        public String getVICMSSTDeson() {
                            return vicmsstDeson;
                        }

                        public void setVICMSSTDeson(String value) {
                            this.vicmsstDeson = value;
                        }

                        public String getMotDesICMSST() {
                            return motDesICMSST;
                        }

                        public void setMotDesICMSST(String value) {
                            this.motDesICMSST = value;
                        }

                    }


                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "orig",
                        "cst",
                        "qbcMono",
                        "adRemICMS",
                        "vicmsMono",
                        "qbcMonoReten",
                        "adRemICMSReten",
                        "vicmsMonoReten",
                        "pRedAdRem",
                        "motRedAdRem"
                    })
                    public static class ICMS15 {

                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String orig;
                        @XmlElement(name = "CST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String cst;
                        @XmlElement(name = "qBCMono", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String qbcMono;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String adRemICMS;
                        @XmlElement(name = "vICMSMono", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vicmsMono;
                        @XmlElement(name = "qBCMonoReten", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String qbcMonoReten;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String adRemICMSReten;
                        @XmlElement(name = "vICMSMonoReten", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vicmsMonoReten;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pRedAdRem;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String motRedAdRem;
                        
                        public String getOrig() {
                            return orig;
                        }

                        public void setOrig(String value) {
                            this.orig = value;
                        }
                        
                        public String getCST() {
                            return cst;
                        }
                        
                        public void setCST(String value) {
                            this.cst = value;
                        }
                        
                        public String getQBCMono() {
                            return qbcMono;
                        }
                        
                        public void setQBCMono(String value) {
                            this.qbcMono = value;
                        }

                        public String getAdRemICMS() {
                            return adRemICMS;
                        }

                        public void setAdRemICMS(String value) {
                            this.adRemICMS = value;
                        }

                        public String getVICMSMono() {
                            return vicmsMono;
                        }

                        public void setVICMSMono(String value) {
                            this.vicmsMono = value;
                        }

                        public String getQBCMonoReten() {
                            return qbcMonoReten;
                        }

                        public void setQBCMonoReten(String value) {
                            this.qbcMonoReten = value;
                        }

                        public String getAdRemICMSReten() {
                            return adRemICMSReten;
                        }

                        public void setAdRemICMSReten(String value) {
                            this.adRemICMSReten = value;
                        }
                        
                        public String getVICMSMonoReten() {
                            return vicmsMonoReten;
                        }
                        
                        public void setVICMSMonoReten(String value) {
                            this.vicmsMonoReten = value;
                        }
                        
                        public String getPRedAdRem() {
                            return pRedAdRem;
                        }

                        public void setPRedAdRem(String value) {
                            this.pRedAdRem = value;
                        }
                        
                        public String getMotRedAdRem() {
                            return motRedAdRem;
                        }

                        public void setMotRedAdRem(String value) {
                            this.motRedAdRem = value;
                        }

                    }

                    
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "orig",
                        "cst",
                        "modBC",
                        "pRedBC",
                        "vbc",
                        "picms",
                        "vicms",
                        "vbcfcp",
                        "pfcp",
                        "vfcp",
                        "vicmsDeson",
                        "motDesICMS",
                        "indDeduzDeson"
                    })
                    public static class ICMS20 {

                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String orig;
                        @XmlElement(name = "CST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String cst;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String modBC;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String pRedBC;
                        @XmlElement(name = "vBC", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vbc;
                        @XmlElement(name = "pICMS", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String picms;
                        @XmlElement(name = "vICMS", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vicms;
                        @XmlElement(name = "vBCFCP", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbcfcp;
                        @XmlElement(name = "pFCP", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pfcp;
                        @XmlElement(name = "vFCP", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vfcp;
                        @XmlElement(name = "vICMSDeson", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vicmsDeson;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String motDesICMS;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String indDeduzDeson;

                        public String getOrig() {
                            return orig;
                        }
                        
                        public void setOrig(String value) {
                            this.orig = value;
                        }

                        public String getCST() {
                            return cst;
                        }
                        
                        public void setCST(String value) {
                            this.cst = value;
                        }
                        
                        public String getModBC() {
                            return modBC;
                        }
                        
                        public void setModBC(String value) {
                            this.modBC = value;
                        }

                        public String getPRedBC() {
                            return pRedBC;
                        }
                        
                        public void setPRedBC(String value) {
                            this.pRedBC = value;
                        }

                        public String getVBC() {
                            return vbc;
                        }

                        public void setVBC(String value) {
                            this.vbc = value;
                        }

                        public String getPICMS() {
                            return picms;
                        }

                        public void setPICMS(String value) {
                            this.picms = value;
                        }
                        
                        public String getVICMS() {
                            return vicms;
                        }

                        public void setVICMS(String value) {
                            this.vicms = value;
                        }

                        public String getVBCFCP() {
                            return vbcfcp;
                        }

                        public void setVBCFCP(String value) {
                            this.vbcfcp = value;
                        }

                        public String getPFCP() {
                            return pfcp;
                        }
                        
                        public void setPFCP(String value) {
                            this.pfcp = value;
                        }

                        public String getVFCP() {
                            return vfcp;
                        }

                        public void setVFCP(String value) {
                            this.vfcp = value;
                        }
                        
                        public String getVICMSDeson() {
                            return vicmsDeson;
                        }

                        public void setVICMSDeson(String value) {
                            this.vicmsDeson = value;
                        }
                        
                        public String getMotDesICMS() {
                            return motDesICMS;
                        }

                        public void setMotDesICMS(String value) {
                            this.motDesICMS = value;
                        }
                        
                        public String getIndDeduzDeson() {
                            return indDeduzDeson;
                        }

                        public void setIndDeduzDeson(String value) {
                            this.indDeduzDeson = value;
                        }

                    }

                    
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "orig",
                        "cst",
                        "modBCST",
                        "pmvast",
                        "pRedBCST",
                        "vbcst",
                        "picmsst",
                        "vicmsst",
                        "vbcfcpst",
                        "pfcpst",
                        "vfcpst",
                        "vicmsDeson",
                        "motDesICMS",
                        "indDeduzDeson"
                    })
                    public static class ICMS30 {

                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String orig;
                        @XmlElement(name = "CST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String cst;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String modBCST;
                        @XmlElement(name = "pMVAST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pmvast;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pRedBCST;
                        @XmlElement(name = "vBCST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vbcst;
                        @XmlElement(name = "pICMSST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String picmsst;
                        @XmlElement(name = "vICMSST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vicmsst;
                        @XmlElement(name = "vBCFCPST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbcfcpst;
                        @XmlElement(name = "pFCPST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pfcpst;
                        @XmlElement(name = "vFCPST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vfcpst;
                        @XmlElement(name = "vICMSDeson", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vicmsDeson;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String motDesICMS;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String indDeduzDeson;

                        public String getOrig() {
                            return orig;
                        }
                        
                        public void setOrig(String value) {
                            this.orig = value;
                        }
                        
                        public String getCST() {
                            return cst;
                        }

                        public void setCST(String value) {
                            this.cst = value;
                        }

                        public String getModBCST() {
                            return modBCST;
                        }

                        public void setModBCST(String value) {
                            this.modBCST = value;
                        }
                        
                        public String getPMVAST() {
                            return pmvast;
                        }
                        
                        public void setPMVAST(String value) {
                            this.pmvast = value;
                        }
                        
                        public String getPRedBCST() {
                            return pRedBCST;
                        }
                        
                        public void setPRedBCST(String value) {
                            this.pRedBCST = value;
                        }
                        
                        public String getVBCST() {
                            return vbcst;
                        }
                        
                        public void setVBCST(String value) {
                            this.vbcst = value;
                        }
                        
                        public String getPICMSST() {
                            return picmsst;
                        }
                        
                        public void setPICMSST(String value) {
                            this.picmsst = value;
                        }
                        
                        public String getVICMSST() {
                            return vicmsst;
                        }
                        
                        public void setVICMSST(String value) {
                            this.vicmsst = value;
                        }
                        
                        public String getVBCFCPST() {
                            return vbcfcpst;
                        }
                        
                        public void setVBCFCPST(String value) {
                            this.vbcfcpst = value;
                        }
                        
                        public String getPFCPST() {
                            return pfcpst;
                        }
                        
                        public void setPFCPST(String value) {
                            this.pfcpst = value;
                        }
                        
                        public String getVFCPST() {
                            return vfcpst;
                        }

                        public void setVFCPST(String value) {
                            this.vfcpst = value;
                        }

                        public String getVICMSDeson() {
                            return vicmsDeson;
                        }
                        
                        public void setVICMSDeson(String value) {
                            this.vicmsDeson = value;
                        }
                        
                        public String getMotDesICMS() {
                            return motDesICMS;
                        }
                        
                        public void setMotDesICMS(String value) {
                            this.motDesICMS = value;
                        }
                        
                        public String getIndDeduzDeson() {
                            return indDeduzDeson;
                        }
                        
                        public void setIndDeduzDeson(String value) {
                            this.indDeduzDeson = value;
                        }

                    }


                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "orig",
                        "cst",
                        "vicmsDeson",
                        "motDesICMS",
                        "indDeduzDeson"
                    })
                    public static class ICMS40 {

                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String orig;
                        @XmlElement(name = "CST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String cst;
                        @XmlElement(name = "vICMSDeson", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vicmsDeson;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String motDesICMS;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String indDeduzDeson;

                        public String getOrig() {
                            return orig;
                        }
                        
                        public void setOrig(String value) {
                            this.orig = value;
                        }
                        
                        public String getCST() {
                            return cst;
                        }

                        public void setCST(String value) {
                            this.cst = value;
                        }
                        
                        public String getVICMSDeson() {
                            return vicmsDeson;
                        }

                        public void setVICMSDeson(String value) {
                            this.vicmsDeson = value;
                        }

                        public String getMotDesICMS() {
                            return motDesICMS;
                        }
                        
                        public void setMotDesICMS(String value) {
                            this.motDesICMS = value;
                        }

                        public String getIndDeduzDeson() {
                            return indDeduzDeson;
                        }
                        
                        public void setIndDeduzDeson(String value) {
                            this.indDeduzDeson = value;
                        }

                    }

                    
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "orig",
                        "cst",
                        "modBC",
                        "pRedBC",
                        "cBenefRBC",
                        "vbc",
                        "picms",
                        "vicmsOp",
                        "pDif",
                        "vicmsDif",
                        "vicms",
                        "vbcfcp",
                        "pfcp",
                        "vfcp",
                        "pfcpDif",
                        "vfcpDif",
                        "vfcpEfet"
                    })
                    public static class ICMS51 {

                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String orig;
                        @XmlElement(name = "CST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String cst;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String modBC;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pRedBC;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String cBenefRBC;
                        @XmlElement(name = "vBC", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbc;
                        @XmlElement(name = "pICMS", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String picms;
                        @XmlElement(name = "vICMSOp", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vicmsOp;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pDif;
                        @XmlElement(name = "vICMSDif", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vicmsDif;
                        @XmlElement(name = "vICMS", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vicms;
                        @XmlElement(name = "vBCFCP", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbcfcp;
                        @XmlElement(name = "pFCP", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pfcp;
                        @XmlElement(name = "vFCP", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vfcp;
                        @XmlElement(name = "pFCPDif", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pfcpDif;
                        @XmlElement(name = "vFCPDif", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vfcpDif;
                        @XmlElement(name = "vFCPEfet", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vfcpEfet;

                        public String getOrig() {
                            return orig;
                        }
                        
                        public void setOrig(String value) {
                            this.orig = value;
                        }

                        public String getCST() {
                            return cst;
                        }
                        
                        public void setCST(String value) {
                            this.cst = value;
                        }

                        public String getModBC() {
                            return modBC;
                        }

                        public void setModBC(String value) {
                            this.modBC = value;
                        }
                        
                        public String getPRedBC() {
                            return pRedBC;
                        }

                        public void setPRedBC(String value) {
                            this.pRedBC = value;
                        }

                        public String getCBenefRBC() {
                            return cBenefRBC;
                        }

                        public void setCBenefRBC(String value) {
                            this.cBenefRBC = value;
                        }
                        
                        public String getVBC() {
                            return vbc;
                        }

                        public void setVBC(String value) {
                            this.vbc = value;
                        }
                        
                        public String getPICMS() {
                            return picms;
                        }

                        public void setPICMS(String value) {
                            this.picms = value;
                        }
                        
                        public String getVICMSOp() {
                            return vicmsOp;
                        }

                        public void setVICMSOp(String value) {
                            this.vicmsOp = value;
                        }
                        
                        public String getPDif() {
                            return pDif;
                        }

                        public void setPDif(String value) {
                            this.pDif = value;
                        }

                        public String getVICMSDif() {
                            return vicmsDif;
                        }

                        public void setVICMSDif(String value) {
                            this.vicmsDif = value;
                        }

                        public String getVICMS() {
                            return vicms;
                        }

                        public void setVICMS(String value) {
                            this.vicms = value;
                        }
                        
                        public String getVBCFCP() {
                            return vbcfcp;
                        }
                        
                        public void setVBCFCP(String value) {
                            this.vbcfcp = value;
                        }

                        public String getPFCP() {
                            return pfcp;
                        }

                        public void setPFCP(String value) {
                            this.pfcp = value;
                        }

                        public String getVFCP() {
                            return vfcp;
                        }

                        public void setVFCP(String value) {
                            this.vfcp = value;
                        }
                        
                        public String getPFCPDif() {
                            return pfcpDif;
                        }

                        public void setPFCPDif(String value) {
                            this.pfcpDif = value;
                        }

                        public String getVFCPDif() {
                            return vfcpDif;
                        }
                        
                        public void setVFCPDif(String value) {
                            this.vfcpDif = value;
                        }

                        public String getVFCPEfet() {
                            return vfcpEfet;
                        }

                        public void setVFCPEfet(String value) {
                            this.vfcpEfet = value;
                        }
                    }

                    
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "orig",
                        "cst",
                        "qbcMono",
                        "adRemICMS",
                        "vicmsMonoOp",
                        "pDif",
                        "vicmsMonoDif",
                        "vicmsMono",
                        "qbcMonoDif",
                        "adRemICMSDif"
                    })
                    public static class ICMS53 {

                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String orig;
                        @XmlElement(name = "CST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String cst;
                        @XmlElement(name = "qBCMono", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String qbcMono;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String adRemICMS;
                        @XmlElement(name = "vICMSMonoOp", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vicmsMonoOp;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pDif;
                        @XmlElement(name = "vICMSMonoDif", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vicmsMonoDif;
                        @XmlElement(name = "vICMSMono", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vicmsMono;
                        @XmlElement(name = "qBCMonoDif", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String qbcMonoDif;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String adRemICMSDif;
                        
                        public String getOrig() {
                            return orig;
                        }
                        
                        public void setOrig(String value) {
                            this.orig = value;
                        }

                        public String getCST() {
                            return cst;
                        }

                        public void setCST(String value) {
                            this.cst = value;
                        }
                        
                        public String getQBCMono() {
                            return qbcMono;
                        }

                        public void setQBCMono(String value) {
                            this.qbcMono = value;
                        }

                        public String getAdRemICMS() {
                            return adRemICMS;
                        }

                        public void setAdRemICMS(String value) {
                            this.adRemICMS = value;
                        }

                        public String getVICMSMonoOp() {
                            return vicmsMonoOp;
                        }

                        public void setVICMSMonoOp(String value) {
                            this.vicmsMonoOp = value;
                        }
                        
                        public String getPDif() {
                            return pDif;
                        }
                        
                        public void setPDif(String value) {
                            this.pDif = value;
                        }

                        public String getVICMSMonoDif() {
                            return vicmsMonoDif;
                        }
                        
                        public void setVICMSMonoDif(String value) {
                            this.vicmsMonoDif = value;
                        }
                        
                        public String getVICMSMono() {
                            return vicmsMono;
                        }
                        
                        public void setVICMSMono(String value) {
                            this.vicmsMono = value;
                        }
                        
                        public String getQBCMonoDif() {
                            return qbcMonoDif;
                        }
                        
                        public void setQBCMonoDif(String value) {
                            this.qbcMonoDif = value;
                        }
                        
                        public String getAdRemICMSDif() {
                            return adRemICMSDif;
                        }
                        
                        public void setAdRemICMSDif(String value) {
                            this.adRemICMSDif = value;
                        }

                    }

                    
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "orig",
                        "cst",
                        "vbcstRet",
                        "pst",
                        "vicmsSubstituto",
                        "vicmsstRet",
                        "vbcfcpstRet",
                        "pfcpstRet",
                        "vfcpstRet",
                        "pRedBCEfet",
                        "vbcEfet",
                        "picmsEfet",
                        "vicmsEfet"
                    })
                    public static class ICMS60 {

                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String orig;
                        @XmlElement(name = "CST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String cst;
                        @XmlElement(name = "vBCSTRet", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbcstRet;
                        @XmlElement(name = "pST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pst;
                        @XmlElement(name = "vICMSSubstituto", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vicmsSubstituto;
                        @XmlElement(name = "vICMSSTRet", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vicmsstRet;
                        @XmlElement(name = "vBCFCPSTRet", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbcfcpstRet;
                        @XmlElement(name = "pFCPSTRet", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pfcpstRet;
                        @XmlElement(name = "vFCPSTRet", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vfcpstRet;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pRedBCEfet;
                        @XmlElement(name = "vBCEfet", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbcEfet;
                        @XmlElement(name = "pICMSEfet", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String picmsEfet;
                        @XmlElement(name = "vICMSEfet", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vicmsEfet;

                        public String getOrig() {
                            return orig;
                        }

                        public void setOrig(String value) {
                            this.orig = value;
                        }

                        public String getCST() {
                            return cst;
                        }
                        
                        public void setCST(String value) {
                            this.cst = value;
                        }
                        
                        public String getVBCSTRet() {
                            return vbcstRet;
                        }
                        
                        public void setVBCSTRet(String value) {
                            this.vbcstRet = value;
                        }
                        
                        public String getPST() {
                            return pst;
                        }
                        
                        public void setPST(String value) {
                            this.pst = value;
                        }
                        
                        public String getVICMSSubstituto() {
                            return vicmsSubstituto;
                        }
                        
                        public void setVICMSSubstituto(String value) {
                            this.vicmsSubstituto = value;
                        }
                        
                        public String getVICMSSTRet() {
                            return vicmsstRet;
                        }
                        
                        public void setVICMSSTRet(String value) {
                            this.vicmsstRet = value;
                        }
                        
                        public String getVBCFCPSTRet() {
                            return vbcfcpstRet;
                        }
                        
                        public void setVBCFCPSTRet(String value) {
                            this.vbcfcpstRet = value;
                        }

                        public String getPFCPSTRet() {
                            return pfcpstRet;
                        }

                        public void setPFCPSTRet(String value) {
                            this.pfcpstRet = value;
                        }
                        
                        public String getVFCPSTRet() {
                            return vfcpstRet;
                        }
                        
                        public void setVFCPSTRet(String value) {
                            this.vfcpstRet = value;
                        }
                        
                        public String getPRedBCEfet() {
                            return pRedBCEfet;
                        }
                        
                        public void setPRedBCEfet(String value) {
                            this.pRedBCEfet = value;
                        }
                        
                        public String getVBCEfet() {
                            return vbcEfet;
                        }
                        
                        public void setVBCEfet(String value) {
                            this.vbcEfet = value;
                        }

                        public String getPICMSEfet() {
                            return picmsEfet;
                        }

                        public void setPICMSEfet(String value) {
                            this.picmsEfet = value;
                        }

                        public String getVICMSEfet() {
                            return vicmsEfet;
                        }
                        
                        public void setVICMSEfet(String value) {
                            this.vicmsEfet = value;
                        }

                    }

                    
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "orig",
                        "cst",
                        "qbcMonoRet",
                        "adRemICMSRet",
                        "vicmsMonoRet"
                    })
                    public static class ICMS61 {

                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String orig;
                        @XmlElement(name = "CST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String cst;
                        @XmlElement(name = "qBCMonoRet", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String qbcMonoRet;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String adRemICMSRet;
                        @XmlElement(name = "vICMSMonoRet", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vicmsMonoRet;
                        
                        public String getOrig() {
                            return orig;
                        }
                        
                        public void setOrig(String value) {
                            this.orig = value;
                        }
                        
                        public String getCST() {
                            return cst;
                        }

                        public void setCST(String value) {
                            this.cst = value;
                        }
                        
                        public String getQBCMonoRet() {
                            return qbcMonoRet;
                        }

                        public void setQBCMonoRet(String value) {
                            this.qbcMonoRet = value;
                        }

                        public String getAdRemICMSRet() {
                            return adRemICMSRet;
                        }

                        public void setAdRemICMSRet(String value) {
                            this.adRemICMSRet = value;
                        }

                        public String getVICMSMonoRet() {
                            return vicmsMonoRet;
                        }
                        
                        public void setVICMSMonoRet(String value) {
                            this.vicmsMonoRet = value;
                        }

                    }

                    
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "orig",
                        "cst",
                        "modBC",
                        "pRedBC",
                        "vbc",
                        "picms",
                        "vicms",
                        "vbcfcp",
                        "pfcp",
                        "vfcp",
                        "modBCST",
                        "pmvast",
                        "pRedBCST",
                        "vbcst",
                        "picmsst",
                        "vicmsst",
                        "vbcfcpst",
                        "pfcpst",
                        "vfcpst",
                        "vicmsDeson",
                        "motDesICMS",
                        "indDeduzDeson",
                        "vicmsstDeson",
                        "motDesICMSST"
                    })
                    public static class ICMS70 {

                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String orig;
                        @XmlElement(name = "CST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String cst;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String modBC;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String pRedBC;
                        @XmlElement(name = "vBC", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vbc;
                        @XmlElement(name = "pICMS", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String picms;
                        @XmlElement(name = "vICMS", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vicms;
                        @XmlElement(name = "vBCFCP", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbcfcp;
                        @XmlElement(name = "pFCP", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pfcp;
                        @XmlElement(name = "vFCP", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vfcp;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String modBCST;
                        @XmlElement(name = "pMVAST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pmvast;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pRedBCST;
                        @XmlElement(name = "vBCST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vbcst;
                        @XmlElement(name = "pICMSST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String picmsst;
                        @XmlElement(name = "vICMSST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vicmsst;
                        @XmlElement(name = "vBCFCPST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbcfcpst;
                        @XmlElement(name = "pFCPST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pfcpst;
                        @XmlElement(name = "vFCPST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vfcpst;
                        @XmlElement(name = "vICMSDeson", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vicmsDeson;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String motDesICMS;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String indDeduzDeson;
                        @XmlElement(name = "vICMSSTDeson", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vicmsstDeson;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String motDesICMSST;

                        public String getOrig() {
                            return orig;
                        }
                        
                        public void setOrig(String value) {
                            this.orig = value;
                        }

                        public String getCST() {
                            return cst;
                        }
                        
                        public void setCST(String value) {
                            this.cst = value;
                        }
                        
                        public String getModBC() {
                            return modBC;
                        }

                        public void setModBC(String value) {
                            this.modBC = value;
                        }
                        
                        public String getPRedBC() {
                            return pRedBC;
                        }

                        public void setPRedBC(String value) {
                            this.pRedBC = value;
                        }
                        
                        public String getVBC() {
                            return vbc;
                        }
                        
                        public void setVBC(String value) {
                            this.vbc = value;
                        }
                        
                        public String getPICMS() {
                            return picms;
                        }

                        public void setPICMS(String value) {
                            this.picms = value;
                        }
                        
                        public String getVICMS() {
                            return vicms;
                        }
                        
                        public void setVICMS(String value) {
                            this.vicms = value;
                        }
                        
                        public String getVBCFCP() {
                            return vbcfcp;
                        }
                        
                        public void setVBCFCP(String value) {
                            this.vbcfcp = value;
                        }

                        public String getPFCP() {
                            return pfcp;
                        }
                        
                        public void setPFCP(String value) {
                            this.pfcp = value;
                        }

                        public String getVFCP() {
                            return vfcp;
                        }
                        
                        public void setVFCP(String value) {
                            this.vfcp = value;
                        }
                        
                        public String getModBCST() {
                            return modBCST;
                        }

                        public void setModBCST(String value) {
                            this.modBCST = value;
                        }

                        public String getPMVAST() {
                            return pmvast;
                        }

                        public void setPMVAST(String value) {
                            this.pmvast = value;
                        }
                        
                        public String getPRedBCST() {
                            return pRedBCST;
                        }

                        public void setPRedBCST(String value) {
                            this.pRedBCST = value;
                        }
                        
                        public String getVBCST() {
                            return vbcst;
                        }

                        public void setVBCST(String value) {
                            this.vbcst = value;
                        }

                        public String getPICMSST() {
                            return picmsst;
                        }

                        public void setPICMSST(String value) {
                            this.picmsst = value;
                        }

                        public String getVICMSST() {
                            return vicmsst;
                        }
                        
                        public void setVICMSST(String value) {
                            this.vicmsst = value;
                        }

                        public String getVBCFCPST() {
                            return vbcfcpst;
                        }

                        public void setVBCFCPST(String value) {
                            this.vbcfcpst = value;
                        }

                        public String getPFCPST() {
                            return pfcpst;
                        }
                        
                        public void setPFCPST(String value) {
                            this.pfcpst = value;
                        }

                        public String getVFCPST() {
                            return vfcpst;
                        }

                        public void setVFCPST(String value) {
                            this.vfcpst = value;
                        }
                        
                        public String getVICMSDeson() {
                            return vicmsDeson;
                        }

                        public void setVICMSDeson(String value) {
                            this.vicmsDeson = value;
                        }
                        
                        public String getMotDesICMS() {
                            return motDesICMS;
                        }

                        public void setMotDesICMS(String value) {
                            this.motDesICMS = value;
                        }

                        public String getIndDeduzDeson() {
                            return indDeduzDeson;
                        }

                        public void setIndDeduzDeson(String value) {
                            this.indDeduzDeson = value;
                        }

                        public String getVICMSSTDeson() {
                            return vicmsstDeson;
                        }

                        public void setVICMSSTDeson(String value) {
                            this.vicmsstDeson = value;
                        }

                        public String getMotDesICMSST() {
                            return motDesICMSST;
                        }

                        public void setMotDesICMSST(String value) {
                            this.motDesICMSST = value;
                        }

                    }

                    
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "orig",
                        "cst",
                        "modBC",
                        "vbc",
                        "pRedBC",
                        "picms",
                        "vicms",
                        "vbcfcp",
                        "pfcp",
                        "vfcp",
                        "modBCST",
                        "pmvast",
                        "pRedBCST",
                        "vbcst",
                        "picmsst",
                        "vicmsst",
                        "vbcfcpst",
                        "pfcpst",
                        "vfcpst",
                        "vicmsDeson",
                        "motDesICMS",
                        "indDeduzDeson",
                        "vicmsstDeson",
                        "motDesICMSST"
                    })
                    public static class ICMS90 {

                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String orig;
                        @XmlElement(name = "CST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String cst;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String modBC;
                        @XmlElement(name = "vBC", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbc;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pRedBC;
                        @XmlElement(name = "pICMS", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String picms;
                        @XmlElement(name = "vICMS", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vicms;
                        @XmlElement(name = "vBCFCP", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbcfcp;
                        @XmlElement(name = "pFCP", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pfcp;
                        @XmlElement(name = "vFCP", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vfcp;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String modBCST;
                        @XmlElement(name = "pMVAST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pmvast;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pRedBCST;
                        @XmlElement(name = "vBCST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbcst;
                        @XmlElement(name = "pICMSST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String picmsst;
                        @XmlElement(name = "vICMSST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vicmsst;
                        @XmlElement(name = "vBCFCPST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbcfcpst;
                        @XmlElement(name = "pFCPST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pfcpst;
                        @XmlElement(name = "vFCPST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vfcpst;
                        @XmlElement(name = "vICMSDeson", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vicmsDeson;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String motDesICMS;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String indDeduzDeson;
                        @XmlElement(name = "vICMSSTDeson", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vicmsstDeson;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String motDesICMSST;
                        
                        public String getOrig() {
                            return orig;
                        }
                        
                        public void setOrig(String value) {
                            this.orig = value;
                        }

                        public String getCST() {
                            return cst;
                        }
                        
                        public void setCST(String value) {
                            this.cst = value;
                        }

                        public String getModBC() {
                            return modBC;
                        }

                        public void setModBC(String value) {
                            this.modBC = value;
                        }
                        
                        public String getVBC() {
                            return vbc;
                        }

                        public void setVBC(String value) {
                            this.vbc = value;
                        }

                        public String getPRedBC() {
                            return pRedBC;
                        }

                        public void setPRedBC(String value) {
                            this.pRedBC = value;
                        }

                        public String getPICMS() {
                            return picms;
                        }
                        
                        public void setPICMS(String value) {
                            this.picms = value;
                        }
                        
                        public String getVICMS() {
                            return vicms;
                        }

                        public void setVICMS(String value) {
                            this.vicms = value;
                        }
                        
                        public String getVBCFCP() {
                            return vbcfcp;
                        }

                        public void setVBCFCP(String value) {
                            this.vbcfcp = value;
                        }
                        
                        public String getPFCP() {
                            return pfcp;
                        }

                        public void setPFCP(String value) {
                            this.pfcp = value;
                        }

                        public String getVFCP() {
                            return vfcp;
                        }
                        
                        public void setVFCP(String value) {
                            this.vfcp = value;
                        }

                        public String getModBCST() {
                            return modBCST;
                        }

                        public void setModBCST(String value) {
                            this.modBCST = value;
                        }

                        public String getPMVAST() {
                            return pmvast;
                        }
                        
                        public void setPMVAST(String value) {
                            this.pmvast = value;
                        }

                        public String getPRedBCST() {
                            return pRedBCST;
                        }
                        
                        public void setPRedBCST(String value) {
                            this.pRedBCST = value;
                        }
                        
                        public String getVBCST() {
                            return vbcst;
                        }
                        
                        public void setVBCST(String value) {
                            this.vbcst = value;
                        }

                        public String getPICMSST() {
                            return picmsst;
                        }

                        public void setPICMSST(String value) {
                            this.picmsst = value;
                        }

                        public String getVICMSST() {
                            return vicmsst;
                        }

                        public void setVICMSST(String value) {
                            this.vicmsst = value;
                        }
                        
                        public String getVBCFCPST() {
                            return vbcfcpst;
                        }
                        
                        public void setVBCFCPST(String value) {
                            this.vbcfcpst = value;
                        }

                        public String getPFCPST() {
                            return pfcpst;
                        }

                        public void setPFCPST(String value) {
                            this.pfcpst = value;
                        }

                        public String getVFCPST() {
                            return vfcpst;
                        }
                        
                        public void setVFCPST(String value) {
                            this.vfcpst = value;
                        }

                        public String getVICMSDeson() {
                            return vicmsDeson;
                        }

                        public void setVICMSDeson(String value) {
                            this.vicmsDeson = value;
                        }
                        
                        public String getMotDesICMS() {
                            return motDesICMS;
                        }

                        public void setMotDesICMS(String value) {
                            this.motDesICMS = value;
                        }

                        public String getIndDeduzDeson() {
                            return indDeduzDeson;
                        }

                        public void setIndDeduzDeson(String value) {
                            this.indDeduzDeson = value;
                        }
                        
                        public String getVICMSSTDeson() {
                            return vicmsstDeson;
                        }
                        
                        public void setVICMSSTDeson(String value) {
                            this.vicmsstDeson = value;
                        }

                        public String getMotDesICMSST() {
                            return motDesICMSST;
                        }

                        public void setMotDesICMSST(String value) {
                            this.motDesICMSST = value;
                        }

                    }


                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "orig",
                        "cst",
                        "modBC",
                        "vbc",
                        "pRedBC",
                        "picms",
                        "vicms",
                        "modBCST",
                        "pmvast",
                        "pRedBCST",
                        "vbcst",
                        "picmsst",
                        "vicmsst",
                        "vbcfcpst",
                        "pfcpst",
                        "vfcpst",
                        "pbcOp",
                        "ufst"
                    })
                    public static class ICMSPart {

                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String orig;
                        @XmlElement(name = "CST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String cst;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String modBC;
                        @XmlElement(name = "vBC", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vbc;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pRedBC;
                        @XmlElement(name = "pICMS", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String picms;
                        @XmlElement(name = "vICMS", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vicms;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String modBCST;
                        @XmlElement(name = "pMVAST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pmvast;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pRedBCST;
                        @XmlElement(name = "vBCST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vbcst;
                        @XmlElement(name = "pICMSST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String picmsst;
                        @XmlElement(name = "vICMSST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vicmsst;
                        @XmlElement(name = "vBCFCPST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbcfcpst;
                        @XmlElement(name = "pFCPST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pfcpst;
                        @XmlElement(name = "vFCPST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vfcpst;
                        @XmlElement(name = "pBCOp", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String pbcOp;
                        @XmlElement(name = "UFST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        @XmlSchemaType(name = "string")
                        protected Uf ufst;
                        
                        public String getOrig() {
                            return orig;
                        }
                        
                        public void setOrig(String value) {
                            this.orig = value;
                        }

                        public String getCST() {
                            return cst;
                        }
                        
                        public void setCST(String value) {
                            this.cst = value;
                        }

                        public String getModBC() {
                            return modBC;
                        }
                        
                        public void setModBC(String value) {
                            this.modBC = value;
                        }
                        
                        public String getVBC() {
                            return vbc;
                        }

                        public void setVBC(String value) {
                            this.vbc = value;
                        }
                        
                        public String getPRedBC() {
                            return pRedBC;
                        }

                        public void setPRedBC(String value) {
                            this.pRedBC = value;
                        }

                        public String getPICMS() {
                            return picms;
                        }
                        
                        public void setPICMS(String value) {
                            this.picms = value;
                        }
                        
                        public String getVICMS() {
                            return vicms;
                        }
                        
                        public void setVICMS(String value) {
                            this.vicms = value;
                        }

                        public String getModBCST() {
                            return modBCST;
                        }

                        public void setModBCST(String value) {
                            this.modBCST = value;
                        }

                        public String getPMVAST() {
                            return pmvast;
                        }
                        
                        public void setPMVAST(String value) {
                            this.pmvast = value;
                        }

                        public String getPRedBCST() {
                            return pRedBCST;
                        }
                        
                        public void setPRedBCST(String value) {
                            this.pRedBCST = value;
                        }

                        public String getVBCST() {
                            return vbcst;
                        }

                        public void setVBCST(String value) {
                            this.vbcst = value;
                        }

                        public String getPICMSST() {
                            return picmsst;
                        }

                        public void setPICMSST(String value) {
                            this.picmsst = value;
                        }
                        
                        public String getVICMSST() {
                            return vicmsst;
                        }

                        public void setVICMSST(String value) {
                            this.vicmsst = value;
                        }

                        public String getVBCFCPST() {
                            return vbcfcpst;
                        }
                        
                        public void setVBCFCPST(String value) {
                            this.vbcfcpst = value;
                        }

                        public String getPFCPST() {
                            return pfcpst;
                        }

                        public void setPFCPST(String value) {
                            this.pfcpst = value;
                        }
                        
                        public String getVFCPST() {
                            return vfcpst;
                        }

                        public void setVFCPST(String value) {
                            this.vfcpst = value;
                        }
                        
                        public String getPBCOp() {
                            return pbcOp;
                        }
                        
                        public void setPBCOp(String value) {
                            this.pbcOp = value;
                        }
                        
                        public Uf getUFST() {
                            return ufst;
                        }

                        public void setUFST(Uf value) {
                            this.ufst = value;
                        }

                    }

                    
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "orig",
                        "csosn",
                        "pCredSN",
                        "vCredICMSSN"
                    })
                    public static class ICMSSN101 {

                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String orig;
                        @XmlElement(name = "CSOSN", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String csosn;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String pCredSN;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vCredICMSSN;
                        
                        public String getOrig() {
                            return orig;
                        }

                        public void setOrig(String value) {
                            this.orig = value;
                        }

                        public String getCSOSN() {
                            return csosn;
                        }

                        public void setCSOSN(String value) {
                            this.csosn = value;
                        }

                        public String getPCredSN() {
                            return pCredSN;
                        }

                        public void setPCredSN(String value) {
                            this.pCredSN = value;
                        }
                        
                        public String getVCredICMSSN() {
                            return vCredICMSSN;
                        }

                        public void setVCredICMSSN(String value) {
                            this.vCredICMSSN = value;
                        }

                    }


                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "orig",
                        "csosn"
                    })
                    public static class ICMSSN102 {

                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String orig;
                        @XmlElement(name = "CSOSN", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String csosn;

                        public String getOrig() {
                            return orig;
                        }
                        
                        public void setOrig(String value) {
                            this.orig = value;
                        }
                        
                        public String getCSOSN() {
                            return csosn;
                        }
                        
                        public void setCSOSN(String value) {
                            this.csosn = value;
                        }

                    }


                    
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "orig",
                        "csosn",
                        "modBCST",
                        "pmvast",
                        "pRedBCST",
                        "vbcst",
                        "picmsst",
                        "vicmsst",
                        "vbcfcpst",
                        "pfcpst",
                        "vfcpst",
                        "pCredSN",
                        "vCredICMSSN"
                    })
                    public static class ICMSSN201 {

                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String orig;
                        @XmlElement(name = "CSOSN", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String csosn;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String modBCST;
                        @XmlElement(name = "pMVAST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pmvast;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pRedBCST;
                        @XmlElement(name = "vBCST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vbcst;
                        @XmlElement(name = "pICMSST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String picmsst;
                        @XmlElement(name = "vICMSST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vicmsst;
                        @XmlElement(name = "vBCFCPST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbcfcpst;
                        @XmlElement(name = "pFCPST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pfcpst;
                        @XmlElement(name = "vFCPST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vfcpst;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String pCredSN;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vCredICMSSN;

                        public String getOrig() {
                            return orig;
                        }

                        public void setOrig(String value) {
                            this.orig = value;
                        }
                        
                        public String getCSOSN() {
                            return csosn;
                        }
                        
                        public void setCSOSN(String value) {
                            this.csosn = value;
                        }
                        
                        public String getModBCST() {
                            return modBCST;
                        }

                        public void setModBCST(String value) {
                            this.modBCST = value;
                        }

                        public String getPMVAST() {
                            return pmvast;
                        }

                        public void setPMVAST(String value) {
                            this.pmvast = value;
                        }

                        public String getPRedBCST() {
                            return pRedBCST;
                        }

                        public void setPRedBCST(String value) {
                            this.pRedBCST = value;
                        }

                        public String getVBCST() {
                            return vbcst;
                        }

                        public void setVBCST(String value) {
                            this.vbcst = value;
                        }

                        public String getPICMSST() {
                            return picmsst;
                        }

                        public void setPICMSST(String value) {
                            this.picmsst = value;
                        }

                        public String getVICMSST() {
                            return vicmsst;
                        }
                        
                        public void setVICMSST(String value) {
                            this.vicmsst = value;
                        }
                        
                        public String getVBCFCPST() {
                            return vbcfcpst;
                        }

                        public void setVBCFCPST(String value) {
                            this.vbcfcpst = value;
                        }
                        
                        public String getPFCPST() {
                            return pfcpst;
                        }

                        public void setPFCPST(String value) {
                            this.pfcpst = value;
                        }

                        public String getVFCPST() {
                            return vfcpst;
                        }

                        public void setVFCPST(String value) {
                            this.vfcpst = value;
                        }
                        
                        public String getPCredSN() {
                            return pCredSN;
                        }

                        public void setPCredSN(String value) {
                            this.pCredSN = value;
                        }

                        public String getVCredICMSSN() {
                            return vCredICMSSN;
                        }

                        public void setVCredICMSSN(String value) {
                            this.vCredICMSSN = value;
                        }

                    }


                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "orig",
                        "csosn",
                        "modBCST",
                        "pmvast",
                        "pRedBCST",
                        "vbcst",
                        "picmsst",
                        "vicmsst",
                        "vbcfcpst",
                        "pfcpst",
                        "vfcpst"
                    })
                    public static class ICMSSN202 {

                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String orig;
                        @XmlElement(name = "CSOSN", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String csosn;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String modBCST;
                        @XmlElement(name = "pMVAST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pmvast;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pRedBCST;
                        @XmlElement(name = "vBCST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vbcst;
                        @XmlElement(name = "pICMSST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String picmsst;
                        @XmlElement(name = "vICMSST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vicmsst;
                        @XmlElement(name = "vBCFCPST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbcfcpst;
                        @XmlElement(name = "pFCPST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pfcpst;
                        @XmlElement(name = "vFCPST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vfcpst;
                        
                        public String getOrig() {
                            return orig;
                        }
                        
                        public void setOrig(String value) {
                            this.orig = value;
                        }
                        
                        public String getCSOSN() {
                            return csosn;
                        }
                        
                        public void setCSOSN(String value) {
                            this.csosn = value;
                        }

                        public String getModBCST() {
                            return modBCST;
                        }

                        public void setModBCST(String value) {
                            this.modBCST = value;
                        }

                        public String getPMVAST() {
                            return pmvast;
                        }

                        public void setPMVAST(String value) {
                            this.pmvast = value;
                        }

                        public String getPRedBCST() {
                            return pRedBCST;
                        }

                        public void setPRedBCST(String value) {
                            this.pRedBCST = value;
                        }

                        public String getVBCST() {
                            return vbcst;
                        }
                        
                        public void setVBCST(String value) {
                            this.vbcst = value;
                        }

                        public String getPICMSST() {
                            return picmsst;
                        }

                        public void setPICMSST(String value) {
                            this.picmsst = value;
                        }

                        public String getVICMSST() {
                            return vicmsst;
                        }

                        public void setVICMSST(String value) {
                            this.vicmsst = value;
                        }

                        public String getVBCFCPST() {
                            return vbcfcpst;
                        }

                        public void setVBCFCPST(String value) {
                            this.vbcfcpst = value;
                        }

                        public String getPFCPST() {
                            return pfcpst;
                        }

                        public void setPFCPST(String value) {
                            this.pfcpst = value;
                        }
                        
                        public String getVFCPST() {
                            return vfcpst;
                        }

                        public void setVFCPST(String value) {
                            this.vfcpst = value;
                        }

                    }


                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "orig",
                        "csosn",
                        "vbcstRet",
                        "pst",
                        "vicmsSubstituto",
                        "vicmsstRet",
                        "vbcfcpstRet",
                        "pfcpstRet",
                        "vfcpstRet",
                        "pRedBCEfet",
                        "vbcEfet",
                        "picmsEfet",
                        "vicmsEfet"
                    })
                    public static class ICMSSN500 {

                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String orig;
                        @XmlElement(name = "CSOSN", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String csosn;
                        @XmlElement(name = "vBCSTRet", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbcstRet;
                        @XmlElement(name = "pST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pst;
                        @XmlElement(name = "vICMSSubstituto", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vicmsSubstituto;
                        @XmlElement(name = "vICMSSTRet", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vicmsstRet;
                        @XmlElement(name = "vBCFCPSTRet", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbcfcpstRet;
                        @XmlElement(name = "pFCPSTRet", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pfcpstRet;
                        @XmlElement(name = "vFCPSTRet", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vfcpstRet;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pRedBCEfet;
                        @XmlElement(name = "vBCEfet", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbcEfet;
                        @XmlElement(name = "pICMSEfet", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String picmsEfet;
                        @XmlElement(name = "vICMSEfet", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vicmsEfet;

                        public String getOrig() {
                            return orig;
                        }
                        
                        public void setOrig(String value) {
                            this.orig = value;
                        }

                        public String getCSOSN() {
                            return csosn;
                        }
                        
                        public void setCSOSN(String value) {
                            this.csosn = value;
                        }

                        public String getVBCSTRet() {
                            return vbcstRet;
                        }

                        public void setVBCSTRet(String value) {
                            this.vbcstRet = value;
                        }

                        public String getPST() {
                            return pst;
                        }

                        public void setPST(String value) {
                            this.pst = value;
                        }

                        public String getVICMSSubstituto() {
                            return vicmsSubstituto;
                        }

                        public void setVICMSSubstituto(String value) {
                            this.vicmsSubstituto = value;
                        }

                        public String getVICMSSTRet() {
                            return vicmsstRet;
                        }

                        public void setVICMSSTRet(String value) {
                            this.vicmsstRet = value;
                        }

                        public String getVBCFCPSTRet() {
                            return vbcfcpstRet;
                        }

                        public void setVBCFCPSTRet(String value) {
                            this.vbcfcpstRet = value;
                        }

                        public String getPFCPSTRet() {
                            return pfcpstRet;
                        }

                        public void setPFCPSTRet(String value) {
                            this.pfcpstRet = value;
                        }

                        public String getVFCPSTRet() {
                            return vfcpstRet;
                        }

                        public void setVFCPSTRet(String value) {
                            this.vfcpstRet = value;
                        }

                        public String getPRedBCEfet() {
                            return pRedBCEfet;
                        }

                        public void setPRedBCEfet(String value) {
                            this.pRedBCEfet = value;
                        }
                        
                        public String getVBCEfet() {
                            return vbcEfet;
                        }

                        public void setVBCEfet(String value) {
                            this.vbcEfet = value;
                        }

                        public String getPICMSEfet() {
                            return picmsEfet;
                        }

                        public void setPICMSEfet(String value) {
                            this.picmsEfet = value;
                        }

                        public String getVICMSEfet() {
                            return vicmsEfet;
                        }

                        public void setVICMSEfet(String value) {
                            this.vicmsEfet = value;
                        }
                    }


                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "orig",
                        "csosn",
                        "modBC",
                        "vbc",
                        "pRedBC",
                        "picms",
                        "vicms",
                        "modBCST",
                        "pmvast",
                        "pRedBCST",
                        "vbcst",
                        "picmsst",
                        "vicmsst",
                        "vbcfcpst",
                        "pfcpst",
                        "vfcpst",
                        "pCredSN",
                        "vCredICMSSN"
                    })
                    public static class ICMSSN900 {

                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String orig;
                        @XmlElement(name = "CSOSN", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String csosn;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String modBC;
                        @XmlElement(name = "vBC", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbc;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pRedBC;
                        @XmlElement(name = "pICMS", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String picms;
                        @XmlElement(name = "vICMS", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vicms;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String modBCST;
                        @XmlElement(name = "pMVAST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pmvast;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pRedBCST;
                        @XmlElement(name = "vBCST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbcst;
                        @XmlElement(name = "pICMSST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String picmsst;
                        @XmlElement(name = "vICMSST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vicmsst;
                        @XmlElement(name = "vBCFCPST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbcfcpst;
                        @XmlElement(name = "pFCPST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pfcpst;
                        @XmlElement(name = "vFCPST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vfcpst;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pCredSN;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vCredICMSSN;

                        public String getOrig() {
                            return orig;
                        }
                        
                        public void setOrig(String value) {
                            this.orig = value;
                        }
                        
                        public String getCSOSN() {
                            return csosn;
                        }
                        
                        public void setCSOSN(String value) {
                            this.csosn = value;
                        }
                        
                        public String getModBC() {
                            return modBC;
                        }

                        public void setModBC(String value) {
                            this.modBC = value;
                        }

                        public String getVBC() {
                            return vbc;
                        }

                        public void setVBC(String value) {
                            this.vbc = value;
                        }

                        public String getPRedBC() {
                            return pRedBC;
                        }

                        public void setPRedBC(String value) {
                            this.pRedBC = value;
                        }

                        public String getPICMS() {
                            return picms;
                        }

                        public void setPICMS(String value) {
                            this.picms = value;
                        }

                        public String getVICMS() {
                            return vicms;
                        }

                        public void setVICMS(String value) {
                            this.vicms = value;
                        }

                        public String getModBCST() {
                            return modBCST;
                        }
                        
                        public void setModBCST(String value) {
                            this.modBCST = value;
                        }

                        public String getPMVAST() {
                            return pmvast;
                        }

                        public void setPMVAST(String value) {
                            this.pmvast = value;
                        }

                        public String getPRedBCST() {
                            return pRedBCST;
                        }

                        public void setPRedBCST(String value) {
                            this.pRedBCST = value;
                        }

                        public String getVBCST() {
                            return vbcst;
                        }

                        public void setVBCST(String value) {
                            this.vbcst = value;
                        }

                        public String getPICMSST() {
                            return picmsst;
                        }
                        
                        public void setPICMSST(String value) {
                            this.picmsst = value;
                        }

                        public String getVICMSST() {
                            return vicmsst;
                        }

                        public void setVICMSST(String value) {
                            this.vicmsst = value;
                        }

                        public String getVBCFCPST() {
                            return vbcfcpst;
                        }
                        
                        public void setVBCFCPST(String value) {
                            this.vbcfcpst = value;
                        }

                        public String getPFCPST() {
                            return pfcpst;
                        }
                        
                        public void setPFCPST(String value) {
                            this.pfcpst = value;
                        }
                        
                        public String getVFCPST() {
                            return vfcpst;
                        }

                        public void setVFCPST(String value) {
                            this.vfcpst = value;
                        }

                        public String getPCredSN() {
                            return pCredSN;
                        }

                        public void setPCredSN(String value) {
                            this.pCredSN = value;
                        }

                        public String getVCredICMSSN() {
                            return vCredICMSSN;
                        }

                        public void setVCredICMSSN(String value) {
                            this.vCredICMSSN = value;
                        }

                    }


                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "orig",
                        "cst",
                        "vbcstRet",
                        "pst",
                        "vicmsSubstituto",
                        "vicmsstRet",
                        "vbcfcpstRet",
                        "pfcpstRet",
                        "vfcpstRet",
                        "vbcstDest",
                        "vicmsstDest",
                        "pRedBCEfet",
                        "vbcEfet",
                        "picmsEfet",
                        "vicmsEfet"
                    })
                    public static class ICMSST {

                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String orig;
                        @XmlElement(name = "CST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String cst;
                        @XmlElement(name = "vBCSTRet", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vbcstRet;
                        @XmlElement(name = "pST", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pst;
                        @XmlElement(name = "vICMSSubstituto", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vicmsSubstituto;
                        @XmlElement(name = "vICMSSTRet", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vicmsstRet;
                        @XmlElement(name = "vBCFCPSTRet", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbcfcpstRet;
                        @XmlElement(name = "pFCPSTRet", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pfcpstRet;
                        @XmlElement(name = "vFCPSTRet", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vfcpstRet;
                        @XmlElement(name = "vBCSTDest", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vbcstDest;
                        @XmlElement(name = "vICMSSTDest", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vicmsstDest;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String pRedBCEfet;
                        @XmlElement(name = "vBCEfet", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbcEfet;
                        @XmlElement(name = "pICMSEfet", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String picmsEfet;
                        @XmlElement(name = "vICMSEfet", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vicmsEfet;

                        public String getOrig() {
                            return orig;
                        }
                        
                        public void setOrig(String value) {
                            this.orig = value;
                        }
                        
                        public String getCST() {
                            return cst;
                        }
                        
                        public void setCST(String value) {
                            this.cst = value;
                        }
                        
                        public String getVBCSTRet() {
                            return vbcstRet;
                        }
                        
                        public void setVBCSTRet(String value) {
                            this.vbcstRet = value;
                        }

                        public String getPST() {
                            return pst;
                        }

                        public void setPST(String value) {
                            this.pst = value;
                        }
                        
                        public String getVICMSSubstituto() {
                            return vicmsSubstituto;
                        }

                        public void setVICMSSubstituto(String value) {
                            this.vicmsSubstituto = value;
                        }

                        public String getVICMSSTRet() {
                            return vicmsstRet;
                        }
                        
                        public void setVICMSSTRet(String value) {
                            this.vicmsstRet = value;
                        }
                        
                        public String getVBCFCPSTRet() {
                            return vbcfcpstRet;
                        }
                        
                        public void setVBCFCPSTRet(String value) {
                            this.vbcfcpstRet = value;
                        }
                        
                        public String getPFCPSTRet() {
                            return pfcpstRet;
                        }

                        public void setPFCPSTRet(String value) {
                            this.pfcpstRet = value;
                        }

                        public String getVFCPSTRet() {
                            return vfcpstRet;
                        }

                        public void setVFCPSTRet(String value) {
                            this.vfcpstRet = value;
                        }

                        public String getVBCSTDest() {
                            return vbcstDest;
                        }
                        
                        public void setVBCSTDest(String value) {
                            this.vbcstDest = value;
                        }

                        public String getVICMSSTDest() {
                            return vicmsstDest;
                        }

                        public void setVICMSSTDest(String value) {
                            this.vicmsstDest = value;
                        }

                        public String getPRedBCEfet() {
                            return pRedBCEfet;
                        }

                        public void setPRedBCEfet(String value) {
                            this.pRedBCEfet = value;
                        }

                        public String getVBCEfet() {
                            return vbcEfet;
                        }
                        
                        public void setVBCEfet(String value) {
                            this.vbcEfet = value;
                        }

                        public String getPICMSEfet() {
                            return picmsEfet;
                        }

                        public void setPICMSEfet(String value) {
                            this.picmsEfet = value;
                        }

                        public String getVICMSEfet() {
                            return vicmsEfet;
                        }

                        public void setVICMSEfet(String value) {
                            this.vicmsEfet = value;
                        }

                    }

                }


                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "vbcufDest",
                    "vbcfcpufDest",
                    "pfcpufDest",
                    "picmsufDest",
                    "picmsInter",
                    "picmsInterPart",
                    "vfcpufDest",
                    "vicmsufDest",
                    "vicmsufRemet"
                })
                public static class ICMSUFDest {

                    @XmlElement(name = "vBCUFDest", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String vbcufDest;
                    @XmlElement(name = "vBCFCPUFDest", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String vbcfcpufDest;
                    @XmlElement(name = "pFCPUFDest", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String pfcpufDest;
                    @XmlElement(name = "pICMSUFDest", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String picmsufDest;
                    @XmlElement(name = "pICMSInter", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String picmsInter;
                    @XmlElement(name = "pICMSInterPart", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String picmsInterPart;
                    @XmlElement(name = "vFCPUFDest", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String vfcpufDest;
                    @XmlElement(name = "vICMSUFDest", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String vicmsufDest;
                    @XmlElement(name = "vICMSUFRemet", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String vicmsufRemet;

                    public String getVBCUFDest() {
                        return vbcufDest;
                    }

                    public void setVBCUFDest(String value) {
                        this.vbcufDest = value;
                    }

                    public String getVBCFCPUFDest() {
                        return vbcfcpufDest;
                    }

                    public void setVBCFCPUFDest(String value) {
                        this.vbcfcpufDest = value;
                    }
                    
                    public String getPFCPUFDest() {
                        return pfcpufDest;
                    }

                    public void setPFCPUFDest(String value) {
                        this.pfcpufDest = value;
                    }

                    public String getPICMSUFDest() {
                        return picmsufDest;
                    }

                    public void setPICMSUFDest(String value) {
                        this.picmsufDest = value;
                    }

                    public String getPICMSInter() {
                        return picmsInter;
                    }

                    public void setPICMSInter(String value) {
                        this.picmsInter = value;
                    }

                    public String getPICMSInterPart() {
                        return picmsInterPart;
                    }

                    public void setPICMSInterPart(String value) {
                        this.picmsInterPart = value;
                    }

                    public String getVFCPUFDest() {
                        return vfcpufDest;
                    }
                    
                    public void setVFCPUFDest(String value) {
                        this.vfcpufDest = value;
                    }

                    public String getVICMSUFDest() {
                        return vicmsufDest;
                    }

                    public void setVICMSUFDest(String value) {
                        this.vicmsufDest = value;
                    }

                    public String getVICMSUFRemet() {
                        return vicmsufRemet;
                    }

                    public void setVICMSUFRemet(String value) {
                        this.vicmsufRemet = value;
                    }

                }


                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "vbc",
                    "vDespAdu",
                    "vii",
                    "viof"
                })
                public static class II {

                    @XmlElement(name = "vBC", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String vbc;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String vDespAdu;
                    @XmlElement(name = "vII", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String vii;
                    @XmlElement(name = "vIOF", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String viof;

                    public String getVBC() {
                        return vbc;
                    }
                    
                    public void setVBC(String value) {
                        this.vbc = value;
                    }
                    
                    public String getVDespAdu() {
                        return vDespAdu;
                    }
                    
                    public void setVDespAdu(String value) {
                        this.vDespAdu = value;
                    }

                    public String getVII() {
                        return vii;
                    }

                    public void setVII(String value) {
                        this.vii = value;
                    }

                    public String getVIOF() {
                        return viof;
                    }

                    public void setVIOF(String value) {
                        this.viof = value;
                    }

                }


                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "vbc",
                    "vAliq",
                    "vissqn",
                    "cMunFG",
                    "cListServ",
                    "vDeducao",
                    "vOutro",
                    "vDescIncond",
                    "vDescCond",
                    "vissRet",
                    "indISS",
                    "cServico",
                    "cMun",
                    "cPais",
                    "nProcesso",
                    "indIncentivo"
                })
                public static class ISSQN {

                    @XmlElement(name = "vBC", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String vbc;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String vAliq;
                    @XmlElement(name = "vISSQN", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String vissqn;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String cMunFG;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String cListServ;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String vDeducao;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String vOutro;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String vDescIncond;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String vDescCond;
                    @XmlElement(name = "vISSRet", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String vissRet;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String indISS;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String cServico;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String cMun;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String cPais;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String nProcesso;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String indIncentivo;

                    public String getVBC() {
                        return vbc;
                    }
                    
                    public void setVBC(String value) {
                        this.vbc = value;
                    }
                    
                    public String getVAliq() {
                        return vAliq;
                    }
                    
                    public void setVAliq(String value) {
                        this.vAliq = value;
                    }

                    public String getVISSQN() {
                        return vissqn;
                    }

                    public void setVISSQN(String value) {
                        this.vissqn = value;
                    }

                    public String getCMunFG() {
                        return cMunFG;
                    }

                    public void setCMunFG(String value) {
                        this.cMunFG = value;
                    }

                    public String getCListServ() {
                        return cListServ;
                    }
                    
                    public void setCListServ(String value) {
                        this.cListServ = value;
                    }

                    public String getVDeducao() {
                        return vDeducao;
                    }

                    public void setVDeducao(String value) {
                        this.vDeducao = value;
                    }

                    public String getVOutro() {
                        return vOutro;
                    }

                    public void setVOutro(String value) {
                        this.vOutro = value;
                    }

                    public String getVDescIncond() {
                        return vDescIncond;
                    }

                    public void setVDescIncond(String value) {
                        this.vDescIncond = value;
                    }

                    public String getVDescCond() {
                        return vDescCond;
                    }

                    public void setVDescCond(String value) {
                        this.vDescCond = value;
                    }

                    public String getVISSRet() {
                        return vissRet;
                    }

                    public void setVISSRet(String value) {
                        this.vissRet = value;
                    }

                    public String getIndISS() {
                        return indISS;
                    }

                    public void setIndISS(String value) {
                        this.indISS = value;
                    }

                    public String getCServico() {
                        return cServico;
                    }

                    public void setCServico(String value) {
                        this.cServico = value;
                    }

                    public String getCMun() {
                        return cMun;
                    }

                    public void setCMun(String value) {
                        this.cMun = value;
                    }

                    public String getCPais() {
                        return cPais;
                    }

                    public void setCPais(String value) {
                        this.cPais = value;
                    }

                    public String getNProcesso() {
                        return nProcesso;
                    }

                    public void setNProcesso(String value) {
                        this.nProcesso = value;
                    }

                    public String getIndIncentivo() {
                        return indIncentivo;
                    }

                    public void setIndIncentivo(String value) {
                        this.indIncentivo = value;
                    }

                }


                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "pisAliq",
                    "pisQtde",
                    "pisnt",
                    "pisOutr"
                })
                public static class PIS {

                    @XmlElement(name = "PISAliq", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.PIS.PISAliq pisAliq;
                    @XmlElement(name = "PISQtde", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.PIS.PISQtde pisQtde;
                    @XmlElement(name = "PISNT", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.PIS.PISNT pisnt;
                    @XmlElement(name = "PISOutr", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Imposto.PIS.PISOutr pisOutr;

                    public NFe.InfNFe.Det.Imposto.PIS.PISAliq getPISAliq() {
                        return pisAliq;
                    }

                    public void setPISAliq(NFe.InfNFe.Det.Imposto.PIS.PISAliq value) {
                        this.pisAliq = value;
                    }
                    
                    public NFe.InfNFe.Det.Imposto.PIS.PISQtde getPISQtde() {
                        return pisQtde;
                    }

                    public void setPISQtde(NFe.InfNFe.Det.Imposto.PIS.PISQtde value) {
                        this.pisQtde = value;
                    }

                    public NFe.InfNFe.Det.Imposto.PIS.PISNT getPISNT() {
                        return pisnt;
                    }

                    public void setPISNT(NFe.InfNFe.Det.Imposto.PIS.PISNT value) {
                        this.pisnt = value;
                    }

                    public NFe.InfNFe.Det.Imposto.PIS.PISOutr getPISOutr() {
                        return pisOutr;
                    }

                    public void setPISOutr(NFe.InfNFe.Det.Imposto.PIS.PISOutr value) {
                        this.pisOutr = value;
                    }

                    
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "cst",
                        "vbc",
                        "ppis",
                        "vpis"
                    })
                    public static class PISAliq {

                        @XmlElement(name = "CST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String cst;
                        @XmlElement(name = "vBC", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vbc;
                        @XmlElement(name = "pPIS", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String ppis;
                        @XmlElement(name = "vPIS", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vpis;

                        public String getCST() {
                            return cst;
                        }

                        public void setCST(String value) {
                            this.cst = value;
                        }

                        public String getVBC() {
                            return vbc;
                        }

                        public void setVBC(String value) {
                            this.vbc = value;
                        }

                        public String getPPIS() {
                            return ppis;
                        }

                        public void setPPIS(String value) {
                            this.ppis = value;
                        }

                        public String getVPIS() {
                            return vpis;
                        }

                        public void setVPIS(String value) {
                            this.vpis = value;
                        }

                    }


                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "cst"
                    })
                    public static class PISNT {

                        @XmlElement(name = "CST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String cst;

                        public String getCST() {
                            return cst;
                        }

                        public void setCST(String value) {
                            this.cst = value;
                        }

                    }


                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "cst",
                        "vbc",
                        "ppis",
                        "qbcProd",
                        "vAliqProd",
                        "vpis"
                    })
                    public static class PISOutr {

                        @XmlElement(name = "CST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String cst;
                        @XmlElement(name = "vBC", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vbc;
                        @XmlElement(name = "pPIS", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String ppis;
                        @XmlElement(name = "qBCProd", namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String qbcProd;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vAliqProd;
                        @XmlElement(name = "vPIS", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vpis;

                        public String getCST() {
                            return cst;
                        }

                        public void setCST(String value) {
                            this.cst = value;
                        }

                        public String getVBC() {
                            return vbc;
                        }

                        public void setVBC(String value) {
                            this.vbc = value;
                        }

                        public String getPPIS() {
                            return ppis;
                        }

                        public void setPPIS(String value) {
                            this.ppis = value;
                        }

                        public String getQBCProd() {
                            return qbcProd;
                        }

                        public void setQBCProd(String value) {
                            this.qbcProd = value;
                        }
                        
                        public String getVAliqProd() {
                            return vAliqProd;
                        }

                        public void setVAliqProd(String value) {
                            this.vAliqProd = value;
                        }
                        
                        public String getVPIS() {
                            return vpis;
                        }

                        public void setVPIS(String value) {
                            this.vpis = value;
                        }

                    }


                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "cst",
                        "qbcProd",
                        "vAliqProd",
                        "vpis"
                    })
                    public static class PISQtde {

                        @XmlElement(name = "CST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String cst;
                        @XmlElement(name = "qBCProd", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String qbcProd;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vAliqProd;
                        @XmlElement(name = "vPIS", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vpis;

                        public String getCST() {
                            return cst;
                        }

                        public void setCST(String value) {
                            this.cst = value;
                        }

                        public String getQBCProd() {
                            return qbcProd;
                        }

                        public void setQBCProd(String value) {
                            this.qbcProd = value;
                        }

                        public String getVAliqProd() {
                            return vAliqProd;
                        }

                        public void setVAliqProd(String value) {
                            this.vAliqProd = value;
                        }

                        public String getVPIS() {
                            return vpis;
                        }

                        public void setVPIS(String value) {
                            this.vpis = value;
                        }

                    }

                }

                
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "vbc",
                    "ppis",
                    "qbcProd",
                    "vAliqProd",
                    "vpis",
                    "indSomaPISST"
                })
                public static class PISST {

                    @XmlElement(name = "vBC", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String vbc;
                    @XmlElement(name = "pPIS", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String ppis;
                    @XmlElement(name = "qBCProd", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String qbcProd;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String vAliqProd;
                    @XmlElement(name = "vPIS", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String vpis;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String indSomaPISST;

                    public String getVBC() {
                        return vbc;
                    }
                    
                    public void setVBC(String value) {
                        this.vbc = value;
                    }
                    
                    public String getPPIS() {
                        return ppis;
                    }
                    
                    public void setPPIS(String value) {
                        this.ppis = value;
                    }
                    
                    public String getQBCProd() {
                        return qbcProd;
                    }
                    
                    public void setQBCProd(String value) {
                        this.qbcProd = value;
                    }
                    
                    public String getVAliqProd() {
                        return vAliqProd;
                    }
                    
                    public void setVAliqProd(String value) {
                        this.vAliqProd = value;
                    }

                    public String getVPIS() {
                        return vpis;
                    }

                    public void setVPIS(String value) {
                        this.vpis = value;
                    }

                    public String getIndSomaPISST() {
                        return indSomaPISST;
                    }

                    public void setIndSomaPISST(String value) {
                        this.indSomaPISST = value;
                    }

                }
                
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "cEnq",
                    "IPINT",
                    "IPITrib"
                })
                public static class IPI {
                	@XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                	protected String cEnq;
                	@XmlElement(name = "IPINT", namespace = "http://www.portalfiscal.inf.br/nfe")
                	protected NFe.InfNFe.Det.Imposto.IPI.IPINT IPINT;
                	@XmlElement(name = "IPITrib", namespace = "http://www.portalfiscal.inf.br/nfe")
                	protected NFe.InfNFe.Det.Imposto.IPI.IPITrib IPITrib;

                	public String getcEnq() {
						return cEnq;
					}

					public void setcEnq(String cEnq) {
						this.cEnq = cEnq;
					}

					public NFe.InfNFe.Det.Imposto.IPI.IPINT getIPINT() {
						return IPINT;
					}

					public void setIPINT(NFe.InfNFe.Det.Imposto.IPI.IPINT iPINT) {
						IPINT = iPINT;
					}

					public NFe.InfNFe.Det.Imposto.IPI.IPITrib getIPITrib() {
						return IPITrib;
					}

					public void setIPITrib(NFe.InfNFe.Det.Imposto.IPI.IPITrib iPITrib) {
						IPITrib = iPITrib;
					}

					@XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "cst"
                    })
                    public static class IPINT {

                        @XmlElement(name = "CST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String cst;

                        public String getCST() {
                            return cst;
                        }

                        public void setCST(String value) {
                            this.cst = value;
                        }
                    }
                	
                	@XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "cst",
                        "vBC",
                        "pIPI",
                        "vIPI"
                    })
                    public static class IPITrib {

                        @XmlElement(name = "CST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String cst;
                        @XmlElement(name = "vBC", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vBC;
                        @XmlElement(name = "pIPI", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String pIPI;
                        @XmlElement(name = "vIPI", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vIPI;

                        public String getCST() {
                            return cst;
                        }

                        public void setCST(String value) {
                            this.cst = value;
                        }

						public String getCst() {
							return cst;
						}

						public void setCst(String cst) {
							this.cst = cst;
						}

						public String getvBC() {
							return vBC;
						}

						public void setvBC(String vBC) {
							this.vBC = vBC;
						}

						public String getpIPI() {
							return pIPI;
						}

						public void setpIPI(String pIPI) {
							this.pIPI = pIPI;
						}

						public String getvIPI() {
							return vIPI;
						}

						public void setvIPI(String vIPI) {
							this.vIPI = vIPI;
						}
                    }
                }
            }

            
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "pDevol",
                "ipi"
            })
            public static class ImpostoDevol {

                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String pDevol;
                @XmlElement(name = "IPI", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected NFe.InfNFe.Det.ImpostoDevol.IPIDevol ipi;
                
                public String getPDevol() {
                    return pDevol;
                }

                public void setPDevol(String value) {
                    this.pDevol = value;
                }

                public NFe.InfNFe.Det.ImpostoDevol.IPIDevol getIPI() {
                    return ipi;
                }

                public void setIPI(NFe.InfNFe.Det.ImpostoDevol.IPIDevol value) {
                    this.ipi = value;
                }

                
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "vipiDevol"
                })
                public static class IPIDevol {

                    @XmlElement(name = "vIPIDevol", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String vipiDevol;

                    public String getVIPIDevol() {
                        return vipiDevol;
                    }

                    public void setVIPIDevol(String value) {
                        this.vipiDevol = value;
                    }

                }

            }


            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "obsCont",
                "obsFisco"
            })
            public static class ObsItem {

                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected NFe.InfNFe.Det.ObsItem.ObsCont obsCont;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected NFe.InfNFe.Det.ObsItem.ObsFisco obsFisco;

                
//                public NFe.InfNFe.Det.ObsItem.ObsCont getObsCont() {
//                    return obsCont;
//                }
                
//                public void setObsCont(NFe.InfNFe.Det.ObsItem.ObsCont value) {
//                    this.obsCont = value;
//                }
                
                public NFe.InfNFe.Det.ObsItem.ObsFisco getObsFisco() {
                    return obsFisco;
                }

                public void setObsFisco(NFe.InfNFe.Det.ObsItem.ObsFisco value) {
                    this.obsFisco = value;
                }
                
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "xTexto"
                })
                public static class ObsCont {

                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String xTexto;
                    @XmlAttribute(name = "xCampo", required = true)
                    protected String xCampo;
                    
                    public String getXTexto() {
                        return xTexto;
                    }
                    
                    public void setXTexto(String value) {
                        this.xTexto = value;
                    }
                    
                    public String getXCampo() {
                        return xCampo;
                    }
                    
                    public void setXCampo(String value) {
                        this.xCampo = value;
                    }

                }


                
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "xTexto"
                })
                public static class ObsFisco {

                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String xTexto;
                    @XmlAttribute(name = "xCampo", required = true)
                    protected String xCampo;
                    
                    public String getXTexto() {
                        return xTexto;
                    }
                    
                    public void setXTexto(String value) {
                        this.xTexto = value;
                    }

                    public String getXCampo() {
                        return xCampo;
                    }
                    
                    public void setXCampo(String value) {
                        this.xCampo = value;
                    }

                }

            }

            
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "cProd",
                "cean",
                "cBarra",
                "xProd",
                "ncm",
                "nve",
                "cest",
                "indEscala",
                "cnpjFab",
                "cBenef",
                "gCred",
                "extipi",
                "cfop",
                "uCom",
                "qCom",
                "vUnCom",
                "vProd",
                "ceanTrib",
                "cBarraTrib",
                "uTrib",
                "qTrib",
                "vUnTrib",
                "vFrete",
                "vSeg",
                "vDesc",
                "vOutro",
                "indTot",
                "di",
                "detExport",
                "xPed",
                "nItemPed",
                "nfci",
                "rastro",
                "infProdNFF",
                "infProdEmb",
                "veicProd",
                "med",
                "arma",
                "comb",
                "nrecopi"
            })
            public static class Prod {

                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String cProd;
                @XmlElement(name = "cEAN", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String cean;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String cBarra;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String xProd;
                @XmlElement(name = "NCM", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String ncm;
                @XmlElement(name = "NVE", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected List<String> nve;
                @XmlElement(name = "CEST", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String cest;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String indEscala;
                @XmlElement(name = "CNPJFab", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String cnpjFab;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String cBenef;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected List<NFe.InfNFe.Det.Prod.GCred> gCred;
                @XmlElement(name = "EXTIPI", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String extipi;
                @XmlElement(name = "CFOP", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String cfop;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String uCom;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String qCom;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vUnCom;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vProd;
                @XmlElement(name = "cEANTrib", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String ceanTrib;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String cBarraTrib;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String uTrib;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String qTrib;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vUnTrib;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vFrete;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vSeg;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vDesc;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vOutro;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String indTot;
                @XmlElement(name = "DI", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected List<NFe.InfNFe.Det.Prod.DI> di;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected List<NFe.InfNFe.Det.Prod.DetExport> detExport;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String xPed;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String nItemPed;
                @XmlElement(name = "nFCI", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String nfci;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected List<NFe.InfNFe.Det.Prod.Rastro> rastro;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected NFe.InfNFe.Det.Prod.InfProdNFF infProdNFF;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected NFe.InfNFe.Det.Prod.InfProdEmb infProdEmb;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected NFe.InfNFe.Det.Prod.VeicProd veicProd;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected NFe.InfNFe.Det.Prod.Med med;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected List<NFe.InfNFe.Det.Prod.Arma> arma;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected NFe.InfNFe.Det.Prod.Comb comb;
                @XmlElement(name = "nRECOPI", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String nrecopi;

                public String getCProd() {
                    return cProd;
                }
                
                public void setCProd(String value) {
                    this.cProd = value;
                }

                public String getCEAN() {
                    return cean;
                }
                
                public void setCEAN(String value) {
                    this.cean = value;
                }

                public String getCBarra() {
                    return cBarra;
                }
                
                public void setCBarra(String value) {
                    this.cBarra = value;
                }
                
                public String getXProd() {
                    return xProd;
                }

                public void setXProd(String value) {
                    this.xProd = value;
                }
                
                public String getNCM() {
                    return ncm;
                }

                public void setNCM(String value) {
                    this.ncm = value;
                }
                
                public List<String> getNVE() {
                    if (nve == null) {
                        nve = new ArrayList<String>();
                    }
                    return this.nve;
                }
                
                public String getCEST() {
                    return cest;
                }

                public void setCEST(String value) {
                    this.cest = value;
                }
                
                public String getIndEscala() {
                    return indEscala;
                }
                
                public void setIndEscala(String value) {
                    this.indEscala = value;
                }

                public String getCNPJFab() {
                    return cnpjFab;
                }
                
                public void setCNPJFab(String value) {
                    this.cnpjFab = value;
                }

                public String getCBenef() {
                    return cBenef;
                }

                public void setCBenef(String value) {
                    this.cBenef = value;
                }

                public List<NFe.InfNFe.Det.Prod.GCred> getGCred() {
                    if (gCred == null) {
                        gCred = new ArrayList<NFe.InfNFe.Det.Prod.GCred>();
                    }
                    return this.gCred;
                }
                
                public String getEXTIPI() {
                    return extipi;
                }

                public void setEXTIPI(String value) {
                    this.extipi = value;
                }
                
                public String getCFOP() {
                    return cfop;
                }

                public void setCFOP(String value) {
                    this.cfop = value;
                }

                public String getUCom() {
                    return uCom;
                }

                public void setUCom(String value) {
                    this.uCom = value;
                }
                
                public String getQCom() {
                    return qCom;
                }
                
                public void setQCom(String value) {
                    this.qCom = value;
                }

                public String getVUnCom() {
                    return vUnCom;
                }

                public void setVUnCom(String value) {
                    this.vUnCom = value;
                }

                public String getVProd() {
                    return vProd;
                }
                
                public void setVProd(String value) {
                    this.vProd = value;
                }

                public String getCEANTrib() {
                    return ceanTrib;
                }
                
                public void setCEANTrib(String value) {
                    this.ceanTrib = value;
                }
                
                public String getCBarraTrib() {
                    return cBarraTrib;
                }
                
                public void setCBarraTrib(String value) {
                    this.cBarraTrib = value;
                }

                public String getUTrib() {
                    return uTrib;
                }

                public void setUTrib(String value) {
                    this.uTrib = value;
                }
                
                public String getQTrib() {
                    return qTrib;
                }

                public void setQTrib(String value) {
                    this.qTrib = value;
                }

                public String getVUnTrib() {
                    return vUnTrib;
                }

                public void setVUnTrib(String value) {
                    this.vUnTrib = value;
                }
                
                public String getVFrete() {
                    return vFrete;
                }
                
                public void setVFrete(String value) {
                    this.vFrete = value;
                }
                
                public String getVSeg() {
                    return vSeg;
                }
                
                public void setVSeg(String value) {
                    this.vSeg = value;
                }

                public String getVDesc() {
                    return vDesc;
                }

                public void setVDesc(String value) {
                    this.vDesc = value;
                }
                
                public String getVOutro() {
                    return vOutro;
                }
                
                public void setVOutro(String value) {
                    this.vOutro = value;
                }
                
                public String getIndTot() {
                    return indTot;
                }
                
                public void setIndTot(String value) {
                    this.indTot = value;
                }
                
                public List<NFe.InfNFe.Det.Prod.DI> getDI() {
                    if (di == null) {
                        di = new ArrayList<NFe.InfNFe.Det.Prod.DI>();
                    }
                    return this.di;
                }

                public List<NFe.InfNFe.Det.Prod.DetExport> getDetExport() {
                    if (detExport == null) {
                        detExport = new ArrayList<NFe.InfNFe.Det.Prod.DetExport>();
                    }
                    return this.detExport;
                }

                public String getXPed() {
                    return xPed;
                }

                public void setXPed(String value) {
                    this.xPed = value;
                }
                
                public String getNItemPed() {
                    return nItemPed;
                }
                
                public void setNItemPed(String value) {
                    this.nItemPed = value;
                }
                
                public String getNFCI() {
                    return nfci;
                }

                public void setNFCI(String value) {
                    this.nfci = value;
                }
                
                public List<NFe.InfNFe.Det.Prod.Rastro> getRastro() {
                    if (rastro == null) {
                        rastro = new ArrayList<NFe.InfNFe.Det.Prod.Rastro>();
                    }
                    return this.rastro;
                }

                public NFe.InfNFe.Det.Prod.InfProdNFF getInfProdNFF() {
                    return infProdNFF;
                }
                
                public void setInfProdNFF(NFe.InfNFe.Det.Prod.InfProdNFF value) {
                    this.infProdNFF = value;
                }

                public NFe.InfNFe.Det.Prod.InfProdEmb getInfProdEmb() {
                    return infProdEmb;
                }
                
                public void setInfProdEmb(NFe.InfNFe.Det.Prod.InfProdEmb value) {
                    this.infProdEmb = value;
                }

                public NFe.InfNFe.Det.Prod.VeicProd getVeicProd() {
                    return veicProd;
                }

                public void setVeicProd(NFe.InfNFe.Det.Prod.VeicProd value) {
                    this.veicProd = value;
                }
                
                public NFe.InfNFe.Det.Prod.Med getMed() {
                    return med;
                }
                
                public void setMed(NFe.InfNFe.Det.Prod.Med value) {
                    this.med = value;
                }
                
                public List<NFe.InfNFe.Det.Prod.Arma> getArma() {
                    if (arma == null) {
                        arma = new ArrayList<NFe.InfNFe.Det.Prod.Arma>();
                    }
                    return this.arma;
                }
                
                public NFe.InfNFe.Det.Prod.Comb getComb() {
                    return comb;
                }
                
                public void setComb(NFe.InfNFe.Det.Prod.Comb value) {
                    this.comb = value;
                }
                
                public String getNRECOPI() {
                    return nrecopi;
                }
                
                public void setNRECOPI(String value) {
                    this.nrecopi = value;
                }

                
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "tpArma",
                    "nSerie",
                    "nCano",
                    "descr"
                })
                public static class Arma {

                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String tpArma;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String nSerie;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String nCano;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String descr;

                    public String getTpArma() {
                        return tpArma;
                    }
                    
                    public void setTpArma(String value) {
                        this.tpArma = value;
                    }

                    public String getNSerie() {
                        return nSerie;
                    }
                    
                    public void setNSerie(String value) {
                        this.nSerie = value;
                    }
                    
                    public String getNCano() {
                        return nCano;
                    }
                    
                    public void setNCano(String value) {
                        this.nCano = value;
                    }
                    
                    public String getDescr() {
                        return descr;
                    }
                    
                    public void setDescr(String value) {
                        this.descr = value;
                    }
                }

                
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "cProdANP",
                    "descANP",
                    "pglp",
                    "pgNn",
                    "pgNi",
                    "vPart",
                    "codif",
                    "qTemp",
                    "ufCons",
                    "cide",
                    "encerrante",
                    "pBio",
                    "origComb"
                })
                public static class Comb {

                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String cProdANP;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String descANP;
                    @XmlElement(name = "pGLP", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String pglp;
                    @XmlElement(name = "pGNn", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String pgNn;
                    @XmlElement(name = "pGNi", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String pgNi;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String vPart;
                    @XmlElement(name = "CODIF", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String codif;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String qTemp;
                    @XmlElement(name = "UFCons", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    @XmlSchemaType(name = "string")
                    protected Uf ufCons;
                    @XmlElement(name = "CIDE", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Prod.Comb.CIDE cide;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Prod.Comb.Encerrante encerrante;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String pBio;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected List<NFe.InfNFe.Det.Prod.Comb.OrigComb> origComb;

                    public String getCProdANP() {
                        return cProdANP;
                    }

                    public void setCProdANP(String value) {
                        this.cProdANP = value;
                    }
                    
                    public String getDescANP() {
                        return descANP;
                    }

                    public void setDescANP(String value) {
                        this.descANP = value;
                    }
                    
                    public String getPGLP() {
                        return pglp;
                    }

                    public void setPGLP(String value) {
                        this.pglp = value;
                    }
                    
                    public String getPGNn() {
                        return pgNn;
                    }

                    public void setPGNn(String value) {
                        this.pgNn = value;
                    }
                    
                    public String getPGNi() {
                        return pgNi;
                    }
                    
                    public void setPGNi(String value) {
                        this.pgNi = value;
                    }
                    
                    public String getVPart() {
                        return vPart;
                    }
                    
                    public void setVPart(String value) {
                        this.vPart = value;
                    }
                    
                    public String getCODIF() {
                        return codif;
                    }
                    
                    public void setCODIF(String value) {
                        this.codif = value;
                    }
                    
                    public String getQTemp() {
                        return qTemp;
                    }
                    
                    public void setQTemp(String value) {
                        this.qTemp = value;
                    }

                    public Uf getUFCons() {
                        return ufCons;
                    }
                    
                    public void setUFCons(Uf value) {
                        this.ufCons = value;
                    }

                    public NFe.InfNFe.Det.Prod.Comb.CIDE getCIDE() {
                        return cide;
                    }
                    
                    public void setCIDE(NFe.InfNFe.Det.Prod.Comb.CIDE value) {
                        this.cide = value;
                    }
                    
                    public NFe.InfNFe.Det.Prod.Comb.Encerrante getEncerrante() {
                        return encerrante;
                    }
                    
                    public void setEncerrante(NFe.InfNFe.Det.Prod.Comb.Encerrante value) {
                        this.encerrante = value;
                    }

                    public String getPBio() {
                        return pBio;
                    }
                    
                    public void setPBio(String value) {
                        this.pBio = value;
                    }

                    public List<NFe.InfNFe.Det.Prod.Comb.OrigComb> getOrigComb() {
                        if (origComb == null) {
                            origComb = new ArrayList<NFe.InfNFe.Det.Prod.Comb.OrigComb>();
                        }
                        return this.origComb;
                    }

                    
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "qbcProd",
                        "vAliqProd",
                        "vcide"
                    })
                    public static class CIDE {

                        @XmlElement(name = "qBCProd", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String qbcProd;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vAliqProd;
                        @XmlElement(name = "vCIDE", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vcide;

                        public String getQBCProd() {
                            return qbcProd;
                        }
                        
                        public void setQBCProd(String value) {
                            this.qbcProd = value;
                        }
                        
                        public String getVAliqProd() {
                            return vAliqProd;
                        }
                        
                        public void setVAliqProd(String value) {
                            this.vAliqProd = value;
                        }
                        
                        public String getVCIDE() {
                            return vcide;
                        }
                        
                        public void setVCIDE(String value) {
                            this.vcide = value;
                        }

                    }

                    
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "nBico",
                        "nBomba",
                        "nTanque",
                        "vEncIni",
                        "vEncFin"
                    })
                    public static class Encerrante {

                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String nBico;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String nBomba;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String nTanque;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vEncIni;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String vEncFin;
                        
                        public String getNBico() {
                            return nBico;
                        }

                        public void setNBico(String value) {
                            this.nBico = value;
                        }
                        
                        public String getNBomba() {
                            return nBomba;
                        }

                        public void setNBomba(String value) {
                            this.nBomba = value;
                        }
                        
                        public String getNTanque() {
                            return nTanque;
                        }
                        
                        public void setNTanque(String value) {
                            this.nTanque = value;
                        }

                        public String getVEncIni() {
                            return vEncIni;
                        }
                        
                        public void setVEncIni(String value) {
                            this.vEncIni = value;
                        }

                        public String getVEncFin() {
                            return vEncFin;
                        }
                        
                        public void setVEncFin(String value) {
                            this.vEncFin = value;
                        }

                    }


                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "indImport",
                        "cufOrig",
                        "pOrig"
                    })
                    public static class OrigComb {

                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String indImport;
                        @XmlElement(name = "cUFOrig", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String cufOrig;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String pOrig;
                        
                        public String getIndImport() {
                            return indImport;
                        }

                        public void setIndImport(String value) {
                            this.indImport = value;
                        }
                        
                        public String getCUFOrig() {
                            return cufOrig;
                        }

                        public void setCUFOrig(String value) {
                            this.cufOrig = value;
                        }
                        
                        public String getPOrig() {
                            return pOrig;
                        }
                        
                        public void setPOrig(String value) {
                            this.pOrig = value;
                        }

                    }

                }

                
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "ndi",
                    "ddi",
                    "xLocDesemb",
                    "ufDesemb",
                    "dDesemb",
                    "tpViaTransp",
                    "vafrmm",
                    "tpIntermedio",
                    "cnpj",
                    "cpf",
                    "ufTerceiro",
                    "cExportador",
                    "adi"
                })
                public static class DI {

                    @XmlElement(name = "nDI", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String ndi;
                    @XmlElement(name = "dDI", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String ddi;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String xLocDesemb;
                    @XmlElement(name = "UFDesemb", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    @XmlSchemaType(name = "string")
                    protected UfEmi ufDesemb;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String dDesemb;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String tpViaTransp;
                    @XmlElement(name = "vAFRMM", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String vafrmm;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String tpIntermedio;
                    @XmlElement(name = "CNPJ", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String cnpj;
                    @XmlElement(name = "CPF", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String cpf;
                    @XmlElement(name = "UFTerceiro", namespace = "http://www.portalfiscal.inf.br/nfe")
                    @XmlSchemaType(name = "string")
                    protected UfEmi ufTerceiro;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String cExportador;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected List<NFe.InfNFe.Det.Prod.DI.Adi> adi;

                    public String getNDI() {
                        return ndi;
                    }

                    public void setNDI(String value) {
                        this.ndi = value;
                    }

                    public String getDDI() {
                        return ddi;
                    }

                    public void setDDI(String value) {
                        this.ddi = value;
                    }

                    public String getXLocDesemb() {
                        return xLocDesemb;
                    }

                    public void setXLocDesemb(String value) {
                        this.xLocDesemb = value;
                    }

                    public UfEmi getUFDesemb() {
                        return ufDesemb;
                    }
                    
                    public void setUFDesemb(UfEmi value) {
                        this.ufDesemb = value;
                    }

                    public String getDDesemb() {
                        return dDesemb;
                    }
                    
                    public void setDDesemb(String value) {
                        this.dDesemb = value;
                    }
                    
                    public String getTpViaTransp() {
                        return tpViaTransp;
                    }

                    public void setTpViaTransp(String value) {
                        this.tpViaTransp = value;
                    }

                    public String getVAFRMM() {
                        return vafrmm;
                    }
                    
                    public void setVAFRMM(String value) {
                        this.vafrmm = value;
                    }

                    public String getTpIntermedio() {
                        return tpIntermedio;
                    }

                    public void setTpIntermedio(String value) {
                        this.tpIntermedio = value;
                    }

                    public String getCNPJ() {
                        return cnpj;
                    }

                    public void setCNPJ(String value) {
                        this.cnpj = value;
                    }
                    
                    public String getCPF() {
                        return cpf;
                    }

                    public void setCPF(String value) {
                        this.cpf = value;
                    }
                    
                    public UfEmi getUFTerceiro() {
                        return ufTerceiro;
                    }
                    
                    public void setUFTerceiro(UfEmi value) {
                        this.ufTerceiro = value;
                    }

                    public String getCExportador() {
                        return cExportador;
                    }

                    public void setCExportador(String value) {
                        this.cExportador = value;
                    }

                    public List<NFe.InfNFe.Det.Prod.DI.Adi> getAdi() {
                        if (adi == null) {
                            adi = new ArrayList<NFe.InfNFe.Det.Prod.DI.Adi>();
                        }
                        return this.adi;
                    }


                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "nAdicao",
                        "nSeqAdic",
                        "cFabricante",
                        "vDescDI",
                        "nDraw"
                    })
                    public static class Adi {

                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String nAdicao;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String nSeqAdic;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String cFabricante;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String vDescDI;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                        protected String nDraw;
                        
                        public String getNAdicao() {
                            return nAdicao;
                        }

                        public void setNAdicao(String value) {
                            this.nAdicao = value;
                        }
                        
                        public String getNSeqAdic() {
                            return nSeqAdic;
                        }

                        public void setNSeqAdic(String value) {
                            this.nSeqAdic = value;
                        }
                        
                        public String getCFabricante() {
                            return cFabricante;
                        }
                        
                        public void setCFabricante(String value) {
                            this.cFabricante = value;
                        }
                        
                        public String getVDescDI() {
                            return vDescDI;
                        }
                        
                        public void setVDescDI(String value) {
                            this.vDescDI = value;
                        }

                        public String getNDraw() {
                            return nDraw;
                        }

                        public void setNDraw(String value) {
                            this.nDraw = value;
                        }

                    }

                }


                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "nDraw",
                    "exportInd"
                })
                public static class DetExport {

                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String nDraw;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected NFe.InfNFe.Det.Prod.DetExport.ExportInd exportInd;

                    public String getNDraw() {
                        return nDraw;
                    }

                    public void setNDraw(String value) {
                        this.nDraw = value;
                    }

                    public NFe.InfNFe.Det.Prod.DetExport.ExportInd getExportInd() {
                        return exportInd;
                    }
                    
                    public void setExportInd(NFe.InfNFe.Det.Prod.DetExport.ExportInd value) {
                        this.exportInd = value;
                    }

                    
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "nre",
                        "chNFe",
                        "qExport"
                    })
                    public static class ExportInd {

                        @XmlElement(name = "nRE", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String nre;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String chNFe;
                        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                        protected String qExport;

                        public String getNRE() {
                            return nre;
                        }

                        public void setNRE(String value) {
                            this.nre = value;
                        }

                        public String getChNFe() {
                            return chNFe;
                        }

                        public void setChNFe(String value) {
                            this.chNFe = value;
                        }

                        public String getQExport() {
                            return qExport;
                        }
                        
                        public void setQExport(String value) {
                            this.qExport = value;
                        }

                    }

                }


                
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "cCredPresumido",
                    "pCredPresumido",
                    "vCredPresumido"
                })
                public static class GCred {

                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String cCredPresumido;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String pCredPresumido;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String vCredPresumido;
                    
                    public String getCCredPresumido() {
                        return cCredPresumido;
                    }
                    
                    public void setCCredPresumido(String value) {
                        this.cCredPresumido = value;
                    }

                    public String getPCredPresumido() {
                        return pCredPresumido;
                    }
                    
                    public void setPCredPresumido(String value) {
                        this.pCredPresumido = value;
                    }
                    
                    public String getVCredPresumido() {
                        return vCredPresumido;
                    }

                    
                    public void setVCredPresumido(String value) {
                        this.vCredPresumido = value;
                    }

                }

                
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "xEmb",
                    "qVolEmb",
                    "uEmb"
                })
                public static class InfProdEmb {

                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String xEmb;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String qVolEmb;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String uEmb;

                    public String getXEmb() {
                        return xEmb;
                    }

                    public void setXEmb(String value) {
                        this.xEmb = value;
                    }

                    public String getQVolEmb() {
                        return qVolEmb;
                    }
                    
                    public void setQVolEmb(String value) {
                        this.qVolEmb = value;
                    }

                    public String getUEmb() {
                        return uEmb;
                    }
                    
                    public void setUEmb(String value) {
                        this.uEmb = value;
                    }

                }

                
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "cProdFisco",
                    "cOperNFF"
                })
                public static class InfProdNFF {

                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String cProdFisco;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String cOperNFF;

                    public String getCProdFisco() {
                        return cProdFisco;
                    }

                    public void setCProdFisco(String value) {
                        this.cProdFisco = value;
                    }

                    public String getCOperNFF() {
                        return cOperNFF;
                    }

                    public void setCOperNFF(String value) {
                        this.cOperNFF = value;
                    }

                }

                
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "cProdANVISA",
                    "xMotivoIsencao",
                    "vpmc"
                })
                public static class Med {

                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String cProdANVISA;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String xMotivoIsencao;
                    @XmlElement(name = "vPMC", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String vpmc;

                    public String getCProdANVISA() {
                        return cProdANVISA;
                    }

                    public void setCProdANVISA(String value) {
                        this.cProdANVISA = value;
                    }

                    public String getXMotivoIsencao() {
                        return xMotivoIsencao;
                    }

                    public void setXMotivoIsencao(String value) {
                        this.xMotivoIsencao = value;
                    }

                    public String getVPMC() {
                        return vpmc;
                    }

                    public void setVPMC(String value) {
                        this.vpmc = value;
                    }

                }

                
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "nLote",
                    "qLote",
                    "dFab",
                    "dVal",
                    "cAgreg"
                })
                public static class Rastro {

                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String nLote;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String qLote;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String dFab;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String dVal;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String cAgreg;

                    public String getNLote() {
                        return nLote;
                    }

                    public void setNLote(String value) {
                        this.nLote = value;
                    }

                    public String getQLote() {
                        return qLote;
                    }

                    public void setQLote(String value) {
                        this.qLote = value;
                    }

                    public String getDFab() {
                        return dFab;
                    }

                    public void setDFab(String value) {
                        this.dFab = value;
                    }

                    public String getDVal() {
                        return dVal;
                    }

                    public void setDVal(String value) {
                        this.dVal = value;
                    }

                    public String getCAgreg() {
                        return cAgreg;
                    }

                    public void setCAgreg(String value) {
                        this.cAgreg = value;
                    }

                }


                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "tpOp",
                    "chassi",
                    "cCor",
                    "xCor",
                    "pot",
                    "cilin",
                    "pesoL",
                    "pesoB",
                    "nSerie",
                    "tpComb",
                    "nMotor",
                    "cmt",
                    "dist",
                    "anoMod",
                    "anoFab",
                    "tpPint",
                    "tpVeic",
                    "espVeic",
                    "vin",
                    "condVeic",
                    "cMod",
                    "cCorDENATRAN",
                    "lota",
                    "tpRest"
                })
                public static class VeicProd {

                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String tpOp;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String chassi;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String cCor;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String xCor;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String pot;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String cilin;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String pesoL;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String pesoB;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String nSerie;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String tpComb;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String nMotor;
                    @XmlElement(name = "CMT", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String cmt;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String dist;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String anoMod;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String anoFab;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String tpPint;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String tpVeic;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String espVeic;
                    @XmlElement(name = "VIN", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String vin;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String condVeic;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String cMod;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String cCorDENATRAN;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String lota;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String tpRest;

                    public String getTpOp() {
                        return tpOp;
                    }

                    public void setTpOp(String value) {
                        this.tpOp = value;
                    }

                    public String getChassi() {
                        return chassi;
                    }

                    public void setChassi(String value) {
                        this.chassi = value;
                    }

                    public String getCCor() {
                        return cCor;
                    }

                    public void setCCor(String value) {
                        this.cCor = value;
                    }

                    public String getXCor() {
                        return xCor;
                    }

                    public void setXCor(String value) {
                        this.xCor = value;
                    }

                    public String getPot() {
                        return pot;
                    }

                    public void setPot(String value) {
                        this.pot = value;
                    }
                    
                    public String getCilin() {
                        return cilin;
                    }

                    public void setCilin(String value) {
                        this.cilin = value;
                    }

                    public String getPesoL() {
                        return pesoL;
                    }

                    public void setPesoL(String value) {
                        this.pesoL = value;
                    }
                    
                    public String getPesoB() {
                        return pesoB;
                    }
                    
                    public void setPesoB(String value) {
                        this.pesoB = value;
                    }
                    
                    public String getNSerie() {
                        return nSerie;
                    }

                    public void setNSerie(String value) {
                        this.nSerie = value;
                    }
                    
                    public String getTpComb() {
                        return tpComb;
                    }

                    public void setTpComb(String value) {
                        this.tpComb = value;
                    }
                    
                    public String getNMotor() {
                        return nMotor;
                    }
                    
                    public void setNMotor(String value) {
                        this.nMotor = value;
                    }
                    
                    public String getCMT() {
                        return cmt;
                    }

                    public void setCMT(String value) {
                        this.cmt = value;
                    }

                    public String getDist() {
                        return dist;
                    }
                    
                    public void setDist(String value) {
                        this.dist = value;
                    }
                    
                    public String getAnoMod() {
                        return anoMod;
                    }
                    
                    public void setAnoMod(String value) {
                        this.anoMod = value;
                    }

                    public String getAnoFab() {
                        return anoFab;
                    }

                    public void setAnoFab(String value) {
                        this.anoFab = value;
                    }

                    public String getTpPint() {
                        return tpPint;
                    }

                    public void setTpPint(String value) {
                        this.tpPint = value;
                    }

                    public String getTpVeic() {
                        return tpVeic;
                    }

                    public void setTpVeic(String value) {
                        this.tpVeic = value;
                    }

                    public String getEspVeic() {
                        return espVeic;
                    }

                    public void setEspVeic(String value) {
                        this.espVeic = value;
                    }
                    
                    public String getVIN() {
                        return vin;
                    }

                    public void setVIN(String value) {
                        this.vin = value;
                    }
                    
                    public String getCondVeic() {
                        return condVeic;
                    }

                    public void setCondVeic(String value) {
                        this.condVeic = value;
                    }
                    
                    public String getCMod() {
                        return cMod;
                    }
                    
                    public void setCMod(String value) {
                        this.cMod = value;
                    }
                    
                    public String getCCorDENATRAN() {
                        return cCorDENATRAN;
                    }
                    
                    public void setCCorDENATRAN(String value) {
                        this.cCorDENATRAN = value;
                    }
                    
                    public String getLota() {
                        return lota;
                    }
                    
                    public void setLota(String value) {
                        this.lota = value;
                    }
                    
                    public String getTpRest() {
                        return tpRest;
                    }

                    public void setTpRest(String value) {
                        this.tpRest = value;
                    }

                }

            }

        }


        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "cnpj",
            "cpf",
            "xNome",
            "xFant",
            "enderEmit",
            "ie",
            "iest",
            "im",
            "cnae",
            "crt"
        })
        public static class Emit {

            @XmlElement(name = "CNPJ", namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String cnpj;
            @XmlElement(name = "CPF", namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String cpf;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String xNome;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String xFant;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected EnderEmi enderEmit;
            @XmlElement(name = "IE", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String ie;
            @XmlElement(name = "IEST", namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String iest;
            @XmlElement(name = "IM", namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String im;
            @XmlElement(name = "CNAE", namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String cnae;
            @XmlElement(name = "CRT", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String crt;

            public String getCNPJ() {
                return cnpj;
            }

            public void setCNPJ(String value) {
                this.cnpj = value;
            }

            public String getCPF() {
                return cpf;
            }

            public void setCPF(String value) {
                this.cpf = value;
            }

            public String getXNome() {
                return xNome;
            }

            public void setXNome(String value) {
                this.xNome = value;
            }

            public String getXFant() {
                return xFant;
            }

            public void setXFant(String value) {
                this.xFant = value;
            }

            public EnderEmi getEnderEmit() {
                return enderEmit;
            }

            public void setEnderEmit(EnderEmi value) {
                this.enderEmit = value;
            }

            public String getIE() {
                return ie;
            }

            public void setIE(String value) {
                this.ie = value;
            }

            public String getIEST() {
                return iest;
            }

            public void setIEST(String value) {
                this.iest = value;
            }

            public String getIM() {
                return im;
            }

            public void setIM(String value) {
                this.im = value;
            }

            public String getCNAE() {
                return cnae;
            }

            public void setCNAE(String value) {
                this.cnae = value;
            }

            public String getCRT() {
                return crt;
            }

            public void setCRT(String value) {
                this.crt = value;
            }

        }


        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "ufSaidaPais",
            "xLocExporta",
            "xLocDespacho"
        })
        public static class Exporta {

            @XmlElement(name = "UFSaidaPais", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            @XmlSchemaType(name = "string")
            protected UfEmi ufSaidaPais;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String xLocExporta;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String xLocDespacho;

            public UfEmi getUFSaidaPais() {
                return ufSaidaPais;
            }

            public void setUFSaidaPais(UfEmi value) {
                this.ufSaidaPais = value;
            }
            
            public String getXLocExporta() {
                return xLocExporta;
            }
            
            public void setXLocExporta(String value) {
                this.xLocExporta = value;
            }

            public String getXLocDespacho() {
                return xLocDespacho;
            }

            public void setXLocDespacho(String value) {
                this.xLocDespacho = value;
            }

        }


        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "cuf",
            "cnf",
            "natOp",
            "mod",
            "serie",
            "nnf",
            "dhEmi",
            "dhSaiEnt",
            "tpNF",
            "idDest",
            "cMunFG",
            "tpImp",
            "tpEmis",
            "cdv",
            "tpAmb",
            "finNFe",
            "indFinal",
            "indPres",
            "indIntermed",
            "procEmi",
            "verProc",
            "dhCont",
            "xJust",
            "nFref"
        })
        public static class Ide {

            @XmlElement(name = "cUF", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String cuf;
            @XmlElement(name = "cNF", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String cnf;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String natOp;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String mod;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String serie;
            @XmlElement(name = "nNF", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String nnf;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String dhEmi;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String dhSaiEnt;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String tpNF;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String idDest;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String cMunFG;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String tpImp;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String tpEmis;
            @XmlElement(name = "cDV", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String cdv;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String tpAmb;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String finNFe;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String indFinal;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String indPres;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String indIntermed;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String procEmi;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String verProc;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String dhCont;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String xJust;
            @XmlElement(name = "NFref", namespace = "http://www.portalfiscal.inf.br/nfe")
            protected List<NFe.InfNFe.Ide.NFref> nFref;
            
            public String getCUF() {
                return cuf;
            }
            
            public void setCUF(String value) {
                this.cuf = value;
            }
            
            public String getCNF() {
                return cnf;
            }
            
            public void setCNF(String value) {
                this.cnf = value;
            }
            
            public String getNatOp() {
                return natOp;
            }
            
            public void setNatOp(String value) {
                this.natOp = value;
            }

            public String getMod() {
                return mod;
            }
            
            public void setMod(String value) {
                this.mod = value;
            }

            public String getSerie() {
                return serie;
            }
            
            public void setSerie(String value) {
                this.serie = value;
            }

            public String getNNF() {
                return nnf;
            }

            public void setNNF(String value) {
                this.nnf = value;
            }
            
            public String getDhEmi() {
                return dhEmi;
            }
            
            public void setDhEmi(String value) {
                this.dhEmi = value;
            }
            
            public String getDhSaiEnt() {
                return dhSaiEnt;
            }
            
            public void setDhSaiEnt(String value) {
                this.dhSaiEnt = value;
            }

            public String getTpNF() {
                return tpNF;
            }
            
            public void setTpNF(String value) {
                this.tpNF = value;
            }

            public String getIdDest() {
                return idDest;
            }
            
            public void setIdDest(String value) {
                this.idDest = value;
            }
            
            public String getCMunFG() {
                return cMunFG;
            }

            public void setCMunFG(String value) {
                this.cMunFG = value;
            }

            public String getTpImp() {
                return tpImp;
            }

            public void setTpImp(String value) {
                this.tpImp = value;
            }

            public String getTpEmis() {
                return tpEmis;
            }

            public void setTpEmis(String value) {
                this.tpEmis = value;
            }

            public String getCDV() {
                return cdv;
            }

            public void setCDV(String value) {
                this.cdv = value;
            }

            public String getTpAmb() {
                return tpAmb;
            }
            
            public void setTpAmb(String value) {
                this.tpAmb = value;
            }

            public String getFinNFe() {
                return finNFe;
            }

            public void setFinNFe(String value) {
                this.finNFe = value;
            }
            
            public String getIndFinal() {
                return indFinal;
            }

            public void setIndFinal(String value) {
                this.indFinal = value;
            }

            public String getIndPres() {
                return indPres;
            }

            public void setIndPres(String value) {
                this.indPres = value;
            }
            
            public String getIndIntermed() {
                return indIntermed;
            }

            public void setIndIntermed(String value) {
                this.indIntermed = value;
            }

            public String getProcEmi() {
                return procEmi;
            }
            
            public void setProcEmi(String value) {
                this.procEmi = value;
            }

            public String getVerProc() {
                return verProc;
            }

            public void setVerProc(String value) {
                this.verProc = value;
            }

            public String getDhCont() {
                return dhCont;
            }

            public void setDhCont(String value) {
                this.dhCont = value;
            }

            public String getXJust() {
                return xJust;
            }

            public void setXJust(String value) {
                this.xJust = value;
            }

            public List<NFe.InfNFe.Ide.NFref> getNFref() {
                if (nFref == null) {
                    nFref = new ArrayList<NFe.InfNFe.Ide.NFref>();
                }
                return this.nFref;
            }

            
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "refNFe",
                "refNFeSig",
                "refNF",
                "refNFP",
                "refCTe",
                "refECF"
            })
            public static class NFref {

                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String refNFe;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String refNFeSig;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected NFe.InfNFe.Ide.NFref.RefNF refNF;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected NFe.InfNFe.Ide.NFref.RefNFP refNFP;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String refCTe;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected NFe.InfNFe.Ide.NFref.RefECF refECF;

                public String getRefNFe() {
                    return refNFe;
                }

                public void setRefNFe(String value) {
                    this.refNFe = value;
                }

                public String getRefNFeSig() {
                    return refNFeSig;
                }
                
                public void setRefNFeSig(String value) {
                    this.refNFeSig = value;
                }
                
                public NFe.InfNFe.Ide.NFref.RefNF getRefNF() {
                    return refNF;
                }
                
                public void setRefNF(NFe.InfNFe.Ide.NFref.RefNF value) {
                    this.refNF = value;
                }
                
                public NFe.InfNFe.Ide.NFref.RefNFP getRefNFP() {
                    return refNFP;
                }

                public void setRefNFP(NFe.InfNFe.Ide.NFref.RefNFP value) {
                    this.refNFP = value;
                }

                public String getRefCTe() {
                    return refCTe;
                }

                public void setRefCTe(String value) {
                    this.refCTe = value;
                }

                public NFe.InfNFe.Ide.NFref.RefECF getRefECF() {
                    return refECF;
                }

                public void setRefECF(NFe.InfNFe.Ide.NFref.RefECF value) {
                    this.refECF = value;
                }

                
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "mod",
                    "necf",
                    "ncoo"
                })
                public static class RefECF {

                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String mod;
                    @XmlElement(name = "nECF", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String necf;
                    @XmlElement(name = "nCOO", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String ncoo;

                    public String getMod() {
                        return mod;
                    }

                    public void setMod(String value) {
                        this.mod = value;
                    }

                    public String getNECF() {
                        return necf;
                    }
                    
                    public void setNECF(String value) {
                        this.necf = value;
                    }

                    public String getNCOO() {
                        return ncoo;
                    }
                    
                    public void setNCOO(String value) {
                        this.ncoo = value;
                    }

                }

                
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "cuf",
                    "aamm",
                    "cnpj",
                    "mod",
                    "serie",
                    "nnf"
                })
                public static class RefNF {

                    @XmlElement(name = "cUF", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String cuf;
                    @XmlElement(name = "AAMM", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String aamm;
                    @XmlElement(name = "CNPJ", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String cnpj;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String mod;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String serie;
                    @XmlElement(name = "nNF", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String nnf;
                    
                    public String getCUF() {
                        return cuf;
                    }

                    public void setCUF(String value) {
                        this.cuf = value;
                    }
                    
                    public String getAAMM() {
                        return aamm;
                    }

                    
                    public void setAAMM(String value) {
                        this.aamm = value;
                    }

                    public String getCNPJ() {
                        return cnpj;
                    }

                    public void setCNPJ(String value) {
                        this.cnpj = value;
                    }
                    
                    public String getMod() {
                        return mod;
                    }

                    public void setMod(String value) {
                        this.mod = value;
                    }

                    public String getSerie() {
                        return serie;
                    }

                    public void setSerie(String value) {
                        this.serie = value;
                    }

                    public String getNNF() {
                        return nnf;
                    }
                    
                    public void setNNF(String value) {
                        this.nnf = value;
                    }

                }

                
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "cuf",
                    "aamm",
                    "cnpj",
                    "cpf",
                    "ie",
                    "mod",
                    "serie",
                    "nnf"
                })
                public static class RefNFP {

                    @XmlElement(name = "cUF", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String cuf;
                    @XmlElement(name = "AAMM", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String aamm;
                    @XmlElement(name = "CNPJ", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String cnpj;
                    @XmlElement(name = "CPF", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String cpf;
                    @XmlElement(name = "IE", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String ie;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String mod;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String serie;
                    @XmlElement(name = "nNF", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String nnf;

                    public String getCUF() {
                        return cuf;
                    }

                    public void setCUF(String value) {
                        this.cuf = value;
                    }

                    public String getAAMM() {
                        return aamm;
                    }

                    public void setAAMM(String value) {
                        this.aamm = value;
                    }

                    public String getCNPJ() {
                        return cnpj;
                    }

                    public void setCNPJ(String value) {
                        this.cnpj = value;
                    }

                    public String getCPF() {
                        return cpf;
                    }

                    public void setCPF(String value) {
                        this.cpf = value;
                    }

                    public String getIE() {
                        return ie;
                    }

                    public void setIE(String value) {
                        this.ie = value;
                    }

                    public String getMod() {
                        return mod;
                    }

                    public void setMod(String value) {
                        this.mod = value;
                    }

                    public String getSerie() {
                        return serie;
                    }

                    public void setSerie(String value) {
                        this.serie = value;
                    }

                    public String getNNF() {
                        return nnf;
                    }

                    public void setNNF(String value) {
                        this.nnf = value;
                    }

                }

            }

        }


        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "infAdFisco",
            "infCpl",
            "obsCont",
            "obsFisco",
            "procRef"
        })
        public static class InfAdic {

            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String infAdFisco;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String infCpl;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected List<NFe.InfNFe.InfAdic.ObsCont> obsCont;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected List<NFe.InfNFe.InfAdic.ObsFisco> obsFisco;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected List<NFe.InfNFe.InfAdic.ProcRef> procRef;
            
            public String getInfAdFisco() {
                return infAdFisco;
            }
            
            public void setInfAdFisco(String value) {
                this.infAdFisco = value;
            }
            
            public String getInfCpl() {
                return infCpl;
            }
            
            public void setInfCpl(String value) {
                this.infCpl = value;
            }
            
            public List<NFe.InfNFe.InfAdic.ObsCont> getObsCont() {
                if (obsCont == null) {
                    obsCont = new ArrayList<NFe.InfNFe.InfAdic.ObsCont>();
                }
                return this.obsCont;
            }

            public void setProcRef(List<ProcRef> procRef) {
                this.procRef = procRef;
            }

            public void setObsFisco(List<ObsFisco> obsFisco) {
                this.obsFisco = obsFisco;
            }

            public void setObsCont(List<ObsCont> obsCont) {
                this.obsCont = obsCont;
            }

            public List<NFe.InfNFe.InfAdic.ObsFisco> getObsFisco() {
                if (obsFisco == null) {
                    obsFisco = new ArrayList<NFe.InfNFe.InfAdic.ObsFisco>();
                }
                return this.obsFisco;
            }

            public List<NFe.InfNFe.InfAdic.ProcRef> getProcRef() {
                if (procRef == null) {
                    procRef = new ArrayList<NFe.InfNFe.InfAdic.ProcRef>();
                }
                return this.procRef;
            }

            
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "xTexto"
            })
            public static class ObsCont {

                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String xTexto;
                @XmlAttribute(name = "xCampo", required = true)
                protected String xCampo;

                public String getXTexto() {
                    return xTexto;
                }

                public void setXTexto(String value) {
                    this.xTexto = value;
                }

                public String getXCampo() {
                    return xCampo;
                }

                public void setXCampo(String value) {
                    this.xCampo = value;
                }

            }

            
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "xTexto"
            })
            public static class ObsFisco {

                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String xTexto;
                @XmlAttribute(name = "xCampo", required = true)
                protected String xCampo;

                public String getXTexto() {
                    return xTexto;
                }

                public void setXTexto(String value) {
                    this.xTexto = value;
                }

                public String getXCampo() {
                    return xCampo;
                }

                public void setXCampo(String value) {
                    this.xCampo = value;
                }

            }


            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "nProc",
                "indProc",
                "tpAto"
            })
            public static class ProcRef {

                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String nProc;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String indProc;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String tpAto;

                public String getNProc() {
                    return nProc;
                }

                public void setNProc(String value) {
                    this.nProc = value;
                }

                public String getIndProc() {
                    return indProc;
                }

                public void setIndProc(String value) {
                    this.indProc = value;
                }

                public String getTpAto() {
                    return tpAto;
                }

                public void setTpAto(String value) {
                    this.tpAto = value;
                }

            }

        }


        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "cnpj",
            "idCadIntTran"
        })
        public static class InfIntermed {

            @XmlElement(name = "CNPJ", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String cnpj;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String idCadIntTran;

            public String getCNPJ() {
                return cnpj;
            }

            public void setCNPJ(String value) {
                this.cnpj = value;
            }

            public String getIdCadIntTran() {
                return idCadIntTran;
            }

            public void setIdCadIntTran(String value) {
                this.idCadIntTran = value;
            }

        }

        
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "xSolic"
        })
        public static class InfSolicNFF {

            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String xSolic;
            
            public String getXSolic() {
                return xSolic;
            }
            
            public void setXSolic(String value) {
                this.xSolic = value;
            }

        }


        
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "detPag",
            "vTroco"
        })
        public static class Pag {

            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected List<NFe.InfNFe.Pag.DetPag> detPag;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String vTroco;

            public List<NFe.InfNFe.Pag.DetPag> getDetPag() {
                if (detPag == null) {
                    detPag = new ArrayList<NFe.InfNFe.Pag.DetPag>();
                }
                return this.detPag;
            }

            public void setDetPag(List<NFe.InfNFe.Pag.DetPag> detPag) {
				this.detPag = detPag;
			}

			public String getVTroco() {
                return vTroco;
            }

            public void setVTroco(String value) {
                this.vTroco = value;
            }


            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "indPag",
                "tPag",
                "xPag",
                "vPag",
                "dPag",
                "cnpjPag",
                "ufPag",
                "card"
            })
            public static class DetPag {

                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String indPag;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String tPag;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String xPag;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vPag;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String dPag;
                @XmlElement(name = "CNPJPag", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String cnpjPag;
                @XmlElement(name = "UFPag", namespace = "http://www.portalfiscal.inf.br/nfe")
                @XmlSchemaType(name = "string")
                protected UfEmi ufPag;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected NFe.InfNFe.Pag.DetPag.Card card;

                public String getIndPag() {
                    return indPag;
                }

                public void setIndPag(String value) {
                    this.indPag = value;
                }

                public String getTPag() {
                    return tPag;
                }

                public void setTPag(String value) {
                    this.tPag = value;
                }

                public String getXPag() {
                    return xPag;
                }

                public void setXPag(String value) {
                    this.xPag = value;
                }

                public String getVPag() {
                    return vPag;
                }

                public void setVPag(String value) {
                    this.vPag = value;
                }

                public String getDPag() {
                    return dPag;
                }

                public void setDPag(String value) {
                    this.dPag = value;
                }

                public String getCNPJPag() {
                    return cnpjPag;
                }
                
                public void setCNPJPag(String value) {
                    this.cnpjPag = value;
                }

                public UfEmi getUFPag() {
                    return ufPag;
                }

                public void setUFPag(UfEmi value) {
                    this.ufPag = value;
                }

                public NFe.InfNFe.Pag.DetPag.Card getCard() {
                    return card;
                }

                public void setCard(NFe.InfNFe.Pag.DetPag.Card value) {
                    this.card = value;
                }

                
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "tpIntegra",
                    "cnpj",
                    "tBand",
                    "cAut",
                    "cnpjReceb",
                    "idTermPag"
                })
                public static class Card {

                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String tpIntegra;
                    @XmlElement(name = "CNPJ", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String cnpj;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String tBand;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String cAut;
                    @XmlElement(name = "CNPJReceb", namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String cnpjReceb;
                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                    protected String idTermPag;
                    
                    public String getTpIntegra() {
                        return tpIntegra;
                    }

                    public void setTpIntegra(String value) {
                        this.tpIntegra = value;
                    }

                    public String getCNPJ() {
                        return cnpj;
                    }

                    public void setCNPJ(String value) {
                        this.cnpj = value;
                    }

                    public String getTBand() {
                        return tBand;
                    }

                    public void setTBand(String value) {
                        this.tBand = value;
                    }

                    public String getCAut() {
                        return cAut;
                    }

                    public void setCAut(String value) {
                        this.cAut = value;
                    }

                    public String getCNPJReceb() {
                        return cnpjReceb;
                    }

                    public void setCNPJReceb(String value) {
                        this.cnpjReceb = value;
                    }

                    public String getIdTermPag() {
                        return idTermPag;
                    }

                    public void setIdTermPag(String value) {
                        this.idTermPag = value;
                    }

                }

            }

        }

        
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "icmsTot",
            "issqNtot",
            "retTrib"
        })
        public static class Total {

            @XmlElement(name = "ICMSTot", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected NFe.InfNFe.Total.ICMSTot icmsTot;
            @XmlElement(name = "ISSQNtot", namespace = "http://www.portalfiscal.inf.br/nfe")
            protected NFe.InfNFe.Total.ISSQNtot issqNtot;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected NFe.InfNFe.Total.RetTrib retTrib;

            public NFe.InfNFe.Total.ICMSTot getICMSTot() {
                return icmsTot;
            }

            public void setICMSTot(NFe.InfNFe.Total.ICMSTot value) {
                this.icmsTot = value;
            }

            public NFe.InfNFe.Total.ISSQNtot getISSQNtot() {
                return issqNtot;
            }

            public void setISSQNtot(NFe.InfNFe.Total.ISSQNtot value) {
                this.issqNtot = value;
            }

            public NFe.InfNFe.Total.RetTrib getRetTrib() {
                return retTrib;
            }

            public void setRetTrib(NFe.InfNFe.Total.RetTrib value) {
                this.retTrib = value;
            }

            
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "vbc",
                "vicms",
                "vicmsDeson",
                "vfcpufDest",
                "vicmsufDest",
                "vicmsufRemet",
                "vfcp",
                "vbcst",
                "vst",
                "vfcpst",
                "vfcpstRet",
                "qbcMono",
                "vicmsMono",
                "qbcMonoReten",
                "vicmsMonoReten",
                "qbcMonoRet",
                "vicmsMonoRet",
                "vProd",
                "vFrete",
                "vSeg",
                "vDesc",
                "vii",
                "vipi",
                "vipiDevol",
                "vpis",
                "vcofins",
                "vOutro",
                "vnf",
                "vTotTrib"
            })
            public static class ICMSTot {

                @XmlElement(name = "vBC", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vbc;
                @XmlElement(name = "vICMS", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vicms;
                @XmlElement(name = "vICMSDeson", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vicmsDeson;
                @XmlElement(name = "vFCPUFDest", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vfcpufDest;
                @XmlElement(name = "vICMSUFDest", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vicmsufDest;
                @XmlElement(name = "vICMSUFRemet", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vicmsufRemet;
                @XmlElement(name = "vFCP", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vfcp;
                @XmlElement(name = "vBCST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vbcst;
                @XmlElement(name = "vST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vst;
                @XmlElement(name = "vFCPST", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vfcpst;
                @XmlElement(name = "vFCPSTRet", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vfcpstRet;
                @XmlElement(name = "qBCMono", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String qbcMono;
                @XmlElement(name = "vICMSMono", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vicmsMono;
                @XmlElement(name = "qBCMonoReten", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String qbcMonoReten;
                @XmlElement(name = "vICMSMonoReten", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vicmsMonoReten;
                @XmlElement(name = "qBCMonoRet", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String qbcMonoRet;
                @XmlElement(name = "vICMSMonoRet", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vicmsMonoRet;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vProd;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vFrete;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vSeg;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vDesc;
                @XmlElement(name = "vII", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vii;
                @XmlElement(name = "vIPI", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vipi;
                @XmlElement(name = "vIPIDevol", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vipiDevol;
                @XmlElement(name = "vPIS", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vpis;
                @XmlElement(name = "vCOFINS", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vcofins;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vOutro;
                @XmlElement(name = "vNF", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vnf;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vTotTrib;

                public String getVBC() {
                    return vbc;
                }
                
                public void setVBC(String value) {
                    this.vbc = value;
                }

                public String getVICMS() {
                    return vicms;
                }

                public void setVICMS(String value) {
                    this.vicms = value;
                }

                public String getVICMSDeson() {
                    return vicmsDeson;
                }

                public void setVICMSDeson(String value) {
                    this.vicmsDeson = value;
                }
                
                public String getVFCPUFDest() {
                    return vfcpufDest;
                }

                public void setVFCPUFDest(String value) {
                    this.vfcpufDest = value;
                }

                public String getVICMSUFDest() {
                    return vicmsufDest;
                }

                public void setVICMSUFDest(String value) {
                    this.vicmsufDest = value;
                }

                public String getVICMSUFRemet() {
                    return vicmsufRemet;
                }
                
                public void setVICMSUFRemet(String value) {
                    this.vicmsufRemet = value;
                }

                public String getVFCP() {
                    return vfcp;
                }
                
                public void setVFCP(String value) {
                    this.vfcp = value;
                }
                
                public String getVBCST() {
                    return vbcst;
                }
                
                public void setVBCST(String value) {
                    this.vbcst = value;
                }
                
                public String getVST() {
                    return vst;
                }
                
                public void setVST(String value) {
                    this.vst = value;
                }
                
                public String getVFCPST() {
                    return vfcpst;
                }
                
                public void setVFCPST(String value) {
                    this.vfcpst = value;
                }

                public String getVFCPSTRet() {
                    return vfcpstRet;
                }

                public void setVFCPSTRet(String value) {
                    this.vfcpstRet = value;
                }
                
                public String getQBCMono() {
                    return qbcMono;
                }
                
                public void setQBCMono(String value) {
                    this.qbcMono = value;
                }

                public String getVICMSMono() {
                    return vicmsMono;
                }
                
                public void setVICMSMono(String value) {
                    this.vicmsMono = value;
                }

                public String getQBCMonoReten() {
                    return qbcMonoReten;
                }

                public void setQBCMonoReten(String value) {
                    this.qbcMonoReten = value;
                }

                public String getVICMSMonoReten() {
                    return vicmsMonoReten;
                }
                
                public void setVICMSMonoReten(String value) {
                    this.vicmsMonoReten = value;
                }

                public String getQBCMonoRet() {
                    return qbcMonoRet;
                }

                public void setQBCMonoRet(String value) {
                    this.qbcMonoRet = value;
                }

                public String getVICMSMonoRet() {
                    return vicmsMonoRet;
                }

                public void setVICMSMonoRet(String value) {
                    this.vicmsMonoRet = value;
                }

                public String getVProd() {
                    return vProd;
                }
                
                public void setVProd(String value) {
                    this.vProd = value;
                }

                public String getVFrete() {
                    return vFrete;
                }

                public void setVFrete(String value) {
                    this.vFrete = value;
                }

                public String getVSeg() {
                    return vSeg;
                }

                public void setVSeg(String value) {
                    this.vSeg = value;
                }
                
                public String getVDesc() {
                    return vDesc;
                }

                public void setVDesc(String value) {
                    this.vDesc = value;
                }
                
                public String getVII() {
                    return vii;
                }
                
                public void setVII(String value) {
                    this.vii = value;
                }
                
                public String getVIPI() {
                    return vipi;
                }

                public void setVIPI(String value) {
                    this.vipi = value;
                }

                public String getVIPIDevol() {
                    return vipiDevol;
                }
                
                public void setVIPIDevol(String value) {
                    this.vipiDevol = value;
                }
                
                public String getVPIS() {
                    return vpis;
                }

                public void setVPIS(String value) {
                    this.vpis = value;
                }

                public String getVCOFINS() {
                    return vcofins;
                }

                public void setVCOFINS(String value) {
                    this.vcofins = value;
                }

                public String getVOutro() {
                    return vOutro;
                }
                
                public void setVOutro(String value) {
                    this.vOutro = value;
                }
                
                public String getVNF() {
                    return vnf;
                }

                public void setVNF(String value) {
                    this.vnf = value;
                }

                public String getVTotTrib() {
                    return vTotTrib;
                }
                
                public void setVTotTrib(String value) {
                    this.vTotTrib = value;
                }

            }

            
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "vServ",
                "vbc",
                "viss",
                "vpis",
                "vcofins",
                "dCompet",
                "vDeducao",
                "vOutro",
                "vDescIncond",
                "vDescCond",
                "vissRet",
                "cRegTrib"
            })
            public static class ISSQNtot {

                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vServ;
                @XmlElement(name = "vBC", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vbc;
                @XmlElement(name = "vISS", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String viss;
                @XmlElement(name = "vPIS", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vpis;
                @XmlElement(name = "vCOFINS", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vcofins;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String dCompet;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vDeducao;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vOutro;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vDescIncond;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vDescCond;
                @XmlElement(name = "vISSRet", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vissRet;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String cRegTrib;

                public String getVServ() {
                    return vServ;
                }

                public void setVServ(String value) {
                    this.vServ = value;
                }

                public String getVBC() {
                    return vbc;
                }
                
                public void setVBC(String value) {
                    this.vbc = value;
                }

                public String getVISS() {
                    return viss;
                }
                
                public void setVISS(String value) {
                    this.viss = value;
                }
                
                public String getVPIS() {
                    return vpis;
                }
                
                public void setVPIS(String value) {
                    this.vpis = value;
                }

                public String getVCOFINS() {
                    return vcofins;
                }

                public void setVCOFINS(String value) {
                    this.vcofins = value;
                }

                public String getDCompet() {
                    return dCompet;
                }

                public void setDCompet(String value) {
                    this.dCompet = value;
                }

                public String getVDeducao() {
                    return vDeducao;
                }

                public void setVDeducao(String value) {
                    this.vDeducao = value;
                }

                public String getVOutro() {
                    return vOutro;
                }

                public void setVOutro(String value) {
                    this.vOutro = value;
                }

                public String getVDescIncond() {
                    return vDescIncond;
                }

                public void setVDescIncond(String value) {
                    this.vDescIncond = value;
                }

                public String getVDescCond() {
                    return vDescCond;
                }
                
                public void setVDescCond(String value) {
                    this.vDescCond = value;
                }
                
                public String getVISSRet() {
                    return vissRet;
                }

                public void setVISSRet(String value) {
                    this.vissRet = value;
                }
                
                public String getCRegTrib() {
                    return cRegTrib;
                }

                public void setCRegTrib(String value) {
                    this.cRegTrib = value;
                }

            }

            
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "vRetPIS",
                "vRetCOFINS",
                "vRetCSLL",
                "vbcirrf",
                "virrf",
                "vbcRetPrev",
                "vRetPrev"
            })
            public static class RetTrib {

                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vRetPIS;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vRetCOFINS;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vRetCSLL;
                @XmlElement(name = "vBCIRRF", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vbcirrf;
                @XmlElement(name = "vIRRF", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String virrf;
                @XmlElement(name = "vBCRetPrev", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vbcRetPrev;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String vRetPrev;
                
                public String getVRetPIS() {
                    return vRetPIS;
                }
                
                public void setVRetPIS(String value) {
                    this.vRetPIS = value;
                }

                public String getVRetCOFINS() {
                    return vRetCOFINS;
                }
                
                public void setVRetCOFINS(String value) {
                    this.vRetCOFINS = value;
                }

                public String getVRetCSLL() {
                    return vRetCSLL;
                }
                
                public void setVRetCSLL(String value) {
                    this.vRetCSLL = value;
                }

                public String getVBCIRRF() {
                    return vbcirrf;
                }

                public void setVBCIRRF(String value) {
                    this.vbcirrf = value;
                }

                public String getVIRRF() {
                    return virrf;
                }

                public void setVIRRF(String value) {
                    this.virrf = value;
                }

                public String getVBCRetPrev() {
                    return vbcRetPrev;
                }
                
                public void setVBCRetPrev(String value) {
                    this.vbcRetPrev = value;
                }

                public String getVRetPrev() {
                    return vRetPrev;
                }
                
                public void setVRetPrev(String value) {
                    this.vRetPrev = value;
                }

            }

        }


        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "modFrete",
            "transporta",
            "retTransp",
            "veicTransp",
            "reboque",
            "vagao",
            "balsa",
            "vol"
        })
        public static class Transp {

            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
            protected String modFrete;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected NFe.InfNFe.Transp.Transporta transporta;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected NFe.InfNFe.Transp.RetTransp retTransp;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected Veiculo veicTransp;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected List<Veiculo> reboque;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String vagao;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected String balsa;
            @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
            protected List<NFe.InfNFe.Transp.Vol> vol;
            
            public String getModFrete() {
                return modFrete;
            }

            public void setModFrete(String value) {
                this.modFrete = value;
            }

            public NFe.InfNFe.Transp.Transporta getTransporta() {
                return transporta;
            }
            
            public void setTransporta(NFe.InfNFe.Transp.Transporta value) {
                this.transporta = value;
            }
            
            public NFe.InfNFe.Transp.RetTransp getRetTransp() {
                return retTransp;
            }

            public void setRetTransp(NFe.InfNFe.Transp.RetTransp value) {
                this.retTransp = value;
            }

            public Veiculo getVeicTransp() {
                return veicTransp;
            }

            public void setVeicTransp(Veiculo value) {
                this.veicTransp = value;
            }

            public List<Veiculo> getReboque() {
                if (reboque == null) {
                    reboque = new ArrayList<Veiculo>();
                }
                return this.reboque;
            }

            public void setReboque(List<Veiculo> reboque) {
				this.reboque = reboque;
			}

            public String getVagao() {
                return vagao;
            }
            
            public void setVagao(String value) {
                this.vagao = value;
            }

            public String getBalsa() {
                return balsa;
            }
            
            public void setBalsa(String value) {
                this.balsa = value;
            }
            
            public List<NFe.InfNFe.Transp.Vol> getVol() {
                if (vol == null) {
                    vol = new ArrayList<NFe.InfNFe.Transp.Vol>();
                }
                return this.vol;
            }

			public void setVol(List<NFe.InfNFe.Transp.Vol> vol) {
				this.vol = vol;
			}



			@XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "vServ",
                "vbcRet",
                "picmsRet",
                "vicmsRet",
                "cfop",
                "cMunFG"
            })
            public static class RetTransp {

                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vServ;
                @XmlElement(name = "vBCRet", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vbcRet;
                @XmlElement(name = "pICMSRet", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String picmsRet;
                @XmlElement(name = "vICMSRet", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String vicmsRet;
                @XmlElement(name = "CFOP", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String cfop;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                protected String cMunFG;
                
                public String getVServ() {
                    return vServ;
                }

                public void setVServ(String value) {
                    this.vServ = value;
                }

                public String getVBCRet() {
                    return vbcRet;
                }

                public void setVBCRet(String value) {
                    this.vbcRet = value;
                }
                
                public String getPICMSRet() {
                    return picmsRet;
                }

                public void setPICMSRet(String value) {
                    this.picmsRet = value;
                }

                public String getVICMSRet() {
                    return vicmsRet;
                }

                public void setVICMSRet(String value) {
                    this.vicmsRet = value;
                }
                
                public String getCFOP() {
                    return cfop;
                }
                
                public void setCFOP(String value) {
                    this.cfop = value;
                }

                public String getCMunFG() {
                    return cMunFG;
                }

                public void setCMunFG(String value) {
                    this.cMunFG = value;
                }

            }


            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "cnpj",
                "cpf",
                "xNome",
                "ie",
                "xEnder",
                "xMun",
                "uf"
            })
            public static class Transporta {

                @XmlElement(name = "CNPJ", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String cnpj;
                @XmlElement(name = "CPF", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String cpf;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String xNome;
                @XmlElement(name = "IE", namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String ie;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String xEnder;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String xMun;
                @XmlElement(name = "UF", namespace = "http://www.portalfiscal.inf.br/nfe")
                @XmlSchemaType(name = "string")
                protected Uf uf;
                
                public String getCNPJ() {
                    return cnpj;
                }
                
                public void setCNPJ(String value) {
                    this.cnpj = value;
                }

                public String getCPF() {
                    return cpf;
                }
                
                public void setCPF(String value) {
                    this.cpf = value;
                }
                
                public String getXNome() {
                    return xNome;
                }

                public void setXNome(String value) {
                    this.xNome = value;
                }
                
                public String getIE() {
                    return ie;
                }

                public void setIE(String value) {
                    this.ie = value;
                }

                public String getXEnder() {
                    return xEnder;
                }
                
                public void setXEnder(String value) {
                    this.xEnder = value;
                }

                public String getXMun() {
                    return xMun;
                }

                public void setXMun(String value) {
                    this.xMun = value;
                }

                public Uf getUF() {
                    return uf;
                }

                public void setUF(Uf value) {
                    this.uf = value;
                }

            }

            
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "qVol",
                "esp",
                "marca",
                "nVol",
                "pesoL",
                "pesoB",
                "lacres"
            })
            public static class Vol {

                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String qVol;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String esp;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String marca;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String nVol;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String pesoL;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected String pesoB;
                @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
                protected List<NFe.InfNFe.Transp.Vol.Lacres> lacres;
                
                public String getQVol() {
                    return qVol;
                }

                public void setQVol(String value) {
                    this.qVol = value;
                }

                public String getEsp() {
                    return esp;
                }

                public void setEsp(String value) {
                    this.esp = value;
                }

                public String getMarca() {
                    return marca;
                }
                
                public void setMarca(String value) {
                    this.marca = value;
                }

                public String getNVol() {
                    return nVol;
                }

                public void setNVol(String value) {
                    this.nVol = value;
                }
                
                public String getPesoL() {
                    return pesoL;
                }

                public void setPesoL(String value) {
                    this.pesoL = value;
                }

                public String getPesoB() {
                    return pesoB;
                }

                public void setPesoB(String value) {
                    this.pesoB = value;
                }
                
                public List<NFe.InfNFe.Transp.Vol.Lacres> getLacres() {
                    if (lacres == null) {
                        lacres = new ArrayList<NFe.InfNFe.Transp.Vol.Lacres>();
                    }
                    return this.lacres;
                }

                
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "nLacre"
                })
                public static class Lacres {

                    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
                    protected String nLacre;

                    public String getNLacre() {
                        return nLacre;
                    }

                    public void setNLacre(String value) {
                        this.nLacre = value;
                    }

                }

            }

        }

    }

    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "qrCode",
        "urlChave"
    })
    public static class InfNFeSupl {

        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
        protected String qrCode;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
        protected String urlChave;
        
        public String getQrCode() {
            return qrCode;
        }

        public void setQrCode(String value) {
            this.qrCode = value;
        }

        public String getUrlChave() {
            return urlChave;
        }

        public void setUrlChave(String value) {
            this.urlChave = value;
        }

    }

}

