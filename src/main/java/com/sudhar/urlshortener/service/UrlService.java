package com.sudhar.urlshortener.service;

import com.sudhar.urlshortener.cache.UrlCacheService;
import com.sudhar.urlshortener.entity.Url;
import com.sudhar.urlshortener.repository.UrlRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final ShortCodeService shortCodeService;
    private final UrlCacheService urlCacheService;

    public UrlService(UrlRepository urlRepository,
                      ShortCodeService shortCodeService,
                      UrlCacheService urlCacheService) {
        this.urlRepository = urlRepository;
        this.shortCodeService = shortCodeService;
        this.urlCacheService = urlCacheService;
    }

    public String createShortUrl(String originalUrl) {

        String shortCode = shortCodeService.generateShortCode();

        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setShortCode(shortCode);

        urlRepository.save(url);

        // put into cache immediately
//        urlCacheService.put(shortCode, url);

        System.out.println("CREATED -> " + shortCode);

        return shortCode;
    }

    public Optional<Url> getByShortCode(String shortCode) {

        System.out.println("******** METHOD CALLED ********");

        Url cached = urlCacheService.get(shortCode);

        if (cached != null) {
            System.out.println("CACHE HIT -> " + shortCode);
            return Optional.of(cached);
        }

        System.out.println("DB HIT -> " + shortCode);

        Optional<Url> dbUrl = urlRepository.findByShortCode(shortCode);

        dbUrl.ifPresent(url -> urlCacheService.put(shortCode, url));

        return dbUrl;
    }

    public void increaseClickCount(Url url) {

        url.setClickCount(url.getClickCount() + 1);

        urlRepository.save(url);

        // update cache after DB update
        urlCacheService.put(url.getShortCode(), url);

        System.out.println("CLICK UPDATED -> " + url.getShortCode());
    }
}
