CREATE INDEX IF NOT EXISTS idx_products_sku ON products (sku);
CREATE INDEX IF NOT EXISTS idx_products_name ON products (name);

ALTER TABLE stock_movements
    ADD CONSTRAINT ck_sm_adjustment_notes_required
        CHECK (
            movement_type <> 'ADJUSTMENT'
                OR (notes IS NOT NULL AND length(trim(notes)) > 0)
            );

ALTER TABLE stock_movements
DROP CONSTRAINT IF EXISTS ck_sm_reference_type;

ALTER TABLE stock_movements
    ADD CONSTRAINT ck_sm_reference_type
        CHECK (
            reference_type IS NULL
                OR reference_type IN ('PURCHASE','SALE','VISIT','MANUAL','IMPORT','REVERSAL')
            );

ALTER TABLE stock_movements
    ADD CONSTRAINT ck_sm_reversal_reference_id_required
        CHECK (
            reference_type <> 'REVERSAL'
                OR reference_id IS NOT NULL
            );
