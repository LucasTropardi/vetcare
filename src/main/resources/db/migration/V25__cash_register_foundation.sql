CREATE TABLE IF NOT EXISTS cash_registers (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES company(id) ON DELETE RESTRICT,
    register_code VARCHAR(40) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    opening_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    expected_closing_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    closing_amount NUMERIC(12,2),
    opened_by BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    opened_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    closed_by BIGINT REFERENCES users(id) ON DELETE RESTRICT,
    closed_at TIMESTAMPTZ,
    notes VARCHAR(300),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ck_cash_registers_status CHECK (status IN ('OPEN','CLOSED','CANCELED')),
    CONSTRAINT ck_cash_registers_opening_nonnegative CHECK (opening_amount >= 0),
    CONSTRAINT ck_cash_registers_expected_nonnegative CHECK (expected_closing_amount >= 0),
    CONSTRAINT ck_cash_registers_closing_nonnegative CHECK (closing_amount IS NULL OR closing_amount >= 0),
    CONSTRAINT ck_cash_registers_close_fields CHECK (
        (status = 'OPEN' AND closed_by IS NULL AND closed_at IS NULL AND closing_amount IS NULL)
        OR (status IN ('CLOSED','CANCELED') AND closed_by IS NOT NULL AND closed_at IS NOT NULL AND closing_amount IS NOT NULL)
    )
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_cash_registers_open_by_code
    ON cash_registers (company_id, register_code)
    WHERE status = 'OPEN';

CREATE INDEX IF NOT EXISTS idx_cash_registers_company_status_opened
    ON cash_registers (company_id, status, opened_at DESC);

CREATE TABLE IF NOT EXISTS cash_register_sales (
    id BIGSERIAL PRIMARY KEY,
    cash_register_id BIGINT NOT NULL REFERENCES cash_registers(id) ON DELETE RESTRICT,
    sale_id BIGINT NOT NULL UNIQUE REFERENCES sales(id) ON DELETE RESTRICT,
    sale_number BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    issued_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    customer_name VARCHAR(120),
    customer_document VARCHAR(20),
    fiscal_document_type VARCHAR(10) NOT NULL DEFAULT 'NONE',
    fiscal_document_number VARCHAR(20),
    fiscal_document_series VARCHAR(10),
    fiscal_document_key VARCHAR(44),
    subtotal_snapshot NUMERIC(12,2) NOT NULL DEFAULT 0,
    discount_snapshot NUMERIC(12,2) NOT NULL DEFAULT 0,
    surcharge_snapshot NUMERIC(12,2) NOT NULL DEFAULT 0,
    total_snapshot NUMERIC(12,2) NOT NULL DEFAULT 0,
    exclude_services_from_fiscal BOOLEAN NOT NULL DEFAULT TRUE,
    notes VARCHAR(300),
    created_by BIGINT REFERENCES users(id) ON DELETE RESTRICT,
    closed_by BIGINT REFERENCES users(id) ON DELETE RESTRICT,
    closed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ck_cash_register_sales_status CHECK (status IN ('OPEN','CLOSED','CANCELED')),
    CONSTRAINT ck_cash_register_sales_doc_type CHECK (fiscal_document_type IN ('NONE','NFCE','NFE')),
    CONSTRAINT ck_cash_register_sales_amounts CHECK (
        subtotal_snapshot >= 0
        AND discount_snapshot >= 0
        AND surcharge_snapshot >= 0
        AND total_snapshot >= 0
        AND total_snapshot = (subtotal_snapshot - discount_snapshot + surcharge_snapshot)
    ),
    CONSTRAINT ck_cash_register_sales_close_fields CHECK (
        (status = 'OPEN' AND closed_at IS NULL AND closed_by IS NULL)
        OR (status IN ('CLOSED','CANCELED') AND closed_at IS NOT NULL AND closed_by IS NOT NULL)
    )
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_cash_register_sales_number
    ON cash_register_sales (cash_register_id, sale_number);

CREATE INDEX IF NOT EXISTS idx_cash_register_sales_register_status_issued
    ON cash_register_sales (cash_register_id, status, issued_at DESC);

CREATE TABLE IF NOT EXISTS cash_register_sale_items (
    id BIGSERIAL PRIMARY KEY,
    cash_register_sale_id BIGINT NOT NULL REFERENCES cash_register_sales(id) ON DELETE CASCADE,
    sale_item_id BIGINT NOT NULL UNIQUE REFERENCES sale_items(id) ON DELETE RESTRICT,
    line_no INT NOT NULL,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
    item_type VARCHAR(20) NOT NULL,
    description_snapshot VARCHAR(200) NOT NULL,
    unit_snapshot VARCHAR(10) NOT NULL,
    quantity NUMERIC(12,3) NOT NULL,
    unit_price NUMERIC(12,2) NOT NULL,
    discount_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    surcharge_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    total NUMERIC(12,2) NOT NULL,
    exclude_from_fiscal BOOLEAN NOT NULL DEFAULT FALSE,
    exclude_reason VARCHAR(200),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ck_cash_register_sale_items_type CHECK (item_type IN ('PRODUCT','SERVICE')),
    CONSTRAINT ck_cash_register_sale_items_qty CHECK (quantity > 0),
    CONSTRAINT ck_cash_register_sale_items_values CHECK (
        unit_price >= 0
        AND discount_amount >= 0
        AND surcharge_amount >= 0
        AND total >= 0
        AND total = ((quantity * unit_price) - discount_amount + surcharge_amount)
    ),
    CONSTRAINT ck_cash_register_sale_items_fiscal_exclusion CHECK (
        exclude_from_fiscal = FALSE OR (exclude_reason IS NOT NULL AND length(trim(exclude_reason)) > 0)
    )
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_cash_register_sale_items_line
    ON cash_register_sale_items (cash_register_sale_id, line_no);

CREATE INDEX IF NOT EXISTS idx_cash_register_sale_items_sale
    ON cash_register_sale_items (cash_register_sale_id);

CREATE TABLE IF NOT EXISTS cash_register_item_taxes (
    id BIGSERIAL PRIMARY KEY,
    cash_register_sale_id BIGINT NOT NULL REFERENCES cash_register_sales(id) ON DELETE CASCADE,
    cash_register_sale_item_id BIGINT NOT NULL UNIQUE REFERENCES cash_register_sale_items(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
    tax_profile_id BIGINT REFERENCES tax_profiles(id) ON DELETE SET NULL,
    ncm VARCHAR(8),
    cest VARCHAR(7),
    service_list_code VARCHAR(20),
    cfop VARCHAR(4),
    cbenef VARCHAR(20),
    csosn VARCHAR(3),
    cst_icms VARCHAR(3),
    cst_pis VARCHAR(3),
    cst_cofins VARCHAR(3),
    cst_ipi VARCHAR(3),
    base_icms NUMERIC(12,2) NOT NULL DEFAULT 0,
    rate_icms NUMERIC(8,4) NOT NULL DEFAULT 0,
    amount_icms NUMERIC(12,2) NOT NULL DEFAULT 0,
    base_icms_st NUMERIC(12,2) NOT NULL DEFAULT 0,
    rate_mva NUMERIC(8,4) NOT NULL DEFAULT 0,
    rate_icms_st NUMERIC(8,4) NOT NULL DEFAULT 0,
    amount_icms_st NUMERIC(12,2) NOT NULL DEFAULT 0,
    base_ipi NUMERIC(12,2) NOT NULL DEFAULT 0,
    rate_ipi NUMERIC(8,4) NOT NULL DEFAULT 0,
    amount_ipi NUMERIC(12,2) NOT NULL DEFAULT 0,
    base_pis NUMERIC(12,2) NOT NULL DEFAULT 0,
    rate_pis NUMERIC(8,4) NOT NULL DEFAULT 0,
    amount_pis NUMERIC(12,2) NOT NULL DEFAULT 0,
    base_cofins NUMERIC(12,2) NOT NULL DEFAULT 0,
    rate_cofins NUMERIC(8,4) NOT NULL DEFAULT 0,
    amount_cofins NUMERIC(12,2) NOT NULL DEFAULT 0,
    fcp_rate NUMERIC(8,4) NOT NULL DEFAULT 0,
    fcp_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    desoneration_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    freight_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    insurance_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    other_expenses_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    discount_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ck_cash_register_item_taxes_nonnegative CHECK (
        base_icms >= 0 AND amount_icms >= 0
        AND base_icms_st >= 0 AND amount_icms_st >= 0
        AND base_ipi >= 0 AND amount_ipi >= 0
        AND base_pis >= 0 AND amount_pis >= 0
        AND base_cofins >= 0 AND amount_cofins >= 0
        AND fcp_amount >= 0 AND desoneration_amount >= 0
        AND freight_amount >= 0 AND insurance_amount >= 0
        AND other_expenses_amount >= 0 AND discount_amount >= 0
    )
);

CREATE INDEX IF NOT EXISTS idx_cash_register_item_taxes_sale
    ON cash_register_item_taxes (cash_register_sale_id);

CREATE TABLE IF NOT EXISTS cash_register_sale_payments (
    id BIGSERIAL PRIMARY KEY,
    cash_register_sale_id BIGINT NOT NULL REFERENCES cash_register_sales(id) ON DELETE CASCADE,
    sale_payment_id BIGINT UNIQUE REFERENCES sale_payments(id) ON DELETE SET NULL,
    method VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PAID',
    amount NUMERIC(12,2) NOT NULL,
    installments SMALLINT,
    authorization_code VARCHAR(40),
    acquirer_name VARCHAR(60),
    nsu_code VARCHAR(40),
    paid_at TIMESTAMPTZ,
    created_by BIGINT REFERENCES users(id) ON DELETE RESTRICT,
    notes VARCHAR(200),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ck_cash_register_sale_payments_method CHECK (method IN ('PIX','CARD','CASH','TRANSFER','STORE_CREDIT','OTHER')),
    CONSTRAINT ck_cash_register_sale_payments_status CHECK (status IN ('PENDING','PAID','CANCELED','REFUNDED')),
    CONSTRAINT ck_cash_register_sale_payments_amount CHECK (amount > 0),
    CONSTRAINT ck_cash_register_sale_payments_installments CHECK (installments IS NULL OR installments >= 1)
);

CREATE INDEX IF NOT EXISTS idx_cash_register_sale_payments_sale
    ON cash_register_sale_payments (cash_register_sale_id, created_at DESC);

CREATE TABLE IF NOT EXISTS cash_register_occurrences (
    id BIGSERIAL PRIMARY KEY,
    cash_register_id BIGINT NOT NULL REFERENCES cash_registers(id) ON DELETE CASCADE,
    event_type VARCHAR(30) NOT NULL,
    amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    description VARCHAR(200),
    performed_by BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    approved_by BIGINT REFERENCES users(id) ON DELETE RESTRICT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ck_cash_register_occurrences_type CHECK (
        event_type IN ('OPENING','CLOSING','SUPPLY','WITHDRAWAL','ADJUSTMENT','PAYMENT_REVERSAL')
    ),
    CONSTRAINT ck_cash_register_occurrences_amount_nonnegative CHECK (amount >= 0)
);

CREATE INDEX IF NOT EXISTS idx_cash_register_occurrences_register_created
    ON cash_register_occurrences (cash_register_id, created_at DESC);
