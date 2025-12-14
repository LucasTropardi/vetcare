package com.lucast.vetcare.common.jpa;

import com.lucast.vetcare.common.enums.FiscalOrigin;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class FiscalOriginConverter implements AttributeConverter<FiscalOrigin, String> {

    @Override
    public String convertToDatabaseColumn(FiscalOrigin attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public FiscalOrigin convertToEntityAttribute(String dbData) {
        return FiscalOrigin.fromCode(dbData);
    }
}
