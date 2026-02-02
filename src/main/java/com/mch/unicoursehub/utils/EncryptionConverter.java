package com.mch.unicoursehub.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.SneakyThrows;

/**
 * JPA AttributeConverter for automatically encrypting and decrypting String values
 * stored in the database.
 * <p>
 * This converter uses {@link EncryptionUtil} to encrypt data before persisting it
 * and to decrypt it when reading from the database.
 * <p>
 * Example usage:
 * <pre>
 *     @Convert(converter = EncryptionConverter.class)
 *     private String nationalCode;
 * </pre>
 */
@Converter
public class EncryptionConverter implements AttributeConverter<String, String> {

    /**
     * Converts the entity attribute (plain text) into an encrypted value
     * to be stored in the database.
     *
     * @param attribute the plain text attribute to encrypt
     * @return the encrypted string to store in the database,
     *         or {@code null} if the input attribute is {@code null}
     */
    @SneakyThrows
    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute == null ? null : EncryptionUtil.encrypt(attribute);
    }

    /**
     * Converts the encrypted database value back into the plain text
     * attribute for use in the entity.
     *
     * @param dbData the encrypted data retrieved from the database
     * @return the decrypted plain text value, or {@code null} if the input is {@code null}
     */
    @SneakyThrows
    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData == null ? null : EncryptionUtil.decrypt(dbData);
    }
}
