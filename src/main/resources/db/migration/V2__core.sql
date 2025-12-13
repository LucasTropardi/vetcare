DROP TABLE IF EXISTS app_bootstrap;

CREATE TABLE users (
                       id            BIGSERIAL PRIMARY KEY,
                       name          VARCHAR(120) NOT NULL,
                       email         VARCHAR(160) NOT NULL,
                       password_hash VARCHAR(120) NOT NULL,
                       role          VARCHAR(30)  NOT NULL,
                       active        BOOLEAN      NOT NULL DEFAULT TRUE,
                       created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
                       updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
                       CONSTRAINT uk_users_email UNIQUE (email),
                       CONSTRAINT ck_users_role CHECK (role IN ('ADMIN', 'RECEPTION', 'VET'))
);

CREATE TABLE company (
                         id           BIGSERIAL PRIMARY KEY,
                         legal_name   VARCHAR(200) NOT NULL,
                         trade_name   VARCHAR(200),
                         cnpj         VARCHAR(14)  NOT NULL,
                         phone        VARCHAR(30),
                         email        VARCHAR(160),
                         created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
                         updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
                         CONSTRAINT uk_company_cnpj UNIQUE (cnpj)
);

CREATE TABLE company_address (
                                 company_id    BIGINT PRIMARY KEY REFERENCES company(id) ON DELETE CASCADE,
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
                                 CONSTRAINT ck_company_addr_uf CHECK (state_uf ~ '^[A-Z]{2}$')
    );

CREATE TABLE company_fiscal_config (
                                       company_id   BIGINT PRIMARY KEY REFERENCES company(id) ON DELETE CASCADE,
                                       ie           VARCHAR(20),
                                       ie_indicator VARCHAR(30) NOT NULL,
                                       crt          VARCHAR(30) NOT NULL,
                                       created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
                                       updated_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
                                       CONSTRAINT ck_company_ie_indicator CHECK (ie_indicator IN ('CONTRIBUTOR', 'EXEMPT', 'NON_CONTRIBUTOR')),
                                       CONSTRAINT ck_company_crt CHECK (crt IN ('SIMPLES_NACIONAL', 'REGIME_NORMAL'))
);

CREATE TABLE tutors (
                        id          BIGSERIAL PRIMARY KEY,
                        name        VARCHAR(160) NOT NULL,
                        document    VARCHAR(14),
                        phone       VARCHAR(30),
                        email       VARCHAR(160),
                        active      BOOLEAN      NOT NULL DEFAULT TRUE,
                        created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
                        updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_tutors_name_lower ON tutors (lower(name));
CREATE INDEX idx_tutors_document ON tutors (document);

CREATE TABLE tutor_address (
                               tutor_id      BIGINT PRIMARY KEY REFERENCES tutors(id) ON DELETE CASCADE,
                               zip_code      VARCHAR(8),
                               street        VARCHAR(160),
                               number        VARCHAR(30),
                               complement    VARCHAR(120),
                               neighborhood  VARCHAR(120),
                               city_name     VARCHAR(120),
                               city_ibge     VARCHAR(7),
                               state_uf      VARCHAR(2),
                               country       VARCHAR(60) DEFAULT 'BR',
                               created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
                               updated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
                               CONSTRAINT ck_tutor_addr_uf CHECK (state_uf IS NULL OR state_uf ~ '^[A-Z]{2}$')
    );

CREATE TABLE pets (
                      id          BIGSERIAL PRIMARY KEY,
                      tutor_id    BIGINT       NOT NULL REFERENCES tutors(id) ON DELETE RESTRICT,
                      name        VARCHAR(120) NOT NULL,
                      species     VARCHAR(30)  NOT NULL,
                      breed       VARCHAR(120),
                      sex         VARCHAR(10),
                      birth_date  DATE,
                      weight_kg   NUMERIC(6,2),
                      notes       TEXT,
                      active      BOOLEAN      NOT NULL DEFAULT TRUE,
                      created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
                      updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
                      CONSTRAINT ck_pets_species CHECK (species IN ('DOG', 'CAT', 'OTHER')),
                      CONSTRAINT ck_pets_sex CHECK (sex IS NULL OR sex IN ('MALE', 'FEMALE'))
);

CREATE INDEX idx_pets_tutor_id ON pets (tutor_id);
CREATE INDEX idx_pets_name_lower ON pets (lower(name));
