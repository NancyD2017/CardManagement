package com.example.creditCardManagement.configuration;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Converter
public class AesEncryptor implements AttributeConverter<Long, String> {
    private static final String AES = "AES";
    private static final String SECRET_KEY = "SomeSecretKeyoidhujh9jkoi9ydhuj534nh89jjnhs7xvjmhziklogjmmskmrpojksgns8ejHUYhfnnbythbfhjz7lUHNN89f79gin58ujmgnbn";

    @Override
    public String convertToDatabaseColumn(Long attribute) {
        if (attribute == null) return null;
        try {
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), AES);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(String.valueOf(attribute).getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Encryption failed", e);
        }
    }

    @Override
    public Long convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), AES);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return Long.valueOf(new String(cipher.doFinal(Base64.getDecoder().decode(dbData)), StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Decryption failed", e);
        }
    }
}
