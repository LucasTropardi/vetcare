CREATE TABLE products (
                          id            BIGSERIAL PRIMARY KEY,
                          sku           VARCHAR(60) NOT NULL,
                          name          VARCHAR(200) NOT NULL,
                          item_type     VARCHAR(20) NOT NULL,
                          category      VARCHAR(40) NOT NULL,
                          unit          VARCHAR(10) NOT NULL DEFAULT 'UN',
                          active        BOOLEAN NOT NULL DEFAULT TRUE,
                          sale_price    NUMERIC(12,2) NOT NULL DEFAULT 0,
                          cost_price    NUMERIC(12,2) NOT NULL DEFAULT 0,
                          min_stock     NUMERIC(12,3) NOT NULL DEFAULT 0,
                          created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
                          updated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
                          CONSTRAINT uk_products_sku UNIQUE (sku),
                          CONSTRAINT ck_products_item_type CHECK (item_type IN ('PRODUCT', 'SERVICE')),
                          CONSTRAINT ck_products_category CHECK (category IN ('MEDICINE', 'SUPPLY', 'FEED', 'OTHER')),
                          CONSTRAINT ck_products_prices CHECK (sale_price >= 0 AND cost_price >= 0),
                          CONSTRAINT ck_products_stock CHECK (min_stock >= 0)
);

CREATE INDEX idx_products_name_lower ON products (lower(name));
CREATE INDEX idx_products_type_category ON products (item_type, category);

CREATE TABLE product_fiscal (
                                product_id        BIGINT PRIMARY KEY REFERENCES products(id) ON DELETE CASCADE,
                                ncm               VARCHAR(8),
                                cest              VARCHAR(7),
                                origin            VARCHAR(2) NOT NULL DEFAULT '0',
                                gtin_ean          VARCHAR(14),
                                gtin_ean_trib     VARCHAR(14),
                                u_trib            VARCHAR(10),
                                trib_factor       NUMERIC(18,6),
                                cbenef            VARCHAR(20),
                                service_list_code VARCHAR(20),
                                created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
                                updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
                                CONSTRAINT ck_pf_ncm CHECK (ncm IS NULL OR ncm ~ '^[0-9]{8}$'),
                                CONSTRAINT ck_pf_cest CHECK (cest IS NULL OR cest ~ '^[0-9]{7}$'),
                                CONSTRAINT ck_pf_gtin CHECK (gtin_ean IS NULL OR gtin_ean ~ '^[0-9]{8,14}$'),
                                CONSTRAINT ck_pf_gtin_trib CHECK (gtin_ean_trib IS NULL OR gtin_ean_trib ~ '^[0-9]{8,14}$'),
                                CONSTRAINT ck_pf_trib_factor CHECK (trib_factor IS NULL OR trib_factor > 0)
);

CREATE TABLE tax_profiles (
                              id              BIGSERIAL PRIMARY KEY,
                              name            VARCHAR(120) NOT NULL,
                              operation       VARCHAR(30)  NOT NULL DEFAULT 'SALE',
                              cfop            VARCHAR(4),
                              icms_code       VARCHAR(10),
                              icms_rate       NUMERIC(6,4),
                              pis_code        VARCHAR(10),
                              pis_rate        NUMERIC(6,4),
                              cofins_code     VARCHAR(10),
                              cofins_rate     NUMERIC(6,4),
                              ipi_code        VARCHAR(10),
                              ipi_rate        NUMERIC(6,4),
                              created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
                              updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX uk_tax_profiles_name ON tax_profiles (lower(name));

CREATE TABLE product_tax_profiles (
                                      product_id     BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
                                      tax_profile_id BIGINT NOT NULL REFERENCES tax_profiles(id) ON DELETE RESTRICT,
                                      is_default     BOOLEAN NOT NULL DEFAULT TRUE,
                                      PRIMARY KEY (product_id, tax_profile_id)
);

CREATE INDEX idx_ptp_default ON product_tax_profiles (product_id, is_default);

CREATE TABLE stock_movements (
                                 id             BIGSERIAL PRIMARY KEY,
                                 product_id     BIGINT NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
                                 movement_type  VARCHAR(40) NOT NULL,
                                 quantity       NUMERIC(12,3) NOT NULL,
                                 unit_cost      NUMERIC(12,2),
                                 notes          VARCHAR(300),
                                 reference_type VARCHAR(40),
                                 reference_id   BIGINT,
                                 created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
                                 CONSTRAINT ck_sm_type CHECK (movement_type IN ('ENTRY_PURCHASE', 'EXIT_SALE', 'EXIT_VISIT_CONSUMPTION', 'ADJUSTMENT')),
                                 CONSTRAINT ck_sm_qty CHECK (quantity <> 0),
                                 CONSTRAINT ck_sm_cost CHECK (unit_cost IS NULL OR unit_cost >= 0)
);

CREATE INDEX idx_sm_product_created ON stock_movements (product_id, created_at DESC);
