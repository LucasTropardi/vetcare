ALTER TABLE users
    ADD COLUMN IF NOT EXISTS professional_license VARCHAR(80),
    ADD COLUMN IF NOT EXISTS signature_image_base64 TEXT,
    ADD COLUMN IF NOT EXISTS signature_image_content_type VARCHAR(80),
    ADD COLUMN IF NOT EXISTS signature_updated_at TIMESTAMPTZ;

CREATE INDEX IF NOT EXISTS idx_users_professional_license ON users(professional_license);
