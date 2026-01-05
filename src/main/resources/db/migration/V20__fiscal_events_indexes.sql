CREATE INDEX IF NOT EXISTS idx_fiscal_events_document_created_at
    ON fiscal_events (fiscal_document_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_fiscal_events_document_status_created_at
    ON fiscal_events (fiscal_document_id, status, created_at DESC);
