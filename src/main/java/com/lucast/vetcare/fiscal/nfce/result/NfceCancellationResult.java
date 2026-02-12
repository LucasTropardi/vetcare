package com.lucast.vetcare.fiscal.nfce.result;

import java.util.ArrayList;

public record NfceCancellationResult(
        String tpAmb,
        String verAplic,
        String dhRegEvento,
        String tpEvento,
        String xEvento,
        String cStat,
        String xMotivo,
        String nSeqEvento,
        String chNFe,
        String cOrgao,
        String rawResponseXml,
        String requestUrl,
        String requestXml
) {
    public ArrayList<String> toLegacyList() {
        ArrayList<String> list = new ArrayList<>();
        list.add(tpAmb);
        list.add(verAplic);
        list.add(dhRegEvento);
        list.add(tpEvento);
        list.add(xEvento);
        list.add(cStat);
        list.add(xMotivo);
        list.add(nSeqEvento);
        list.add(chNFe);
        list.add(cOrgao);
        list.add(rawResponseXml);
        list.add(requestUrl);
        list.add(requestXml);
        return list;
    }
}
