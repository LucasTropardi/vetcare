package com.lucast.vetcare.sales;

import java.math.BigDecimal;
import java.time.Instant;

public interface SaleListProjection {
    Long getId();
    Long getCompanyId();
    Long getTutorId();
    Long getCustomerCompanyId();
    Long getAppointmentId();
    String getStatus();
    BigDecimal getSubtotal();
    BigDecimal getDiscount();
    BigDecimal getTotal();
    BigDecimal getPaidTotal();
    BigDecimal getRemaining();
    String getNotes();
    Long getCreatedBy();
    Long getConfirmedBy();
    Instant getConfirmedAt();
    Long getCanceledBy();
    Instant getCanceledAt();
    Instant getCreatedAt();
    Instant getUpdatedAt();
    Integer getItemCount();
    String getCustomerName();
    String getCustomerDocument();
    String getRegisterCode();
    Long getSaleNumber();
    String getDocumentNumber();
    String getProtocol();
    Boolean getCanCancel();
    Boolean getXmlAvailable();
    Boolean getReceiptAvailable();
}
