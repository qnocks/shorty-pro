package com.qnocks.url_shortener_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.stereotype.Component;

/**
 * Custom cache error handler that gracefully handles cache failures.
 * 
 * Ensures the application continues to operate even if Redis becomes unavailable.
 * All cache errors are logged but not propagated to maintain service availability.
 * 
 * This is essential for the cache-aside pattern where database fallback is always available.
 */
@Slf4j
@Component
public class CacheErrorHandler extends SimpleCacheErrorHandler {

    @Override
    public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
        log.warn("Cache GET failed for key: {}, cache: {}. Falling back to database.", key, cache.getName(), e);
    }

    @Override
    public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
        log.warn("Cache PUT failed for key: {}, cache: {}. Continuing without cache update.", key, cache.getName(), e);
    }

    @Override
    public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
        log.warn("Cache EVICT failed for key: {}, cache: {}. Continuing without eviction.", key, cache.getName(), e);
    }

    @Override
    public void handleCacheClearError(RuntimeException e, Cache cache) {
        log.warn("Cache CLEAR failed for cache: {}. Continuing without clearing.", cache.getName(), e);
    }
}