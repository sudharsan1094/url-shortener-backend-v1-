package com.sudhar.urlshortener.controller;

import com.sudhar.urlshortener.cache.UrlCacheService;
import com.sudhar.urlshortener.entity.Url;
import com.sudhar.urlshortener.service.UrlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
public class UrlController {

    private final UrlService urlService;
    private final UrlCacheService cache;

    public UrlController(UrlService urlService, UrlCacheService cache) {
        this.urlService = urlService;
        this.cache = cache;
    }

    @PostMapping("/shorten")
    public String create(@RequestParam String url) {
        return urlService.createShortUrl(url);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<?> redirect(@PathVariable String shortCode) {

        Url url = cache.get(shortCode);

        if (url == null) {

            Optional<Url> urlOptional = urlService.getByShortCode(shortCode);

            if (urlOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            url = urlOptional.get();
            cache.put(shortCode, url);
        }

        urlService.increaseClickCount(url);

        return ResponseEntity
                .status(302)
                .location(URI.create(url.getOriginalUrl()))
                .build();
    }
}
