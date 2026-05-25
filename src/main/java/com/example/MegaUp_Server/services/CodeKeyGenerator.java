package com.example.MegaUp_Server.services;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class CodeKeyGenerator {

    // SecureRandom é criptograficamente seguro (CSPRNG), ao contrário de Random
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // Charset sem caracteres especiais (ç/Ç causam problemas de encoding em email)
    private static final String CHARSET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public String gerarKey() {
        int size = 64;
        StringBuilder key = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            key.append(CHARSET.charAt(SECURE_RANDOM.nextInt(CHARSET.length())));
        }
        return key.toString();
    }
}
