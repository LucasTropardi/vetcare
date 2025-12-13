package com.lucast.vetcare.common.enums;

public enum ItemType {
    PRODUCT("Produto"),
    SERVICE("Serviço");

    private final String label;

    ItemType(String label) { this.label = label; }

    public String getLabel() { return label; }
}
