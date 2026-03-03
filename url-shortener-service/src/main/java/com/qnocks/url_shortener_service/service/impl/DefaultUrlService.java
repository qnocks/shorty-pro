package com.qnocks.url_shortener_service.service.impl;

import com.qnocks.url_shortener_service.dto.CreateShortUrlDto;
import com.qnocks.url_shortener_service.entity.UrlMapping;
import com.qnocks.url_shortener_service.repository.UrlMappingRepository;
import com.qnocks.url_shortener_service.service.KeyGenerator;
import com.qnocks.url_shortener_service.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultUrlService implements UrlService {

    // TODO: transactional outbox pattern
    // TODO: @Transactional

    private static final int MAX_RETRIES = 5;
    private final KeyGenerator keyGenerator;
    private final UrlMappingRepository urlMappingRepository;

    @Override
    public String shortenUrl(CreateShortUrlDto createShortUrlDto) {
        var originalUrl = createShortUrlDto.url();

        if (!StringUtils.hasText(originalUrl)) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }

        // Check if URL is already shortened
        Optional<String> existingShortUrl = urlMappingRepository.findShortUrlByOriginalUrl(originalUrl);
        if (existingShortUrl.isPresent()) {
            return existingShortUrl.get();
        }

        // Generate unique short key
        String shortKey = generateUniqueShortKey();

        // Store mapping
        UrlMapping urlMapping = UrlMapping.builder()
                .originalUrl(originalUrl)
                .shortUrl(shortKey)
                .build();

        urlMappingRepository.save(urlMapping);

        return urlMapping.getShortUrl();
    }

    @Override
    @Cacheable(value = "originalUrl", key = "#shortKey", unless = "#result == null")
    public String getOriginalUrl(String shortKey) {
        if (!StringUtils.hasText(shortKey)) {
            throw new IllegalArgumentException("Short key cannot be null or empty");
        }

        log.debug("Cache miss for short key: {}. Querying database.", shortKey);
        return urlMappingRepository.findOriginalUrlByShortUrl(shortKey).orElseThrow(
                () -> new IllegalArgumentException(String.format("Original URL not found by code: [%s]", shortKey)));
    }

    /**
     * Caches the URL mapping for fast retrieval on subsequent requests.
     * Uses @CachePut to ensure cache is always updated with the latest value.
     *
     * @param shortKey the short URL key
     * @param originalUrl the original URL
     */
    @CachePut(value = "originalUrl", key = "#shortKey")
    public String cacheUrlMapping(String shortKey, String originalUrl) {
        log.debug("Caching URL mapping: {} -> {}", shortKey, originalUrl);
        return originalUrl;
    }

    private String generateUniqueShortKey() {
        int retries = 0;

        while (retries < MAX_RETRIES) {
            String shortKey = keyGenerator.generate();
            if (!urlMappingRepository.existsByShortUrl(shortKey)) {
                return shortKey;
            }

            retries++;
        }

        log.error("Unable to generate unique short URL after {} attempts", MAX_RETRIES);
        throw new RuntimeException("Unable to generate unique short URL after " + MAX_RETRIES + " attempts");
    }
}