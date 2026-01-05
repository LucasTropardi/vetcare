package com.lucast.vetcare.fiscal.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "fiscal_documents")
public class FiscalDocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_type", nullable = false, length = 10)
    private FiscalDocumentType docType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FiscalDocumentStatus status = FiscalDocumentStatus.DRAFT;

    @Column(name = "sale_id")
    private Long saleId;

    @Column(name = "uf", length = 2)
    private String uf;

    @Column(name = "environment", length = 20)
    private String environment;

    @Column(name = "access_key", length = 44)
    private String accessKey;

    @Column(name = "xml", columnDefinition = "text")
    private String xml;

    @Column(name = "xml_signed", columnDefinition = "text")
    private String xmlSigned;

    @Column(name = "xml_proc", columnDefinition = "text")
    private String xmlProc;

    @Column(name = "protocol", length = 60)
    private String protocol;

    @Column(name = "last_response", columnDefinition = "text")
    private String lastResponse;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersist() {
        final var now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
