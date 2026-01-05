CREATE TABLE fiscal_documents (
    id BIGSERIAL PRIMARY KEY,
    doc_type VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL,
    sale_id BIGINT,
    uf VARCHAR(2),
    environment VARCHAR(20),
    access_key VARCHAR(44),
    xml TEXT,
    xml_signed TEXT,
    xml_proc TEXT,
    protocol VARCHAR(60),
    last_response TEXT,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_fiscal_documents_sale_id ON fiscal_documents (sale_id);
CREATE INDEX idx_fiscal_documents_access_key ON fiscal_documents (access_key);
