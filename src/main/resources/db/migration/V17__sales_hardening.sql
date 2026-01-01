CREATE INDEX IF NOT EXISTS idx_sale_payments_sale_status
    ON sale_payments (sale_id, status);

ALTER TABLE sale_payments
    ADD COLUMN IF NOT EXISTS notes VARCHAR(200);