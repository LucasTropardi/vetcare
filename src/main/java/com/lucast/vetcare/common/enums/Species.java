package com.lucast.vetcare.common.enums;

public enum Species {

    DOG("Cachorro"),
    CAT("Gato"),

    BIRD("Ave"),
    RABBIT("Coelho"),
    HAMSTER("Hamster"),
    GUINEA_PIG("Porquinho-da-índia"),
    FERRET("Furão"),

    REPTILE("Réptil"),
    SNAKE("Cobra"),
    LIZARD("Lagarto"),
    TURTLE("Tartaruga"),

    FISH("Peixe"),
    HORSE("Cavalo"),
    COW("Bovino"),
    PIG("Suíno"),

    OTHER("Outro");

    private final String label;

    Species(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
