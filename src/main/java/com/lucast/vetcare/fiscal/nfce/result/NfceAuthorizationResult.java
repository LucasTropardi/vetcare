package com.lucast.vetcare.fiscal.nfce.result;

import java.util.ArrayList;

public record NfceAuthorizationResult(
        String tpAmb,
        String verAplic,
        String dhRecbto,
        String nProt,
        String digVal,
        String cStat,
        String xMotivo,
        String cUf,
        String chNFe,
        String versao,
        String rawResponseXml
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
        list.add(versao);
        list.add(rawResponseXml);
        return list;
    }
}
