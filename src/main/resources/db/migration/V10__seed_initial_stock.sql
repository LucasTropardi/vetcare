-- seed initial stock (dev)

WITH seeded AS (
    SELECT id, sku, cost_price
    FROM products
    WHERE sku IN (
                  'PET-FOOD-DOG-15KG',
                  'PET-FOOD-CAT-10KG',
                  'PET-SHAMP-DOG-500',
                  'PET-ANTI-FLEA-XL',
                  'PET-DEWORM-CAT',
                  'PET-OTIC-DROPS',
                  'PET-THERMOMETER',
                  'PET-SYRINGE-5ML',
                  'PET-GAUZE',
                  'PET-COLLAR-ELIZ-M'
        )
),
     qty AS (
         SELECT
             s.id AS product_id,
             s.cost_price AS unit_cost,
             CASE
                 WHEN s.sku IN ('PET-SYRINGE-5ML') THEN 200.000
                 WHEN s.sku IN ('PET-GAUZE') THEN 80.000
                 ELSE 10.000
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
    'Seed estoque inicial (dev)',
    'IMPORT',
    NULL,
    (SELECT user_id FROM admin),
    now()
FROM qty q;

WITH seeded AS (
    SELECT id, sku, cost_price
    FROM products
    WHERE sku IN (
                  'PET-FOOD-DOG-15KG',
                  'PET-FOOD-CAT-10KG',
                  'PET-SHAMP-DOG-500',
                  'PET-ANTI-FLEA-XL',
                  'PET-DEWORM-CAT',
                  'PET-OTIC-DROPS',
                  'PET-THERMOMETER',
                  'PET-SYRINGE-5ML',
                  'PET-GAUZE',
                  'PET-COLLAR-ELIZ-M'
        )
),
     qty AS (
         SELECT
             s.id AS product_id,
             s.cost_price AS unit_cost,
             CASE
                 WHEN s.sku IN ('PET-SYRINGE-5ML') THEN 200.000
                 WHEN s.sku IN ('PET-GAUZE') THEN 80.000
                 ELSE 10.000
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
