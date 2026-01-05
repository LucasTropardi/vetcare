package com.lucast.vetcare.fiscal.document;

import com.lucast.vetcare.fiscal.domain.FiscalDocumentEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "fiscal_events")
public class FiscalEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "fiscal_document_id")
    private FiscalDocumentEntity document;

    @Column(name = "event_type", nullable = false, length = 30)
    private String eventType;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "request_xml", columnDefinition = "text")
    private String requestXml;

    @Column(name = "response_xml", columnDefinition = "text")
    private String responseXml;

    @Column(name = "sefaz_message", columnDefinition = "text")
    private String sefazMessage;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = OffsetDateTime.now();
    }
}
