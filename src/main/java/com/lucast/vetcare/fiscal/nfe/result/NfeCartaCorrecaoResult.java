package com.lucast.vetcare.fiscal.nfe.result;

import java.util.ArrayList;
import java.util.List;

public record NfeCartaCorrecaoResult(
        String tpAmb,
        String verAplic,
        String cOrgao,
        String cStat,
        String xMotivo,
        String chNFe,
        String tpEvento,
        String xEvento,
        String nSeqEvento,
        String dhRegEvento,
        String xmlFinal,
        String rawResponseXml
) {
    public List<String> toLegacyList() {
        List<String> retorno = new ArrayList<>();
        retorno.add(tpAmb);
        retorno.add(verAplic);
        retorno.add(cOrgao);
        retorno.add(cStat);
        retorno.add(xMotivo);
        retorno.add(chNFe);
        retorno.add(tpEvento);
        retorno.add(xEvento);
        retorno.add(nSeqEvento);
        retorno.add(dhRegEvento);
        retorno.add(xmlFinal);
        retorno.add(rawResponseXml);
        return retorno;
    }
}
