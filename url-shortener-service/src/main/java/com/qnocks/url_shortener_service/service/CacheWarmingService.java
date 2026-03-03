package com.qnocks.url_shortener_service.service;

public interface CacheWarmingService {

    void warmupCache();
    void warmupCacheWithLimit(int limit);
}
