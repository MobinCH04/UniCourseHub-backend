package com.mch.unicoursehub.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.SneakyThrows;

@Converter
public class EncryptionConverter implements AttributeConverter<String, String> {

    @SneakyThrows
    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute == null ? null : EncryptionUtil.encrypt(attribute);
    }

    @SneakyThrows
    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData == null ? null : EncryptionUtil.decrypt(dbData);
    }
}
