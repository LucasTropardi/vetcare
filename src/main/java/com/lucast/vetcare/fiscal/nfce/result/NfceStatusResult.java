package com.lucast.vetcare.fiscal.nfce.result;

import java.util.ArrayList;

public record NfceStatusResult(
        String tpAmb,
        String verAplic,
        String cStat,
        String xMotivo,
        String cUf,
        String tMed
) {
    public ArrayList<String> toLegacyList() {
        ArrayList<String> list = new ArrayList<>();
        list.add(tpAmb);
        list.add(verAplic);
        list.add(cStat);
        list.add(xMotivo);
        list.add(cUf);
        list.add(tMed);
        return list;
    }
}
