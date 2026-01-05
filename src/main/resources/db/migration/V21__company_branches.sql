ALTER TABLE company
  ADD COLUMN IF NOT EXISTS parent_company_id BIGINT REFERENCES company(id);

ALTER TABLE company
  ADD COLUMN IF NOT EXISTS is_headquarter BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX IF NOT EXISTS idx_company_parent ON company(parent_company_id);
