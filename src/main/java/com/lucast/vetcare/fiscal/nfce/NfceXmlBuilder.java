package com.lucast.vetcare.fiscal.nfce;

import com.lucast.vetcare.fiscal.FiscalProperties;
import com.lucast.vetcare.fiscal.issuer.IssuerData;
import com.lucast.vetcare.fiscal.issuer.IssuerService;
import org.springframework.stereotype.Component;
import com.lucast.vetcare.fiscal.util.FiscalUtils;
import com.lucast.vetcare.sales.SaleEntity;
import com.lucast.vetcare.sales.SaleItemEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class NfceXmlBuilder {

    private final FiscalProperties props;
    private final IssuerService issuerService;

    public NfceXmlBuilder(FiscalProperties props, IssuerService issuerService) {
        this.props = props;
        this.issuerService = issuerService;
    }


    public String buildFromSale(SaleEntity sale, String uf, String environment) {
        if (sale == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sale is required");
        IssuerData issuer = issuerService.resolveIssuer(sale.getCompanyId());

        String ufSigla = (uf == null || uf.isBlank()) ? props.getUf() : uf.trim();
        Integer cUF = FiscalUtils.ufToCodUf(ufSigla);

        String mod = "65"; // NFC-e
        String serie = "1";
        String nNF = String.valueOf(sale.getId());
        String dhEmi = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        // cNF determinístico (8 dígitos)
        int cnf = Math.abs(("SALE:" + sale.getId()).hashCode()) % 100_000_000;
        String cNF = String.format("%08d", cnf);

        // dados simples para dev
        String dummyKey44 = ("00000000000000000000000000000000000000000000");
        String id = "NFe" + dummyKey44;

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
           .append("<NFe xmlns=\"http://www.portalfiscal.inf.br/nfe\">")
           .append("<infNFe Id=\"").append(id).append("\" versao=\"4.00\">" );

        xml.append("<ide>")
           .append(tag("cUF", String.valueOf(cUF)))
           .append(tag("cNF", cNF))
           .append(tag("natOp", "VENDA"))
           .append(tag("mod", mod))
           .append(tag("serie", serie))
           .append(tag("nNF", nNF))
           .append(tag("dhEmi", dhEmi))
           .append(tag("tpNF", "1"))
           .append(tag("idDest", "1"))
           .append(tag("cMunFG", issuer.endereco().cMun()))
           .append(tag("tpImp", "4"))
           .append(tag("tpEmis", "1"))
           .append(tag("tpAmb", "PRODUCAO".equalsIgnoreCase(environment) ? "1" : "2"))
           .append(tag("finNFe", "1"))
           .append(tag("indFinal", "1"))
           .append(tag("indPres", "1"))
           .append(tag("procEmi", "0"))
           .append(tag("verProc", "VetCare"))
           .append("</ide>");

        xml.append("<emit>")
           .append(tag("CNPJ", digitsOnly(issuer.cnpj())))
           .append(tag("xNome", escape(issuer.xNome())))
           .append(tagIfNotBlank("xFant", escape(issuer.xFant())))
           .append("<enderEmit>")
           .append(tag("xLgr", escape(issuer.endereco().xLgr())))
           .append(tag("nro", escape(issuer.endereco().nro())))
           .append(tag("xBairro", escape(issuer.endereco().xBairro())))
           .append(tag("cMun", issuer.endereco().cMun()))
           .append(tag("xMun", escape(issuer.endereco().xMun())))
           .append(tag("UF", ufSigla))
           .append(tag("CEP", digitsOnly(issuer.endereco().cep())))
           .append(tag("cPais", "1058"))
           .append(tag("xPais", "BRASIL"))
           .append("</enderEmit>")
           .append(tagIfNotBlank("IE", digitsOnly(issuer.ie())))
           .append(tag("CRT", issuer.crt()))
           .append("</emit>");

        int itemN = 1;
        BigDecimal vProdTotal = BigDecimal.ZERO;
        for (SaleItemEntity it : sale.getItems()) {
            BigDecimal vProd = nvl(it.getTotal());
            vProdTotal = vProdTotal.add(vProd);

            xml.append("<det nItem=\"").append(itemN++).append("\">")
               .append("<prod>")
               .append(tag("cProd", String.valueOf(it.getProductId())))
               .append(tag("cEAN", "SEM GTIN"))
               .append(tag("xProd", escape(nullToEmpty(it.getDescriptionSnapshot()))))
               .append(tag("NCM", "00000000"))
               .append(tag("CFOP", "5102"))
               .append(tag("uCom", escape(nullToEmpty(it.getUnitSnapshot(), "UN"))))
               .append(tag("qCom", fmt(nvl(it.getQuantity()))))
               .append(tag("vUnCom", fmt(nvl(it.getUnitPrice()))))
               .append(tag("vProd", fmt(vProd)))
               .append(tag("cEANTrib", "SEM GTIN"))
               .append(tag("uTrib", escape(nullToEmpty(it.getUnitSnapshot(), "UN"))))
               .append(tag("qTrib", fmt(nvl(it.getQuantity()))))
               .append(tag("vUnTrib", fmt(nvl(it.getUnitPrice()))))
               .append(tag("indTot", "1"))
               .append("</prod>")
               .append("<imposto>")
               .append("<ICMS>")
               .append("<ICMSSN102>")
               .append(tag("orig", "0"))
               .append(tag("CSOSN", "102"))
               .append("</ICMSSN102>")
               .append("</ICMS>")
               .append("<PIS><PISNT>")
               .append(tag("CST", "07"))
               .append("</PISNT></PIS>")
               .append("<COFINS><COFINSNT>")
               .append(tag("CST", "07"))
               .append("</COFINSNT></COFINS>")
               .append("</imposto>")
               .append("</det>");
        }

        BigDecimal vNF = nvl(sale.getTotal()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal vDesc = nvl(sale.getDiscount()).setScale(2, RoundingMode.HALF_UP);

        xml.append("<total><ICMSTot>")
           .append(tag("vBC", "0.00"))
           .append(tag("vICMS", "0.00"))
           .append(tag("vICMSDeson", "0.00"))
           .append(tag("vFCP", "0.00"))
           .append(tag("vBCST", "0.00"))
           .append(tag("vST", "0.00"))
           .append(tag("vFCPST", "0.00"))
           .append(tag("vFCPSTRet", "0.00"))
           .append(tag("vProd", fmt(vProdTotal)))
           .append(tag("vFrete", "0.00"))
           .append(tag("vSeg", "0.00"))
           .append(tag("vDesc", fmt(vDesc)))
           .append(tag("vII", "0.00"))
           .append(tag("vIPI", "0.00"))
           .append(tag("vIPIDevol", "0.00"))
           .append(tag("vPIS", "0.00"))
           .append(tag("vCOFINS", "0.00"))
           .append(tag("vOutro", "0.00"))
           .append(tag("vNF", fmt(vNF)))
           .append(tag("vTotTrib", "0.00"))
           .append("</ICMSTot></total>");

        xml.append("<transp>")
           .append(tag("modFrete", "9"))
           .append("</transp>");

        xml.append("<pag>")
           .append("<detPag>")
           .append(tag("indPag", "0"))
           .append(tag("tPag", "01"))
           .append(tag("vPag", fmt(vNF)))
           .append("</detPag>")
           .append("</pag>");

        xml.append("<infAdic>")
           .append(tag("infCpl", "Sale #" + sale.getId()))
           .append("</infAdic>");

        xml.append("</infNFe></NFe>");
        return xml.toString();
    }

    private static void requireFilled(String value, String propKey) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Missing config: " + propKey + " (needed to build NFC-e XML)");
        }
    }

    private static String tag(String name, String value) {
        return "<" + name + ">" + (value == null ? "" : value) + "</" + name + ">";
    }

    private static String tagIfNotBlank(String name, String value) {
        if (value == null || value.isBlank()) return "";
        return tag(name, value);
    }

    private static String digitsOnly(String s) {
        if (s == null) return "";
        return s.replaceAll("\\D", "");
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private static BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private static String fmt(BigDecimal v) {
        return nvl(v).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static String nullToEmpty(String v) {
        return v == null ? "" : v;
    }

    private static String nullToEmpty(String v, String fallback) {
        if (v == null || v.isBlank()) return fallback;
        return v;
    }
}
