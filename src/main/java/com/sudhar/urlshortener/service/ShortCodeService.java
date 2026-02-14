package com.sudhar.urlshortener.service;

import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class ShortCodeService {

    private static final String CHARSET =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final int LENGTH = 6;

    public String generateShortCode() {

        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for(int i = 0; i < LENGTH; i++) {
            int index = random.nextInt(CHARSET.length());
            code.append(CHARSET.charAt(index));
        }

        return code.toString();
    }
}
