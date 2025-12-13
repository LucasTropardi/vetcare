package com.lucast.vetcare.common.enums;

public enum ProductCategory {
    MEDICINE("Medicamento"),
    SUPPLY("Insumo"),
    FEED("Ração"),
    OTHER("Outro");

    private final String label;

    ProductCategory(String label) { this.label = label; }

    public String getLabel() { return label; }
}
