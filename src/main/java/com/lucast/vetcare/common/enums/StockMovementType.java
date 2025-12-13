package com.lucast.vetcare.common.enums;

public enum StockMovementType {
    ENTRY_PURCHASE("Entrada (Compra)"),
    EXIT_SALE("Saída (Venda)"),
    EXIT_VISIT_CONSUMPTION("Saída (Consumo em Atendimento)"),
    ADJUSTMENT("Ajuste");

    private final String label;

    StockMovementType(String label) { this.label = label; }

    public String getLabel() { return label; }
}
