CREATE TABLE fiscal_events (
    id BIGSERIAL PRIMARY KEY,
    fiscal_document_id BIGINT NOT NULL,
    event_type VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    request_xml TEXT,
    response_xml TEXT,
    sefaz_message TEXT,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_fiscal_events_document
       FOREIGN KEY (fiscal_document_id)
           REFERENCES fiscal_documents (id)
           ON DELETE CASCADE
);
