package com.qnocks.url_shortener_service.service.impl;

import com.qnocks.url_shortener_service.repository.UrlMappingRepository;
import com.qnocks.url_shortener_service.service.CacheWarmingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service for asynchronous cache warming.
 * 
 * Loads frequently accessed URLs into Redis cache to improve performance
 * and reduce database load during startup or on-demand.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultCacheWarmingService implements CacheWarmingService {

    private final UrlMappingRepository urlMappingRepository;
    private final DefaultUrlService urlService;

    /**
     * Warms up the cache by loading all URL mappings into Redis.
     * This operation runs asynchronously and doesn't block application startup.
     * 
     * Use case: Call this method during application startup or periodically
     * to ensure frequently used URL mappings are cached.
     */
    @Async
    @Override
    public void warmupCache() {
        log.info("Starting cache warmup process");
        long startTime = System.currentTimeMillis();
        int cachedCount = 0;

        try {
            for (var mapping : urlMappingRepository.findAll()) {
                try {
                    urlService.cacheUrlMapping(mapping.getShortUrl(), mapping.getOriginalUrl());
                    cachedCount++;
                } catch (Exception e) {
                    log.warn("Failed to cache URL mapping: {} -> {}", mapping.getShortUrl(), mapping.getOriginalUrl(), e);
                }
            }

            long duration = System.currentTimeMillis() - startTime;
            log.info("Cache warmup completed. Cached {} URLs in {} ms", cachedCount, duration);
        } catch (Exception e) {
            log.error("Cache warmup process failed", e);
        }
    }

    /**
     * Warms up cache with a batch of specific URLs.
     * Useful for pre-loading popular or recently accessed URLs.
     *
     * @param limit maximum number of URLs to load into cache
     */
    @Async
    @Override
    public void warmupCacheWithLimit(int limit) {
        log.info("Starting cache warmup process with limit: {}", limit);
        long startTime = System.currentTimeMillis();
        int cachedCount = 0;

        try {
            for (var mapping : urlMappingRepository.findAll()) {
                if (cachedCount >= limit) {
                    return;
                }

                try {
                    urlService.cacheUrlMapping(mapping.getShortUrl(), mapping.getOriginalUrl());
                    cachedCount++;
                } catch (Exception e) {
                    log.warn("Failed to cache URL mapping: {} -> {}", mapping.getShortUrl(), mapping.getOriginalUrl(), e);
                }
            }

            long duration = System.currentTimeMillis() - startTime;
            log.info("Limited cache warmup completed. Cached {} URLs in {} ms", cachedCount, duration);
        } catch (Exception e) {
            log.error("Limited cache warmup process failed", e);
        }
    }
}