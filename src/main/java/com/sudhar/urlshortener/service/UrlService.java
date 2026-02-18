package com.sudhar.urlshortener.service;

import com.sudhar.urlshortener.entity.Url;
import com.sudhar.urlshortener.repository.UrlRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final ShortCodeService shortCodeService;

    public UrlService(UrlRepository urlRepository,
                      ShortCodeService shortCodeService) {
        this.urlRepository = urlRepository;
        this.shortCodeService = shortCodeService;
    }

    public String createShortUrl(String originalUrl) {

        String shortCode = shortCodeService.generateShortCode();

        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setShortCode(shortCode);

        urlRepository.save(url);

        System.out.println("CREATED -> " + shortCode);

        return shortCode;
    }

    // ⭐ Redis cache read
    @Cacheable(value = "urls", key = "#shortCode")
    public Optional<Url> getByShortCode(String shortCode) {

        System.out.println("DB HIT -> " + shortCode);

        return urlRepository.findByShortCode(shortCode);
    }

    // ⭐ Redis cache update after click
    @CachePut(value = "urls", key = "#url.shortCode")
    public Url increaseClickCount(Url url) {

        url.setClickCount(url.getClickCount() + 1);

        urlRepository.save(url);

        System.out.println("CLICK UPDATED -> " + url.getShortCode());

        return url;
    }

    public Optional<Url> getStats(String shortCode) {
        return urlRepository.findByShortCode(shortCode);
    }
}
