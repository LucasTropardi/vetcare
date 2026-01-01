CREATE TABLE IF NOT EXISTS customer_companies (
    id          BIGSERIAL PRIMARY KEY,
    tutor_id    BIGINT NOT NULL REFERENCES tutors(id) ON DELETE RESTRICT,
    legal_name  VARCHAR(200) NOT NULL,
    trade_name  VARCHAR(200),
    cnpj        VARCHAR(14)  NOT NULL,
    phone       VARCHAR(30),
    email       VARCHAR(160),
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uk_customer_companies_cnpj UNIQUE (cnpj),
    CONSTRAINT ck_customer_companies_cnpj CHECK (cnpj ~ '^[0-9]{14}$')
);

CREATE INDEX IF NOT EXISTS idx_customer_companies_tutor_id ON customer_companies (tutor_id);
CREATE INDEX IF NOT EXISTS idx_customer_companies_legal_name_lower ON customer_companies (lower(legal_name));
CREATE INDEX IF NOT EXISTS idx_customer_companies_trade_name_lower ON customer_companies (lower(trade_name));

CREATE TABLE IF NOT EXISTS customer_company_address (
    customer_company_id BIGINT PRIMARY KEY REFERENCES customer_companies(id) ON DELETE CASCADE,
    zip_code      VARCHAR(8)   NOT NULL,
    street        VARCHAR(160) NOT NULL,
    number        VARCHAR(30),
    complement    VARCHAR(120),
    neighborhood  VARCHAR(120),
    city_name     VARCHAR(120) NOT NULL,
    city_ibge     VARCHAR(7),
    state_uf      VARCHAR(2)   NOT NULL,
    country       VARCHAR(60)  NOT NULL DEFAULT 'BR',
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT ck_customer_company_addr_uf CHECK (state_uf ~ '^[A-Z]{2}$')
);

CREATE TABLE IF NOT EXISTS customer_company_fiscal (
    customer_company_id BIGINT PRIMARY KEY REFERENCES customer_companies(id) ON DELETE CASCADE,
    ie           VARCHAR(20),
    ie_indicator VARCHAR(30) NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ck_customer_company_ie_indicator
    CHECK (ie_indicator IN ('CONTRIBUTOR', 'EXEMPT', 'NON_CONTRIBUTOR'))
);
