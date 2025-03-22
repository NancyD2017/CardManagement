package com.example.task_management.security.jwt;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

public class JwtKeyGenerator {

    public static void main() {

        SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

        String encodedKey = java.util.Base64.getEncoder().encodeToString(secretKey.getEncoded());

        System.out.println("Generated Key: " + encodedKey);

    }


    public static String generateSecretKey() {
        SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        return java.util.Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
}

