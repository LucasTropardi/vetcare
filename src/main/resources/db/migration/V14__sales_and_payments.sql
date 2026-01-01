CREATE TABLE IF NOT EXISTS sales (
    id                    BIGSERIAL PRIMARY KEY,
    company_id            BIGINT NOT NULL REFERENCES company(id) ON DELETE RESTRICT,
    tutor_id              BIGINT REFERENCES tutors(id) ON DELETE RESTRICT,
    customer_company_id   BIGINT REFERENCES customer_companies(id) ON DELETE RESTRICT,
    appointment_id        BIGINT REFERENCES appointments(id) ON DELETE RESTRICT,
    status                VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    subtotal              NUMERIC(12,2) NOT NULL DEFAULT 0,
    discount              NUMERIC(12,2) NOT NULL DEFAULT 0,
    total                 NUMERIC(12,2) NOT NULL DEFAULT 0,
    notes                 VARCHAR(300),
    created_by            BIGINT REFERENCES users(id) ON DELETE RESTRICT,
    confirmed_by          BIGINT REFERENCES users(id) ON DELETE RESTRICT,
    canceled_by           BIGINT REFERENCES users(id) ON DELETE RESTRICT,
    confirmed_at          TIMESTAMPTZ,
    canceled_at           TIMESTAMPTZ,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ck_sales_status CHECK (status IN ('DRAFT','CONFIRMED','CANCELED')),
    CONSTRAINT ck_sales_amounts CHECK (subtotal >= 0 AND discount >= 0 AND total >= 0),
    CONSTRAINT ck_sales_total_formula CHECK (total = (subtotal - discount)),
    CONSTRAINT ck_sales_recipient_oneof CHECK (
        (tutor_id IS NOT NULL AND customer_company_id IS NULL)
        OR (tutor_id IS NULL AND customer_company_id IS NOT NULL)
        OR (tutor_id IS NULL AND customer_company_id IS NULL)
    )
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_sales_appointment_id
    ON sales (appointment_id)
    WHERE appointment_id IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_sales_company_created_at ON sales (company_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_sales_status_created_at ON sales (status, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_sales_tutor_id ON sales (tutor_id);
CREATE INDEX IF NOT EXISTS idx_sales_customer_company_id ON sales (customer_company_id);

CREATE TABLE IF NOT EXISTS sale_items (
    id                 BIGSERIAL PRIMARY KEY,
    sale_id            BIGINT NOT NULL REFERENCES sales(id) ON DELETE CASCADE,
    product_id         BIGINT NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
    item_type          VARCHAR(20) NOT NULL,
    description_snapshot VARCHAR(200) NOT NULL,
    unit_snapshot        VARCHAR(10)  NOT NULL,
    quantity           NUMERIC(12,3) NOT NULL,
    unit_price         NUMERIC(12,2) NOT NULL,
    total              NUMERIC(12,2) NOT NULL,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ck_sale_items_item_type CHECK (item_type IN ('PRODUCT','SERVICE')),
    CONSTRAINT ck_sale_items_qty CHECK (quantity > 0),
    CONSTRAINT ck_sale_items_prices CHECK (unit_price >= 0 AND total >= 0),
    CONSTRAINT ck_sale_items_total_formula CHECK (total = (quantity * unit_price))
);

CREATE INDEX IF NOT EXISTS idx_sale_items_sale_id ON sale_items (sale_id);
CREATE INDEX IF NOT EXISTS idx_sale_items_product_id ON sale_items (product_id);

CREATE TABLE IF NOT EXISTS sale_payments (
    id          BIGSERIAL PRIMARY KEY,
    sale_id      BIGINT NOT NULL REFERENCES sales(id) ON DELETE CASCADE,
    method      VARCHAR(10) NOT NULL,
    status      VARCHAR(10) NOT NULL DEFAULT 'PAID',
    amount      NUMERIC(12,2) NOT NULL,
    paid_at     TIMESTAMPTZ,
    created_by  BIGINT REFERENCES users(id) ON DELETE RESTRICT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ck_sale_payments_method CHECK (method IN ('PIX','CARD','CASH','OTHER')),
    CONSTRAINT ck_sale_payments_status CHECK (status IN ('PENDING','PAID','CANCELED')),
    CONSTRAINT ck_sale_payments_amount CHECK (amount > 0)
);

CREATE INDEX IF NOT EXISTS idx_sale_payments_sale_id ON sale_payments (sale_id);
CREATE INDEX IF NOT EXISTS idx_sale_payments_created_at ON sale_payments (created_at DESC);
