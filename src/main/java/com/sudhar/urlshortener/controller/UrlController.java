package com.sudhar.urlshortener.controller;

import com.sudhar.urlshortener.entity.Url;
import com.sudhar.urlshortener.service.UrlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    // 🔹 CREATE SHORT URL
    @PostMapping("/shorten")
    public String create(@RequestParam String url) {

        String shortCode = urlService.createShortUrl(url);

        return "http://localhost:8080/" + shortCode;
    }

    // 🔹 REDIRECT
    @GetMapping("/{shortCode}")
    public ResponseEntity<?> redirect(@PathVariable String shortCode) {

        Optional<Url> urlOptional = urlService.getByShortCode(shortCode);

        if (urlOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Url url = urlOptional.get();

        urlService.increaseClickCount(url);

        return ResponseEntity
                .status(302)
                .location(URI.create(url.getOriginalUrl()))
                .build();
    }
}
