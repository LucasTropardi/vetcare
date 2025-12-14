-- seed pet products (dev)

-- Produtos
INSERT INTO products (sku, name, item_type, category, unit, active, sale_price, cost_price, min_stock, created_at, updated_at)
VALUES
    ('PET-FOOD-DOG-15KG', 'Ração Premium Cães Adultos 15kg', 'PRODUCT', 'FEED', 'UN', true, 189.90, 129.90, 2.000, now(), now()),
    ('PET-FOOD-CAT-10KG', 'Ração Premium Gatos Adultos 10kg', 'PRODUCT', 'FEED', 'UN', true, 164.90, 112.90, 2.000, now(), now()),
    ('PET-SHAMP-DOG-500', 'Shampoo Neutro para Cães 500ml', 'PRODUCT', 'SUPPLY', 'UN', true, 34.90, 18.50, 5.000, now(), now()),
    ('PET-ANTI-FLEA-XL', 'Antipulgas e Carrapatos (Cães 20–40kg)', 'PRODUCT', 'MEDICINE', 'UN', true, 89.90, 52.00, 3.000, now(), now()),
    ('PET-DEWORM-CAT', 'Vermífugo Gatos (comprimidos)', 'PRODUCT', 'MEDICINE', 'UN', true, 39.90, 21.00, 5.000, now(), now()),
    ('PET-OTIC-DROPS', 'Solução Otológica 20ml', 'PRODUCT', 'MEDICINE', 'UN', true, 49.90, 28.00, 4.000, now(), now()),
    ('PET-THERMOMETER', 'Termômetro Digital', 'PRODUCT', 'SUPPLY', 'UN', true, 29.90, 14.90, 3.000, now(), now()),
    ('PET-SYRINGE-5ML', 'Seringa 5ml (unidade)', 'PRODUCT', 'SUPPLY', 'UN', true, 2.50, 0.80, 50.000, now(), now()),
    ('PET-GAUZE', 'Gaze Estéril (pacote)', 'PRODUCT', 'SUPPLY', 'UN', true, 9.90, 4.50, 20.000, now(), now()),
    ('PET-COLLAR-ELIZ-M', 'Colar Elizabetano (M)', 'PRODUCT', 'SUPPLY', 'UN', true, 24.90, 11.50, 6.000, now(), now())
    ON CONFLICT (sku) DO NOTHING;

-- Fiscal (1–1 com products)
INSERT INTO product_fiscal (product_id, ncm, cest, origin, gtin_ean, gtin_ean_trib, u_trib, trib_factor, cbenef, service_list_code, created_at, updated_at)
SELECT p.id,
       v.ncm,
       v.cest,
       '0'::varchar,
    NULL, NULL, NULL, NULL,
       NULL,
       NULL,
       now(), now()
FROM products p
         JOIN (VALUES
                   ('PET-FOOD-DOG-15KG', '23099090', NULL),
                   ('PET-FOOD-CAT-10KG', '23099090', NULL),
                   ('PET-SHAMP-DOG-500', '33051000', NULL),
                   ('PET-ANTI-FLEA-XL',  '30049099', NULL),
                   ('PET-DEWORM-CAT',    '30049099', NULL),
                   ('PET-OTIC-DROPS',    '30049099', NULL),
                   ('PET-THERMOMETER',   '90251990', NULL),
                   ('PET-SYRINGE-5ML',   '90183119', NULL),
                   ('PET-GAUZE',         '30059090', NULL),
                   ('PET-COLLAR-ELIZ-M', '39269090', NULL)
) AS v(sku, ncm, cest)
              ON p.sku = v.sku
    ON CONFLICT (product_id) DO NOTHING;
