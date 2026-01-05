package com.lucast.vetcare.fiscal.nfe.print;

import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.lucast.vetcare.fiscal.util.FiscalUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class NfeProcPrint {

    private static final DecimalFormat FORMAT_CURRENCY = new DecimalFormat("#,##0.00");

    private FiscalUtil fiscalUtil = new FiscalUtil();

    private String xml;

    public NfeProcPrint(String xml) {
        this.xml = xml;
    }

    public String getTipoAmbiente() {
        return fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "ide"), "tpAmb");
    }

    public String getDataEmissao() {
        String dhEmi = fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "ide"), "dhEmi");
        if (dhEmi != null && !dhEmi.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
                LocalDate localDate = LocalDate.parse(dhEmi, formatter);
                return localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (DateTimeParseException e) {
                e.printStackTrace();
                return "";
            }
        } else {
            return "";
        }
    }

    public String getHoraEmissao() {
        String dhEmi = fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "ide"), "dhEmi");
        if (dhEmi != null && !dhEmi.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
                LocalDateTime dateTime = LocalDateTime.parse(dhEmi, formatter);
                return dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            } catch (DateTimeParseException e) {
                e.printStackTrace();
                return "";
            }
        } else {
            return "";
        }
    }

    public String getDataSaidaEntrada() {
        String dhSaiEnt = fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "ide"), "dhSaiEnt");
        if (dhSaiEnt != null && !dhSaiEnt.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
                LocalDate localDate = LocalDate.parse(dhSaiEnt, formatter);
                return localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (DateTimeParseException e) {
                e.printStackTrace();
                return "";
            }
        } else {
            return "";
        }
    }

    public String getHoraSaidaEntrada() {
        String dhSaiEnt = fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "ide"), "dhSaiEnt");
        if (dhSaiEnt != null && !dhSaiEnt.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
                LocalDateTime dateTime = LocalDateTime.parse(dhSaiEnt, formatter);
                return dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            } catch (DateTimeParseException e) {
                e.printStackTrace();
                return "";
            }
        } else {
            return "";
        }
    }

    public String getNumeroNota() {
        String numero = getNestedTag("ide", "nNF");
        return numero.isEmpty() ? "" : numero.replaceAll("(\\d{3})(\\d{3})(\\d{3})", "$1.$2.$3");
    }

    public String getNaturezaOperacao() {
        return getNestedTag("ide", "natOp");
    }

    public String getModelo() {
        return getNestedTag("ide", "mod");
    }

    public String getTipoNF() {
        return getNestedTag("ide", "tpNF");
    }

    public String getSerie() {
        return getNestedTag("ide", "serie");
    }

    public String getChaveAcesso() {
        // Tenta buscar chNFe no protocolo (infProt)
        String chave = getNestedTag("infProt", "chNFe");

        // Se não encontrou no protocolo, tenta extrair do atributo Id de infNFe
        if (chave == null || chave.isEmpty()) {
            String id = getAttributeFromTag("infNFe", "Id");
            if (id != null && id.startsWith("NFe")) {
                chave = id.substring(3); // remove o prefixo NFe
            }
        }

        if (chave == null || chave.isEmpty()) {
            return "";
        }

        return chave.replaceAll("(.{4})(?=.{4})", "$1 ");
    }

    private String getAttributeFromTag(String tagName, String attributeName) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(this.xml)));

            NodeList list = document.getElementsByTagName(tagName);
            if (list == null || list.getLength() == 0) {
                return "";
            }

            Node node = list.item(0);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String value = element.getAttribute(attributeName);
                if (value != null) {
                    return value;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public String getProtocoloAutorizacao() {
        return getNestedTag("protNFe", "nProt");
    }

    private String getNestedTag(String parentTag, String childTag) {
        String parent = fiscalUtil.pegaTag(xml, parentTag);
        return (parent != null) ? fiscalUtil.pegaTag(parent, childTag) : "";
    }

    public String getDataProtocoloAutorizacao() {
        String value = getNestedTag("protNFe", "dhRecbto");
        if (value.isEmpty()) {
            return "";
        }

        DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        LocalDateTime dateTime = LocalDateTime.parse(value, inputFormatter);
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public String getIEEmitente() {
        return getEmitenteTag("IE");
    }

    public String getCnpjEmi() {
        return formatCnpj(getEmitenteTag("CNPJ"));
    }

    public String getRazaoSocialEmi() {
        return getEmitenteTag("xNome");
    }

    public String getEnderecoEmi() {
        return getEmitenteTag("xLgr") + ", " + getEmitenteTag("nro");
    }

    public String getBairroEmi() {
        return getEmitenteTag("xBairro");
    }

    public String getMunicipioEmi() {
        return getEmitenteTag("xMun");
    }

    public String getCepEmi() {
        return formatCep(getEmitenteTag("CEP"));
    }

    public String getUFEmi() {
        return getEmitenteTag("UF");
    }

    public String getPaisEmi() {
        return getEmitenteTag("xPais");
    }

    private String getEmitenteTag(String childTag) {
        return getNestedTag("emit", childTag); // aquele helper que comentei antes
    }

    private String formatCnpj(String cnpj) {
        return cnpj == null || cnpj.isEmpty() ? ""
                : cnpj.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }

    private String formatCep(String cep) {
        return cep == null || cep.isEmpty() ? ""
                : cep.replaceFirst("(\\d{5})(\\d{3})", "$1-$2");
    }

    private String formatFone(String fone) {
        if (fone == null) return "";

        String digits = fone.replaceAll("\\D", "");

        if (digits.length() == 10) {
            return digits.replaceFirst("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        } else if (digits.length() == 11) {
            return digits.replaceFirst("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        }

        return fone;
    }

    public String getIEDest() {
        return getDestTag("IE");
    }

    public String getCnpjDest() {
        String cnpj = getDestTag("CNPJ");
        if (cnpj == null || cnpj.isBlank()) {
            return getDestTag("idEstrangeiro"); // CNPJ estrangeiro
        }
        return formatCnpj(cnpj);
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

    public String getRazaoSocialDest() {
        String razaoSocialDest = getDestTag("xNome");

        String infCpl = getInfAdic();
        if (infCpl != null && !infCpl.isEmpty()) {
            infCpl = infCpl.replace(";", System.lineSeparator());

            if (infCpl.contains("IDENT:")) {
                String cIdent = infCpl.substring(infCpl.indexOf("IDENT:") + 7);

                int posEspaco = cIdent.indexOf(" ");
                if (posEspaco > 0) {
                    cIdent = cIdent.substring(0, posEspaco);
                }

                razaoSocialDest += " - " + cIdent;
            }
        }

        return razaoSocialDest;
    }

    public String getEnderecoDest() {
        return getDestTag("xLgr") + ", " + getDestTag("nro");
    }

    public String getBairroDest() {
        return getDestTag("xBairro");
    }

    public String getMunicipioDest() {
        return getDestTag("xMun");
    }

    public String geFoneDest() {
        String fone = getDestTag("fone");
        return (fone == null || fone.isBlank()) ? "" : formatFone(fone);
    }

    public String getCepDest() {
        String cep = getDestTag("CEP");
        return (cep == null || cep.isBlank()) ? "" : formatCep(cep);
    }

    public String getUFDest() {
        return getDestTag("UF");
    }

    public String getPaisDest() {
        return getDestTag("xPais");
    }

    private String getDestTag(String childTag) {
        return getNestedTag("dest", childTag);
    }

    public String getNroDuplicata() {
        String nDup = getCobrTag("nDup");
        return (nDup != null) ? nDup : "";
    }

    public String getDataVencimentoDuplicata() {
        String dataOriginal = getCobrTag("dVenc");
        if (dataOriginal == null || dataOriginal.isBlank()) {
            return "";
        }

        try {
            SimpleDateFormat formatoOriginal = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatoDesejado = new SimpleDateFormat("dd/MM/yyyy");
            Date data = formatoOriginal.parse(dataOriginal);
            return formatoDesejado.format(data);
        } catch (ParseException e) {
            e.printStackTrace();
            return dataOriginal;
        }
    }

    public BigDecimal getValorDuplicata() {
        String vDupTag = getCobrTag("vDup");
        if (vDupTag == null || vDupTag.isBlank()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return new BigDecimal(vDupTag).setScale(2, RoundingMode.HALF_UP);
    }

    private String getCobrTag(String childTag) {
        String cobr = fiscalUtil.pegaTag(xml, "cobr");
        return (cobr != null) ? fiscalUtil.pegaTag(cobr, childTag) : null;
    }

    public BigDecimal getBCIcms() {
        return getValorTagICMSTot("vBC");
    }

    public BigDecimal getValorIcms() {
        return getValorTagICMSTot("vICMS");
    }

    public BigDecimal getValorFrete() {
        return getValorTagICMSTot("vFrete");
    }

    public BigDecimal getValorSeguro() {
        return getValorTagICMSTot("vSeg");
    }

    public BigDecimal getDescontos() {
        return getValorTagICMSTot("vDesc");
    }

    public BigDecimal getValorIpi() {
        return getValorTagICMSTot("vIPI");
    }

    public BigDecimal getValorTotalNf() {
        return getValorTagICMSTot("vNF");
    }

    public BigDecimal getValorTotalProdutos() {
        return getValorTagICMSTot("vProd");
    }

    public BigDecimal getOutrasDespesas() {
        return getValorTagICMSTot("vOutro");
    }

    public BigDecimal getBCIcmsSubs() {
        return getValorTagICMSTot("vBCST");
    }

    public BigDecimal getValorIcmsSubs() {
        return getValorTagICMSTot("vST");
    }

    private BigDecimal getValorTagICMSTot(String tag) {
        String valor = fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "ICMSTot"), tag);
        if (valor == null || valor.isBlank()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return new BigDecimal(valor).setScale(2, RoundingMode.HALF_UP);
    }

    public String getFretePorConta() {
        String mod = getTransporteTag("modFrete");
        if (mod.isEmpty()) {
            return "";
        }

        return "1".equals(mod) ? mod + "-DESTINATARIO" : mod + "-EMITENTE";
    }

    public String getVolumes() {
        return getTransporteTag("qVol");
    }

    public String getEspecie() {
        return getTransporteTag("esp");
    }

    private String getTransporteTag(String tagName) {
        String transp = fiscalUtil.pegaTag(xml, "transp");
        if (transp == null) {
            return "";
        }
        String value = fiscalUtil.pegaTag(transp, tagName);
        return value != null ? value : "";
    }

    public BigDecimal getPesoLiquido() {
        return getTransporteTagAsBigDecimal("pesoL", 3);
    }

    public BigDecimal getPesoBruto() {
        return getTransporteTagAsBigDecimal("pesoB", 3);
    }

    private BigDecimal getTransporteTagAsBigDecimal(String tagName, int scale) {
        String transp = fiscalUtil.pegaTag(xml, "transp");
        if (transp == null) {
            return BigDecimal.ZERO.setScale(scale, RoundingMode.HALF_UP);
        }
        String value = fiscalUtil.pegaTag(transp, tagName);
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO.setScale(scale, RoundingMode.HALF_UP);
        }
        try {
            return new BigDecimal(value).setScale(scale, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            // caso o valor no XML seja inválido
            return BigDecimal.ZERO.setScale(scale, RoundingMode.HALF_UP);
        }
    }

    public String getInfAdic() {
        String infAdic = fiscalUtil.pegaTag(xml, "infAdic");
        if (infAdic == null) {
            return "";
        }
        String infCpl = fiscalUtil.pegaTag(infAdic, "infCpl");
        return (infCpl != null) ? infCpl : "";
    }

    public List<ProdutoInfo> getProd() {
        List<ProdutoInfo> produtos = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xml)));

            NodeList detalhes = document.getElementsByTagName("det");

            for (int i = 0; i < detalhes.getLength(); i++) {
                Element detalhe = (Element) detalhes.item(i);
                String nItem = detalhe.getAttribute("nItem");

                NodeList prodNodes = detalhe.getElementsByTagName("prod");
                if (prodNodes == null || prodNodes.getLength() == 0) {
                    System.out.println("Elemento <prod> não encontrado para o item: " + nItem);
                    continue;
                }

                Element prod = (Element) prodNodes.item(0);

                String codProduto = getTextContentSafe(prod, "cProd");
                String descProduto = getTextContentSafe(prod, "xProd");
                String ncm = getTextContentSafe(prod, "NCM");
                String cfop = getTextContentSafe(prod, "CFOP");
                String unidade = getTextContentSafe(prod, "uCom");
                BigDecimal quantidade = new BigDecimal(getTextContentSafe(prod, "qCom", "0")).setScale(3, RoundingMode.HALF_UP);
                BigDecimal vlUnitario = new BigDecimal(getTextContentSafe(prod, "vUnCom", "0"));
                BigDecimal vlTotal = new BigDecimal(getTextContentSafe(prod, "vProd", "0"));

                BigDecimal bcIcms = getBCProduto(detalhe);
                BigDecimal vlIcms = getIcmsProduto(detalhe);
                BigDecimal vlIpi = getIpiProduto(detalhe);
                BigDecimal aliqIcms = getAliqIcmsProduto(detalhe);
                BigDecimal aliqIpi = getAliqIpiProduto(detalhe);

                String infAdicional = getTextContentSafe(detalhe, "infAdProd");

                ProdutoInfo info = new ProdutoInfo(
                        codProduto, descProduto, ncm, getCstProduto(detalhe), cfop, unidade,
                        quantidade, vlUnitario, vlTotal, bcIcms, vlIcms, vlIpi, aliqIcms, aliqIpi, infAdicional
                );

                produtos.add(info);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return produtos;
    }

    public List<DuplicataInfo> getDuplicatas() {
        List<DuplicataInfo> duplicatas = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(this.xml)));

            NodeList dupNodes = document.getElementsByTagName("dup");

            DuplicataInfo.Builder builderDup = null;
            int colIndex = 0;

            for (int i = 0; i < dupNodes.getLength(); i++) {
                Element dupElement = (Element) dupNodes.item(i);

                String nDup = dupElement.getElementsByTagName("nDup").item(0).getTextContent();
                String dVencOriginal = dupElement.getElementsByTagName("dVenc").item(0).getTextContent();
                String vDupStr = dupElement.getElementsByTagName("vDup").item(0).getTextContent();

                LocalDate date = LocalDate.parse(dVencOriginal);
                String dVencFormatado = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                BigDecimal vDup = convetStringToBigDecimal(vDupStr);

                if (colIndex == 0) {
                    builderDup = new DuplicataInfo.Builder();
                }

                switch (colIndex) {
                    case 0:
                        builderDup.nDup1(nDup).dVenc1(dVencFormatado).vDup1(vDup);
                        break;
                    case 1:
                        builderDup.nDup2(nDup).dVenc2(dVencFormatado).vDup2(vDup);
                        break;
                    case 2:
                        builderDup.nDup3(nDup).dVenc3(dVencFormatado).vDup3(vDup);
                        break;
                }

                colIndex++;

                if (colIndex == 3 || i == dupNodes.getLength() - 1) {
                    while (colIndex < 3) {
                        switch (colIndex) {
                            case 1:
                                builderDup.nDup2("").dVenc2("").vDup2(null);
                                break;
                            case 2:
                                builderDup.nDup3("").dVenc3("").vDup3(null);
                                break;
                        }
                        colIndex++;
                    }

                    duplicatas.add(builderDup.build());
                    colIndex = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return duplicatas;
    }

    public static String StringFormatToCurrency(String valor) {
        if (valor == null) return null;
        return formatCurrency(convetStringToBigDecimal(valor));
    }

    public static String formatCurrency(BigDecimal valor) {
        if (valor == null) return null;
        return FORMAT_CURRENCY.format(valor);
    }

    public static BigDecimal convetStringToBigDecimal(String valorStr) {
        if (valorStr == null || valorStr.trim().isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        try {
            BigDecimal valor = new BigDecimal(valorStr);
            return valor.setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            System.err.println("Erro ao converter '" + valorStr + "' para BigDecimal. Retornando 0.00");
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
    }

    private String getTextContentSafe(Element element, String tagName) {
        return getTextContentSafe(element, tagName, "");
    }

    private String getTextContentSafe(Element element, String tagName, String defaultValue) {
        NodeList nodes = element.getElementsByTagName(tagName);
        if (nodes != null && nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return defaultValue;
    }


    private BigDecimal getAliqIpiProduto(Element detalhe) {
        try {
            Element impostoElement = (Element) detalhe.getElementsByTagName("imposto").item(0);
            if (impostoElement != null) {
                Element ipiElement = (Element) impostoElement.getElementsByTagName("IPI").item(0);
                if (ipiElement != null) {
                    Element ipiTribElement = (Element) ipiElement.getElementsByTagName("IPITrib").item(0);
                    if (ipiTribElement != null) {
                        String pIPI = ipiTribElement.getElementsByTagName("pIPI").item(0).getTextContent();
                        return new BigDecimal(pIPI).setScale(2, RoundingMode.HALF_UP);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getAliqIcmsProduto(Element detalhe) {
        try {
            Element impostoElement = (Element) detalhe.getElementsByTagName("imposto").item(0);
            if (impostoElement != null) {
                Element icmsElement = (Element) impostoElement.getElementsByTagName("ICMS").item(0);
                if (icmsElement != null) {
                    String[] icmsTypes = {"ICMS00", "ICMS10", "ICMS20", "ICMS51", "ICMS70", "ICMS90"};

                    for (String icmsType : icmsTypes) {
                        Element icmsSpecificElement = (Element) icmsElement.getElementsByTagName(icmsType).item(0);
                        if (icmsSpecificElement != null) {
                            Element pIcmsElement = (Element) icmsSpecificElement.getElementsByTagName("pICMS").item(0);
                            if (pIcmsElement != null) {
                                String pICMS = pIcmsElement.getTextContent();
                                return new BigDecimal(pICMS).setScale(2, RoundingMode.HALF_UP);
                            }
                        }
                    }

                    String[] zeroIcmsTypes = {"ICMS30", "ICMS40", "ICMS60"};
                    for (String zeroIcmsType : zeroIcmsTypes) {
                        if (icmsElement.getElementsByTagName(zeroIcmsType).getLength() > 0) {
                            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getIpiProduto(Element detalhe) {
        try {
            Element impostoElement = (Element) detalhe.getElementsByTagName("imposto").item(0);
            if (impostoElement != null) {
                Element ipiElement = (Element) impostoElement.getElementsByTagName("IPI").item(0);
                if (ipiElement != null) {
                    Element ipiTribElement = (Element) ipiElement.getElementsByTagName("IPITrib").item(0);
                    if (ipiTribElement != null) {
                        Element vIpiElement = (Element) ipiTribElement.getElementsByTagName("vIPI").item(0);
                        if (vIpiElement != null) {
                            String vIPI = vIpiElement.getTextContent();
                            return new BigDecimal(vIPI).setScale(2, RoundingMode.HALF_UP);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getIcmsProduto(Element detalhe) {
        try {
            Element impostoElement = (Element) detalhe.getElementsByTagName("imposto").item(0);
            if (impostoElement != null) {
                Element icmsElement = (Element) impostoElement.getElementsByTagName("ICMS").item(0);
                if (icmsElement != null) {
                    String[] icmsTypes = {"ICMS00", "ICMS10", "ICMS20", "ICMS30", "ICMS40", "ICMS51", "ICMS60", "ICMS70", "ICMS90"};

                    for (String icmsType : icmsTypes) {
                        Element icmsTypeElement = (Element) icmsElement.getElementsByTagName(icmsType).item(0);
                        if (icmsTypeElement != null) {
                            Element vIcmsElement = (Element) icmsTypeElement.getElementsByTagName("vICMS").item(0);
                            if (vIcmsElement != null) {
                                String vICMS = vIcmsElement.getTextContent();
                                return new BigDecimal(vICMS).setScale(2, RoundingMode.HALF_UP);
                            }
                            if (icmsType.equals("ICMS30") || icmsType.equals("ICMS40") || icmsType.equals("ICMS60")) {
                                return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getBCProduto(Element detalhe) {
        try {
            Element impostoElement = (Element) detalhe.getElementsByTagName("imposto").item(0);
            if (impostoElement != null) {
                Element icmsElement = (Element) impostoElement.getElementsByTagName("ICMS").item(0);
                if (icmsElement != null) {
                    String[] icmsTypes = {"ICMS00", "ICMS10", "ICMS20", "ICMS30", "ICMS40", "ICMS51", "ICMS60", "ICMS70", "ICMS90"};

                    for (String icmsType : icmsTypes) {
                        Element icmsTypeElement = (Element) icmsElement.getElementsByTagName(icmsType).item(0);
                        if (icmsTypeElement != null) {
                            Element vBcElement = (Element) icmsTypeElement.getElementsByTagName("vBC").item(0);
                            if (vBcElement != null) {
                                String vBC = vBcElement.getTextContent();
                                return new BigDecimal(vBC).setScale(2, RoundingMode.HALF_UP);
                            }
                            if (icmsType.equals("ICMS30") || icmsType.equals("ICMS40") || icmsType.equals("ICMS60")) {
                                return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }


    public String getCstProduto(Element detalhe) {
        try {
            Element impostoElement = (Element) detalhe.getElementsByTagNameNS("*", "imposto").item(0);
            if (impostoElement != null) {
                Element icmsElement = (Element) impostoElement.getElementsByTagNameNS("*", "ICMS").item(0);
                if (icmsElement != null) {
                    String[] icmsTypes = {"ICMS00", "ICMS10", "ICMS20", "ICMS30", "ICMS40", "ICMS51", "ICMS60", "ICMS70", "ICMS90"};

                    for (String icmsType : icmsTypes) {
                        Element icmsTypeElement = (Element) icmsElement.getElementsByTagName(icmsType).item(0);
                        if (icmsTypeElement != null) {
                            Element origElement = (Element) icmsTypeElement.getElementsByTagName("orig").item(0);
                            Element cstElement = (Element) icmsTypeElement.getElementsByTagName("CST").item(0);

                            // Verifica se ambos os elementos <orig> e <CST> estão presentes
                            if (origElement != null && cstElement != null) {
                                String orig = origElement.getTextContent();
                                String cst = cstElement.getTextContent();
                                return orig + cst;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getCfopProduto(Element detalhe) {
        try {
            Element prodElement = (Element) detalhe.getElementsByTagName("prod").item(0);
            if (prodElement != null) {
                Element cfopElement = (Element) prodElement.getElementsByTagName("CFOP").item(0);
                if (cfopElement != null) {
                    return cfopElement.getTextContent();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getUNProduto(Element detalhe) {
        try {
            Element prodElement = (Element) detalhe.getElementsByTagName("prod").item(0);
            if (prodElement != null) {
                Element uComElement = (Element) prodElement.getElementsByTagName("uCom").item(0);
                if (uComElement != null) {
                    return uComElement.getTextContent();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public BigDecimal getQuantProduto(Element detalhe) {
        try {
            Element prodElement = (Element) detalhe.getElementsByTagName("prod").item(0);
            if (prodElement != null) {
                Element qComElement = (Element) prodElement.getElementsByTagName("qCom").item(0);
                if (qComElement != null) {
                    return new BigDecimal(qComElement.getTextContent()).setScale(3, RoundingMode.HALF_UP);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getVlUniProduto(Element detalhe) {
        try {
            Element prodElement = (Element) detalhe.getElementsByTagName("prod").item(0);
            if (prodElement != null) {
                Element vUnComElement = (Element) prodElement.getElementsByTagName("vUnCom").item(0);
                if (vUnComElement != null) {
                    return new BigDecimal(vUnComElement.getTextContent()).setScale(2, RoundingMode.HALF_UP);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getVlTotProduto(Element detalhe) {
        try {
            Element prodElement = (Element) detalhe.getElementsByTagName("prod").item(0);
            if (prodElement != null) {
                Element vProdElement = (Element) prodElement.getElementsByTagName("vProd").item(0);
                if (vProdElement != null) {
                    return new BigDecimal(vProdElement.getTextContent()).setScale(2, RoundingMode.HALF_UP);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }


    public String getCNPJTransportadora() {
        return fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "transp"), "transporta") != null ? fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "transporta"), "CNPJ") : "";
    }

    public String getRazaoSocialTransportadora() {
        return fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "transp"), "transporta") != null ? fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "transporta"), "xNome") : "";
    }

    public String getIETransportadora() {
        return fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "transp"), "transporta") != null ? fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "transporta"), "IE") : "";
    }

    public String getEnderecoTransportadora() {
        return fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "transp"), "transporta") != null ? fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "transporta"), "xEnder") : "";
    }

    public String getCidadeTransportadora() {
        return fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "transp"), "transporta") != null ? fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "transporta"), "xMun") : "";
    }

    public String getUFTransportadora() {
        return fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "transp"), "transporta") != null ? fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "transporta"), "UF") : "";
    }

    public String getVeiculoPlaca() {
        return fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "transp"), "veicTransp") != null ? fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "veicTransp"), "placa") : "";
    }

    public String getVeiculoUF() {
        return fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "transp"), "veicTransp") != null ? fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "veicTransp"), "UF") : "";
    }

    public String getVeiculoRNTC() {
        return fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "transp"), "veicTransp") != null ? fiscalUtil.pegaTag(fiscalUtil.pegaTag(xml, "veicTransp"), "RNTC") : "";
    }
}

