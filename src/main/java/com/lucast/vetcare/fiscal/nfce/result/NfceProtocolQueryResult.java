package com.lucast.vetcare.fiscal.nfce.result;

import java.util.ArrayList;

public record NfceProtocolQueryResult(
        String tpAmb,
        String verAplic,
        String dhRecbto,
        String nProt,
        String digVal,
        String cStat,
        String xMotivo,
        String cUf,
        String chNFe,
        String protNFe,
        String infProt,
        String retCancNFe,
        String procEventoNFe,
        String rawResponseXml,
        String requestUrl,
        String requestXml
) {
    public ArrayList<String> toLegacyList() {
        ArrayList<String> list = new ArrayList<>();
        list.add(tpAmb);
        list.add(verAplic);
        list.add(dhRecbto);
        list.add(nProt);
        list.add(digVal);
        list.add(cStat);
        list.add(xMotivo);
        list.add(cUf);
        list.add(chNFe);
        list.add(protNFe);
        list.add(infProt);
        list.add(retCancNFe);
        list.add(procEventoNFe);
        list.add(rawResponseXml);
        list.add(requestUrl);
        list.add(requestXml);
        return list;
    }
}
