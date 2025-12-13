package com.lucast.vetcare.common.enums;

public enum PetSex {

    MALE("Macho"),
    FEMALE("Fêmea"),
    UNKNOWN("Desconhecido");

    private final String label;

    PetSex(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
