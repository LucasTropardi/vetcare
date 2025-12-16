CREATE TABLE appointments (
                              id                   BIGSERIAL PRIMARY KEY,
                              pet_id               BIGINT NOT NULL REFERENCES pets(id) ON DELETE RESTRICT,
                              veterinarian_user_id BIGINT REFERENCES users(id) ON DELETE RESTRICT,
                              status               VARCHAR(20) NOT NULL,
                              opened_at            TIMESTAMPTZ NOT NULL DEFAULT now(),
                              finished_at          TIMESTAMPTZ,
                              canceled_at          TIMESTAMPTZ,
                              cancel_reason        VARCHAR(300),
                              created_by           BIGINT REFERENCES users(id) ON DELETE RESTRICT,
                              finished_by          BIGINT REFERENCES users(id) ON DELETE RESTRICT,
                              canceled_by          BIGINT REFERENCES users(id) ON DELETE RESTRICT,
                              created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
                              updated_at           TIMESTAMPTZ NOT NULL DEFAULT now(),

                              CONSTRAINT ck_appointments_status CHECK (status IN ('OPEN','FINISHED','CANCELED')),
                              CONSTRAINT ck_appointments_finish_fields CHECK (
                                  (status <> 'FINISHED') OR (finished_at IS NOT NULL AND finished_by IS NOT NULL)
                                  ),
                              CONSTRAINT ck_appointments_cancel_fields CHECK (
                                  (status <> 'CANCELED') OR (
                                      canceled_at IS NOT NULL AND canceled_by IS NOT NULL
                                          AND cancel_reason IS NOT NULL AND length(trim(cancel_reason)) > 0
                                      )
                                  )
);

CREATE INDEX idx_appointments_pet_status ON appointments (pet_id, status);
CREATE INDEX idx_appointments_vet_status ON appointments (veterinarian_user_id, status);
CREATE INDEX idx_appointments_opened_at ON appointments (opened_at DESC);

-- no máximo 1 atendimento OPEN por pet
CREATE UNIQUE INDEX uk_appointments_one_open_per_pet
    ON appointments (pet_id)
    WHERE status = 'OPEN';


CREATE TABLE medical_records (
                                 id              BIGSERIAL PRIMARY KEY,
                                 appointment_id  BIGINT NOT NULL REFERENCES appointments(id) ON DELETE CASCADE,
                                 chief_complaint TEXT,
                                 clinical_notes  TEXT,
                                 created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
                                 updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
                                 CONSTRAINT uk_medical_records_appointment UNIQUE (appointment_id)
);

CREATE INDEX idx_medrec_appointment_id ON medical_records (appointment_id);


CREATE TABLE medical_record_diagnoses (
                                          id                BIGSERIAL PRIMARY KEY,
                                          medical_record_id BIGINT NOT NULL REFERENCES medical_records(id) ON DELETE CASCADE,
                                          code              VARCHAR(30),
                                          description       VARCHAR(300) NOT NULL,
                                          is_primary        BOOLEAN NOT NULL DEFAULT FALSE,
                                          created_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_mrd_medrec_id ON medical_record_diagnoses (medical_record_id);


CREATE TABLE medical_record_procedures (
                                           id                BIGSERIAL PRIMARY KEY,
                                           medical_record_id BIGINT NOT NULL REFERENCES medical_records(id) ON DELETE CASCADE,
                                           description       VARCHAR(300) NOT NULL,
                                           notes             TEXT,
                                           performed_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
                                           created_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_mrp_medrec_id ON medical_record_procedures (medical_record_id);
CREATE INDEX idx_mrp_performed_at ON medical_record_procedures (performed_at DESC);
