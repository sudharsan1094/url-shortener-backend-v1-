package com.sudhar.urlshortener.cache;

import com.sudhar.urlshortener.entity.Url;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class UrlCacheService {

    private final ConcurrentHashMap<String, Url> cache = new ConcurrentHashMap<>();

    public Url get(String shortCode) {
        return cache.get(shortCode);
    }

    public void put(String shortCode, Url url) {
        cache.put(shortCode, url);
    }
}
