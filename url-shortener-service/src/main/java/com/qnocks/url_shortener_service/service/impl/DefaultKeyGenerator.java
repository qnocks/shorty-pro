package com.qnocks.url_shortener_service.service.impl;

import com.qnocks.url_shortener_service.service.KeyGenerator;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class DefaultKeyGenerator implements KeyGenerator {

    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int SHORT_URL_LENGTH = 7;
    private final SecureRandom random = new SecureRandom();

    @Override
    public String generate() {
        StringBuilder sb = new StringBuilder(SHORT_URL_LENGTH);

        for (int i = 0; i < SHORT_URL_LENGTH; i++) {
            int randomIdx = random.nextInt(BASE62_CHARS.length());
            sb.append(BASE62_CHARS.charAt(randomIdx));
        }

        return sb.toString();
    }
}
