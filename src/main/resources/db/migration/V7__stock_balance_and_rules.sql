-- snapshot de saldo/custo médio
CREATE TABLE IF NOT EXISTS product_stock_balance (
    product_id BIGINT PRIMARY KEY REFERENCES products(id) ON DELETE RESTRICT,
    on_hand    NUMERIC(12,3) NOT NULL DEFAULT 0,
    avg_cost   NUMERIC(12,2) NOT NULL DEFAULT 0,
    updated_at TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT ck_psb_on_hand_non_negative CHECK (on_hand >= 0),
    CONSTRAINT ck_psb_avg_cost_non_negative CHECK (avg_cost >= 0)
    );

-- auditoria e referência controlada
ALTER TABLE stock_movements
    ADD COLUMN IF NOT EXISTS created_by BIGINT REFERENCES users(id) ON DELETE RESTRICT;

ALTER TABLE stock_movements
    ADD CONSTRAINT ck_sm_reference_type
        CHECK (
            reference_type IS NULL OR reference_type IN ('PURCHASE','SALE','VISIT','MANUAL','IMPORT')
            );

-- checks de sinal por tipo (quantity com sinal)
ALTER TABLE stock_movements
    ADD CONSTRAINT ck_sm_qty_sign_by_type
        CHECK (
            (movement_type = 'ENTRY_PURCHASE' AND quantity > 0)
                OR (movement_type IN ('EXIT_SALE','EXIT_VISIT_CONSUMPTION') AND quantity < 0)
                OR (movement_type = 'ADJUSTMENT' AND quantity <> 0)
            );

-- custo por tipo
ALTER TABLE stock_movements
    ADD CONSTRAINT ck_sm_unit_cost_by_type
        CHECK (
            (movement_type = 'ENTRY_PURCHASE' AND unit_cost IS NOT NULL)
                OR (movement_type IN ('EXIT_SALE','EXIT_VISIT_CONSUMPTION','ADJUSTMENT'))
            );

CREATE INDEX IF NOT EXISTS idx_psb_updated_at ON product_stock_balance (updated_at DESC);
CREATE INDEX IF NOT EXISTS idx_sm_created_by ON stock_movements (created_by);
