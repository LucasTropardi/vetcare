-- Garante um emitente padrão em DEV e marca ao menos uma empresa como matriz.

WITH ins AS (
    INSERT INTO company (
        legal_name,
        trade_name,
        cnpj,
        phone,
        email,
        created_at,
        updated_at
    )
    VALUES (
        'Petshop do Primo LTDA',
        'PetCare Penapolis',
        '12345678000199',
        '+55 18 99999-9999',
        'contato@petcare-penapolis.local',
        now(),
        now()
    )
    ON CONFLICT (cnpj) DO UPDATE SET
        legal_name = EXCLUDED.legal_name,
        trade_name = EXCLUDED.trade_name,
        phone = EXCLUDED.phone,
        email = EXCLUDED.email,
        updated_at = now()
    RETURNING id
)
INSERT INTO company_address (
    company_id,
    zip_code,
    street,
    number,
    complement,
    neighborhood,
    city_name,
    city_ibge,
    state_uf,
    country,
    created_at,
    updated_at
)
SELECT
    id,
    '16300000',
    'Rua Exemplo',
    '123',
    NULL,
    'Centro',
    'Penapolis',
    '0000000',
    'SP',
    'BR',
    now(),
    now()
FROM ins
ON CONFLICT (company_id) DO UPDATE SET
    zip_code = EXCLUDED.zip_code,
    street = EXCLUDED.street,
    number = EXCLUDED.number,
    complement = EXCLUDED.complement,
    neighborhood = EXCLUDED.neighborhood,
    city_name = EXCLUDED.city_name,
    city_ibge = EXCLUDED.city_ibge,
    state_uf = EXCLUDED.state_uf,
    country = EXCLUDED.country,
    updated_at = now();

WITH c AS (
    SELECT id
    FROM company
    WHERE cnpj = '12345678000199'
    LIMIT 1
)
INSERT INTO company_fiscal_config (
    company_id,
    ie,
    ie_indicator,
    crt,
    created_at,
    updated_at
)
SELECT
    c.id,
    NULL,
    'NON_CONTRIBUTOR',
    'SIMPLES_NACIONAL',
    now(),
    now()
FROM c
ON CONFLICT (company_id) DO UPDATE SET
    ie = EXCLUDED.ie,
    ie_indicator = EXCLUDED.ie_indicator,
    crt = EXCLUDED.crt,
    updated_at = now();

UPDATE company
SET is_headquarter = TRUE,
    updated_at = now()
WHERE id = (
    SELECT id
    FROM company
    ORDER BY is_headquarter DESC, id ASC
    LIMIT 1
);

UPDATE company
SET is_headquarter = FALSE,
    updated_at = now()
WHERE id <> (
    SELECT id
    FROM company
    ORDER BY is_headquarter DESC, id ASC
    LIMIT 1
)
AND is_headquarter = TRUE
AND (SELECT COUNT(*) FROM company) = 1;
