package com.lucast.vetcare.catalog;

import java.math.BigDecimal;

public interface ProductFiscalSnapshotProjection {
    Long getProductId();
    String getNcm();
    String getCest();
    String getOrigin();
    String getGtinEan();
    String getGtinEanTrib();
    String getUnitTrib();
    BigDecimal getTribFactor();
    String getServiceListCode();
    String getCfop();
    String getIcmsCode();
    BigDecimal getIcmsRate();
    String getPisCode();
    BigDecimal getPisRate();
    String getCofinsCode();
    BigDecimal getCofinsRate();
    String getIpiCode();
    BigDecimal getIpiRate();
}
