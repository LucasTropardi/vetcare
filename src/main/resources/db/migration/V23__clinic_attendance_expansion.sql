ALTER TABLE medical_records
    ADD COLUMN IF NOT EXISTS attended_by_user_id BIGINT REFERENCES users(id) ON DELETE RESTRICT,
    ADD COLUMN IF NOT EXISTS weight_kg NUMERIC(8,3),
    ADD COLUMN IF NOT EXISTS temperature_c NUMERIC(4,1),
    ADD COLUMN IF NOT EXISTS heart_rate_bpm INTEGER,
    ADD COLUMN IF NOT EXISTS respiratory_rate_rpm INTEGER,
    ADD COLUMN IF NOT EXISTS initial_assessment TEXT,
    ADD COLUMN IF NOT EXISTS diagnosis_summary TEXT,
    ADD COLUMN IF NOT EXISTS treatment_plan TEXT,
    ADD COLUMN IF NOT EXISTS used_medications TEXT,
    ADD COLUMN IF NOT EXISTS hospitalization_indicated BOOLEAN,
    ADD COLUMN IF NOT EXISTS hospitalization_notes TEXT,
    ADD COLUMN IF NOT EXISTS discharge_instructions TEXT,
    ADD COLUMN IF NOT EXISTS follow_up_at TIMESTAMPTZ;

CREATE TABLE IF NOT EXISTS appointment_petshop_records (
    id BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT NOT NULL UNIQUE REFERENCES appointments(id) ON DELETE CASCADE,
    attended_by_user_id BIGINT REFERENCES users(id) ON DELETE RESTRICT,
    service_report TEXT,
    products_used TEXT,
    checkin_notes TEXT,
    checkout_notes TEXT,
    started_at TIMESTAMPTZ,
    finished_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ck_petshop_record_time CHECK (
        finished_at IS NULL OR started_at IS NULL OR finished_at >= started_at
    )
);

CREATE INDEX IF NOT EXISTS idx_petshop_record_appointment ON appointment_petshop_records(appointment_id);

CREATE TABLE IF NOT EXISTS prescriptions (
    id BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT NOT NULL REFERENCES appointments(id) ON DELETE CASCADE,
    veterinarian_user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    title VARCHAR(140),
    guidance TEXT,
    valid_until DATE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_prescriptions_appointment ON prescriptions(appointment_id, created_at DESC);

CREATE TABLE IF NOT EXISTS prescription_items (
    id BIGSERIAL PRIMARY KEY,
    prescription_id BIGINT NOT NULL REFERENCES prescriptions(id) ON DELETE CASCADE,
    medication_name VARCHAR(180) NOT NULL,
    dosage VARCHAR(140),
    frequency VARCHAR(140),
    duration VARCHAR(140),
    route VARCHAR(80),
    notes TEXT
);

CREATE INDEX IF NOT EXISTS idx_prescription_items_prescription ON prescription_items(prescription_id);
