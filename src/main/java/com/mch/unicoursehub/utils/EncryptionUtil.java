package com.mch.unicoursehub.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Utility class for encrypting and decrypting strings using AES symmetric encryption.
 * <p>
 * This class provides static methods to encrypt plain text data into an AES-encrypted Base64 string
 * and to decrypt such strings back to plain text.
 * <p>
 * <strong>Note:</strong> The secret key is hardcoded for demonstration purposes.
 * In a production environment, it should be securely stored and managed.
 */
public class EncryptionUtil {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    // Secret key should be stored securely
    private static final byte[] SECRET_KEY = "mysecretkey12345".getBytes();

    /**
     * Encrypts the provided plain text using AES and encodes it in Base64.
     *
     * @param data the plain text to encrypt
     * @return the Base64-encoded AES encrypted string
     * @throws Exception if encryption fails
     */
    public static String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKey secretKey = new SecretKeySpec(SECRET_KEY, ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Decrypts a Base64-encoded AES-encrypted string back into plain text.
     *
     * @param encryptedData the Base64-encoded encrypted string
     * @return the decrypted plain text
     * @throws Exception if decryption fails
     */
    public static String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKey secretKey = new SecretKeySpec(SECRET_KEY, ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }
}
