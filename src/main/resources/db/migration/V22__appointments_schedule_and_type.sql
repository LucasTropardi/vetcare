ALTER TABLE appointments
    ADD COLUMN IF NOT EXISTS appointment_type VARCHAR(20),
    ADD COLUMN IF NOT EXISTS scheduled_start_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS scheduled_end_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS service_product_id BIGINT REFERENCES products(id) ON DELETE RESTRICT,
    ADD COLUMN IF NOT EXISTS notes VARCHAR(500);

UPDATE appointments
SET appointment_type = 'VET'
WHERE appointment_type IS NULL;

UPDATE appointments
SET scheduled_start_at = COALESCE(opened_at, created_at, now())
WHERE scheduled_start_at IS NULL;

UPDATE appointments
SET scheduled_end_at = GREATEST(
        COALESCE(
            finished_at,
            canceled_at,
            opened_at + INTERVAL '30 minutes',
            created_at + INTERVAL '30 minutes',
            now() + INTERVAL '30 minutes'
        ),
        scheduled_start_at + INTERVAL '1 minute'
    )
WHERE scheduled_end_at IS NULL;

ALTER TABLE appointments
    ALTER COLUMN appointment_type SET NOT NULL,
    ALTER COLUMN appointment_type SET DEFAULT 'VET',
    ALTER COLUMN scheduled_start_at SET NOT NULL,
    ALTER COLUMN scheduled_end_at SET NOT NULL;

DROP INDEX IF EXISTS uk_appointments_one_open_per_pet;

ALTER TABLE appointments
    ADD CONSTRAINT ck_appointments_type CHECK (appointment_type IN ('VET', 'PETSHOP')),
    ADD CONSTRAINT ck_appointments_schedule CHECK (scheduled_end_at > scheduled_start_at),
    ADD CONSTRAINT ck_appointments_petshop_service CHECK (
        appointment_type <> 'PETSHOP' OR service_product_id IS NOT NULL
    );

CREATE INDEX IF NOT EXISTS idx_appointments_type_status_start
    ON appointments (appointment_type, status, scheduled_start_at DESC);

CREATE INDEX IF NOT EXISTS idx_appointments_pet_start
    ON appointments (pet_id, scheduled_start_at DESC);

CREATE INDEX IF NOT EXISTS idx_appointments_service_product
    ON appointments (service_product_id);
