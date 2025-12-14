ALTER TABLE stock_movements
DROP CONSTRAINT IF EXISTS ck_sm_reference_type;

ALTER TABLE stock_movements
    ADD CONSTRAINT ck_sm_reference_type
        CHECK (
            reference_type IS NULL OR reference_type IN ('PURCHASE','SALE','VISIT','MANUAL','IMPORT')
            );

ALTER TABLE stock_movements
DROP CONSTRAINT IF EXISTS ck_sm_qty_sign_by_type;

ALTER TABLE stock_movements
    ADD CONSTRAINT ck_sm_qty_sign_by_type
        CHECK (
            (movement_type = 'ENTRY_PURCHASE' AND quantity > 0)
                OR (movement_type IN ('EXIT_SALE','EXIT_VISIT_CONSUMPTION') AND quantity < 0)
                OR (movement_type = 'ADJUSTMENT' AND quantity <> 0)
            );

ALTER TABLE stock_movements
DROP CONSTRAINT IF EXISTS ck_sm_unit_cost_by_type;

ALTER TABLE stock_movements
    ADD CONSTRAINT ck_sm_unit_cost_by_type
        CHECK (
            (movement_type = 'ENTRY_PURCHASE' AND unit_cost IS NOT NULL)
                OR (movement_type IN ('EXIT_SALE','EXIT_VISIT_CONSUMPTION','ADJUSTMENT'))
            );

CREATE INDEX IF NOT EXISTS idx_psb_updated_at ON product_stock_balance (updated_at DESC);
CREATE INDEX IF NOT EXISTS idx_sm_created_by ON stock_movements (created_by);
