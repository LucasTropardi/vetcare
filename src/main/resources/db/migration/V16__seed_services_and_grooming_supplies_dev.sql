INSERT INTO products (sku, name, item_type, category, unit, active, sale_price, cost_price, min_stock, created_at, updated_at)
VALUES
    ('SRV-CONSULTA-VET',        'Consulta Veterinária',              'SERVICE', 'OTHER', 'UN', true, 120.00, 0.00, 0.000, now(), now()),
    ('SRV-RETORNO-VET',         'Retorno / Reavaliação',             'SERVICE', 'OTHER', 'UN', true,  60.00, 0.00, 0.000, now(), now()),
    ('SRV-BANHO-P',             'Banho (Porte Pequeno)',             'SERVICE', 'OTHER', 'UN', true,  45.00, 0.00, 0.000, now(), now()),
    ('SRV-BANHO-M',             'Banho (Porte Médio)',               'SERVICE', 'OTHER', 'UN', true,  60.00, 0.00, 0.000, now(), now()),
    ('SRV-BANHO-G',             'Banho (Porte Grande)',              'SERVICE', 'OTHER', 'UN', true,  80.00, 0.00, 0.000, now(), now()),
    ('SRV-TOSA-HIGIENICA',      'Tosa Higiênica',                    'SERVICE', 'OTHER', 'UN', true,  35.00, 0.00, 0.000, now(), now()),
    ('SRV-TOSA-COMPLETA',       'Tosa Completa',                     'SERVICE', 'OTHER', 'UN', true,  70.00, 0.00, 0.000, now(), now()),
    ('SRV-BANHO-TOSA',          'Combo Banho + Tosa',                'SERVICE', 'OTHER', 'UN', true, 110.00, 0.00, 0.000, now(), now()),
    ('SRV-VACINACAO',           'Aplicação de Vacina',               'SERVICE', 'OTHER', 'UN', true,  30.00, 0.00, 0.000, now(), now()),
    ('SRV-CURATIVO',            'Curativo / Limpeza de Feridas',     'SERVICE', 'OTHER', 'UN', true,  40.00, 0.00, 0.000, now(), now()),
    ('SRV-MEDICACAO',           'Aplicação de Medicação',            'SERVICE', 'OTHER', 'UN', true,  25.00, 0.00, 0.000, now(), now()),
    ('SRV-CASTRACAO',           'Castração',                         'SERVICE', 'OTHER', 'UN', true, 350.00, 0.00, 0.000, now(), now()),
    ('SRV-CIRURGIA-SIMPLES',    'Cirurgia Simples',                  'SERVICE', 'OTHER', 'UN', true, 450.00, 0.00, 0.000, now(), now()),
    ('SRV-ANESTESIA',           'Taxa de Anestesia',                 'SERVICE', 'OTHER', 'UN', true, 120.00, 0.00, 0.000, now(), now())
    ON CONFLICT (sku) DO NOTHING;

-- Fiscal obrigatório p/ SERVICE: service_list_code preenchido, sem NCM/CEST/GTIN
INSERT INTO product_fiscal (
    product_id, ncm, cest, origin,
    gtin_ean, gtin_ean_trib, u_trib, trib_factor,
    cbenef, service_list_code,
    created_at, updated_at
)
SELECT
    p.id,
    NULL, NULL,
    '0'::varchar,
    NULL, NULL, NULL, NULL,
    NULL,
    v.service_list_code,
    now(), now()
FROM products p
         JOIN (VALUES
                   ('SRV-CONSULTA-VET',      'SRV-001'),
                   ('SRV-RETORNO-VET',       'SRV-002'),
                   ('SRV-BANHO-P',           'SRV-010'),
                   ('SRV-BANHO-M',           'SRV-011'),
                   ('SRV-BANHO-G',           'SRV-012'),
                   ('SRV-TOSA-HIGIENICA',    'SRV-020'),
                   ('SRV-TOSA-COMPLETA',     'SRV-021'),
                   ('SRV-BANHO-TOSA',        'SRV-022'),
                   ('SRV-VACINACAO',         'SRV-030'),
                   ('SRV-CURATIVO',          'SRV-031'),
                   ('SRV-MEDICACAO',         'SRV-032'),
                   ('SRV-CASTRACAO',         'SRV-040'),
                   ('SRV-CIRURGIA-SIMPLES',  'SRV-041'),
                   ('SRV-ANESTESIA',         'SRV-042')
) AS v(sku, service_list_code)
    ON p.sku = v.sku
    ON CONFLICT (product_id) DO NOTHING;

INSERT INTO products (sku, name, item_type, category, unit, active, sale_price, cost_price, min_stock, created_at, updated_at)
VALUES
    ('PET-GROOM-SHAMP-NEUTRO-5L',     'Shampoo Neutro 5L (Banho/Tosa)',        'PRODUCT', 'SUPPLY', 'UN', true, 129.90, 79.90, 1.000, now(), now()),
    ('PET-GROOM-SHAMP-HIPO-5L',       'Shampoo Hipoalergênico 5L',            'PRODUCT', 'SUPPLY', 'UN', true, 159.90, 99.90, 1.000, now(), now()),
    ('PET-GROOM-COND-5L',             'Condicionador 5L',                      'PRODUCT', 'SUPPLY', 'UN', true, 149.90, 92.00, 1.000, now(), now()),
    ('PET-GROOM-HIDRAT-1L',           'Máscara de Hidratação 1L',              'PRODUCT', 'SUPPLY', 'UN', true,  89.90, 55.00, 1.000, now(), now()),
    ('PET-GROOM-PERFUME-120ML',       'Perfume Pet 120ml',                    'PRODUCT', 'SUPPLY', 'UN', true,  39.90, 18.00, 2.000, now(), now()),
    ('PET-GROOM-LIMP-OUVIDO-100ML',   'Limpador de Ouvido 100ml',             'PRODUCT', 'SUPPLY', 'UN', true,  34.90, 16.00, 2.000, now(), now()),
    ('PET-GROOM-COLONIA-500ML',       'Colônia Pós-banho 500ml',              'PRODUCT', 'SUPPLY', 'UN', true,  59.90, 31.00, 1.000, now(), now()),
    ('PET-GROOM-TALCO-200G',          'Talco Pet 200g',                       'PRODUCT', 'SUPPLY', 'UN', true,  24.90, 10.00, 2.000, now(), now()),
    ('PET-GROOM-LENCO-UMED-80',       'Lenço Umedecido (80 un)',              'PRODUCT', 'SUPPLY', 'UN', true,  19.90,  9.50, 3.000, now(), now()),
    ('PET-GROOM-LUVA-DESC-100',       'Luva Descartável (100 un)',            'PRODUCT', 'SUPPLY', 'UN', true,  29.90, 14.90, 2.000, now(), now()),
    ('PET-GROOM-LAMINA-10',           'Lâmina Máquina Tosa #10',              'PRODUCT', 'SUPPLY', 'UN', true,  79.90, 49.90, 1.000, now(), now()),
    ('PET-GROOM-LAMINA-7F',           'Lâmina Máquina Tosa #7F',              'PRODUCT', 'SUPPLY', 'UN', true,  89.90, 54.90, 1.000, now(), now()),
    ('PET-GROOM-TESOURA-RETA',        'Tesoura Reta Inox (Tosa)',             'PRODUCT', 'SUPPLY', 'UN', true,  69.90, 39.90, 1.000, now(), now()),
    ('PET-GROOM-TESOURA-DESBASTE',    'Tesoura Desbaste (Tosa)',              'PRODUCT', 'SUPPLY', 'UN', true,  79.90, 44.90, 1.000, now(), now()),
    ('PET-GROOM-ESCOVA',              'Escova (Banho/Tosa)',                  'PRODUCT', 'SUPPLY', 'UN', true,  24.90, 11.90, 2.000, now(), now()),
    ('PET-GROOM-PENTE-METAL',         'Pente de Metal (Tosa)',                'PRODUCT', 'SUPPLY', 'UN', true,  19.90,  9.90, 2.000, now(), now()),
    ('PET-GROOM-CORTA-UNHA',          'Corta-unhas Pet',                      'PRODUCT', 'SUPPLY', 'UN', true,  29.90, 15.00, 2.000, now(), now()),
    ('PET-GROOM-ALGODAO-500G',        'Algodão 500g',                         'PRODUCT', 'SUPPLY', 'UN', true,  19.90,  8.50, 2.000, now(), now()),
    ('PET-GROOM-TOALHA-MICRO',        'Toalha Microfibra (Banho)',            'PRODUCT', 'SUPPLY', 'UN', true,  29.90, 14.00, 2.000, now(), now()),
    ('PET-GROOM-SECADOR',             'Secador Pet (Uso interno)',            'PRODUCT', 'SUPPLY', 'UN', true, 399.90, 260.00, 0.000, now(), now())
    ON CONFLICT (sku) DO NOTHING;

INSERT INTO product_fiscal (
    product_id, ncm, cest, origin,
    gtin_ean, gtin_ean_trib, u_trib, trib_factor,
    cbenef, service_list_code,
    created_at, updated_at
)
SELECT
    p.id,
    v.ncm,
    v.cest,
    '0'::varchar,
    NULL, NULL, NULL, NULL,
    NULL,
    NULL,
    now(), now()
FROM products p
         JOIN (VALUES
                   ('PET-GROOM-SHAMP-NEUTRO-5L',   '33051000', NULL),
                   ('PET-GROOM-SHAMP-HIPO-5L',     '33051000', NULL),
                   ('PET-GROOM-COND-5L',           '33059000', NULL),
                   ('PET-GROOM-HIDRAT-1L',         '33059000', NULL),
                   ('PET-GROOM-PERFUME-120ML',     '33030010', NULL),
                   ('PET-GROOM-LIMP-OUVIDO-100ML', '34013000', NULL),
                   ('PET-GROOM-COLONIA-500ML',     '33030010', NULL),
                   ('PET-GROOM-TALCO-200G',        '33049100', NULL),
                   ('PET-GROOM-LENCO-UMED-80',     '34011900', NULL),
                   ('PET-GROOM-LUVA-DESC-100',     '40151900', NULL),
                   ('PET-GROOM-LAMINA-10',         '82055900', NULL),
                   ('PET-GROOM-LAMINA-7F',         '82055900', NULL),
                   ('PET-GROOM-TESOURA-RETA',      '82130000', NULL),
                   ('PET-GROOM-TESOURA-DESBASTE',  '82130000', NULL),
                   ('PET-GROOM-ESCOVA',            '96032900', NULL),
                   ('PET-GROOM-PENTE-METAL',       '96151900', NULL),
                   ('PET-GROOM-CORTA-UNHA',        '82142000', NULL),
                   ('PET-GROOM-ALGODAO-500G',      '56012190', NULL),
                   ('PET-GROOM-TOALHA-MICRO',      '63026000', NULL),
                   ('PET-GROOM-SECADOR',           '85163100', NULL)
) AS v(sku, ncm, cest)
    ON p.sku = v.sku
    ON CONFLICT (product_id) DO NOTHING;

WITH seeded AS (
    SELECT id, sku, cost_price
    FROM products
    WHERE sku IN (
                  'PET-GROOM-SHAMP-NEUTRO-5L',
                  'PET-GROOM-SHAMP-HIPO-5L',
                  'PET-GROOM-COND-5L',
                  'PET-GROOM-HIDRAT-1L',
                  'PET-GROOM-PERFUME-120ML',
                  'PET-GROOM-LIMP-OUVIDO-100ML',
                  'PET-GROOM-COLONIA-500ML',
                  'PET-GROOM-TALCO-200G',
                  'PET-GROOM-LENCO-UMED-80',
                  'PET-GROOM-LUVA-DESC-100',
                  'PET-GROOM-LAMINA-10',
                  'PET-GROOM-LAMINA-7F',
                  'PET-GROOM-TESOURA-RETA',
                  'PET-GROOM-TESOURA-DESBASTE',
                  'PET-GROOM-ESCOVA',
                  'PET-GROOM-PENTE-METAL',
                  'PET-GROOM-CORTA-UNHA',
                  'PET-GROOM-ALGODAO-500G',
                  'PET-GROOM-TOALHA-MICRO',
                  'PET-GROOM-SECADOR'
        )
),
     qty AS (
         SELECT
             s.id AS product_id,
             s.cost_price AS unit_cost,
             CASE
                 -- consumíveis
                 WHEN s.sku IN ('PET-GROOM-LUVA-DESC-100')   THEN 10.000
                 WHEN s.sku IN ('PET-GROOM-LENCO-UMED-80')   THEN 15.000
                 WHEN s.sku IN ('PET-GROOM-ALGODAO-500G')    THEN 10.000
                 WHEN s.sku IN ('PET-GROOM-PERFUME-120ML')   THEN 12.000
                 WHEN s.sku IN ('PET-GROOM-TALCO-200G')      THEN 12.000
                 WHEN s.sku IN ('PET-GROOM-LIMP-OUVIDO-100ML') THEN 12.000

                 -- líquidos grandes
                 WHEN s.sku IN ('PET-GROOM-SHAMP-NEUTRO-5L')  THEN 3.000
                 WHEN s.sku IN ('PET-GROOM-SHAMP-HIPO-5L')    THEN 2.000
                 WHEN s.sku IN ('PET-GROOM-COND-5L')          THEN 2.000
                 WHEN s.sku IN ('PET-GROOM-HIDRAT-1L')        THEN 3.000
                 WHEN s.sku IN ('PET-GROOM-COLONIA-500ML')    THEN 4.000

                 -- equipamentos/ferramentas
                 WHEN s.sku IN ('PET-GROOM-SECADOR')          THEN 1.000
                 WHEN s.sku IN ('PET-GROOM-LAMINA-10')        THEN 2.000
                 WHEN s.sku IN ('PET-GROOM-LAMINA-7F')        THEN 2.000
                 WHEN s.sku IN ('PET-GROOM-TESOURA-RETA')     THEN 2.000
                 WHEN s.sku IN ('PET-GROOM-TESOURA-DESBASTE') THEN 1.000

                 ELSE 5.000
                 END AS quantity
         FROM seeded s
     ),
     admin AS (
         SELECT id AS user_id
         FROM users
         WHERE email = 'admin@vetcare.local'
    LIMIT 1
)
INSERT INTO stock_movements (
    product_id, movement_type, quantity, unit_cost, notes,
    reference_type, reference_id, created_by, created_at
)
SELECT
    q.product_id,
    'ENTRY_PURCHASE',
    q.quantity,
    q.unit_cost,
    'Seed insumos banho/tosa (dev)',
    'IMPORT',
    NULL,
    (SELECT user_id FROM admin),
    now()
FROM qty q;

WITH seeded AS (
    SELECT id, sku, cost_price
    FROM products
    WHERE sku IN (
                  'PET-GROOM-SHAMP-NEUTRO-5L',
                  'PET-GROOM-SHAMP-HIPO-5L',
                  'PET-GROOM-COND-5L',
                  'PET-GROOM-HIDRAT-1L',
                  'PET-GROOM-PERFUME-120ML',
                  'PET-GROOM-LIMP-OUVIDO-100ML',
                  'PET-GROOM-COLONIA-500ML',
                  'PET-GROOM-TALCO-200G',
                  'PET-GROOM-LENCO-UMED-80',
                  'PET-GROOM-LUVA-DESC-100',
                  'PET-GROOM-LAMINA-10',
                  'PET-GROOM-LAMINA-7F',
                  'PET-GROOM-TESOURA-RETA',
                  'PET-GROOM-TESOURA-DESBASTE',
                  'PET-GROOM-ESCOVA',
                  'PET-GROOM-PENTE-METAL',
                  'PET-GROOM-CORTA-UNHA',
                  'PET-GROOM-ALGODAO-500G',
                  'PET-GROOM-TOALHA-MICRO',
                  'PET-GROOM-SECADOR'
        )
),
     qty AS (
         SELECT
             s.id AS product_id,
             s.cost_price AS unit_cost,
             CASE
                 WHEN s.sku IN ('PET-GROOM-LUVA-DESC-100')   THEN 10.000
                 WHEN s.sku IN ('PET-GROOM-LENCO-UMED-80')   THEN 15.000
                 WHEN s.sku IN ('PET-GROOM-ALGODAO-500G')    THEN 10.000
                 WHEN s.sku IN ('PET-GROOM-PERFUME-120ML')   THEN 12.000
                 WHEN s.sku IN ('PET-GROOM-TALCO-200G')      THEN 12.000
                 WHEN s.sku IN ('PET-GROOM-LIMP-OUVIDO-100ML') THEN 12.000
                 WHEN s.sku IN ('PET-GROOM-SHAMP-NEUTRO-5L')  THEN 3.000
                 WHEN s.sku IN ('PET-GROOM-SHAMP-HIPO-5L')    THEN 2.000
                 WHEN s.sku IN ('PET-GROOM-COND-5L')          THEN 2.000
                 WHEN s.sku IN ('PET-GROOM-HIDRAT-1L')        THEN 3.000
                 WHEN s.sku IN ('PET-GROOM-COLONIA-500ML')    THEN 4.000
                 WHEN s.sku IN ('PET-GROOM-SECADOR')          THEN 1.000
                 WHEN s.sku IN ('PET-GROOM-LAMINA-10')        THEN 2.000
                 WHEN s.sku IN ('PET-GROOM-LAMINA-7F')        THEN 2.000
                 WHEN s.sku IN ('PET-GROOM-TESOURA-RETA')     THEN 2.000
                 WHEN s.sku IN ('PET-GROOM-TESOURA-DESBASTE') THEN 1.000
                 ELSE 5.000
                 END AS quantity
         FROM seeded s
     )
INSERT INTO product_stock_balance (product_id, on_hand, avg_cost, updated_at)
SELECT
    q.product_id,
    q.quantity,
    q.unit_cost,
    now()
FROM qty q
    ON CONFLICT (product_id)
DO UPDATE SET
    on_hand = EXCLUDED.on_hand,
           avg_cost = EXCLUDED.avg_cost,
           updated_at = now();
