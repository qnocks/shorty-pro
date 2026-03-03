package com.qnocks.url_shortener_service.controller;

import com.qnocks.url_shortener_service.service.CacheWarmingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Cache management REST controller.
 * 
 * Provides endpoints for cache monitoring, warming, and clearing.
 * Useful for operations and debugging during development and production.
 * 
 * Base path: /api/v1/cache
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/caches")
@RequiredArgsConstructor
@Tag(name = "Cache", description = "APIs for cache management")
public class CacheManagementControllerV1 {

    private final CacheManager cacheManager;
    private final CacheWarmingService cacheWarmingService;

    /**
     * Get cache status information.
     *
     * @return cache manager details and available caches
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getCacheStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("cacheManager", cacheManager.getClass().getSimpleName());
        status.put("availableCaches", cacheManager.getCacheNames());
        
        log.info("Cache status requested");
        return ResponseEntity.ok(status);
    }

    /**
     * Clear all caches.
     *
     * @return operation result
     */
    @PostMapping("/clear-all")
    public ResponseEntity<Map<String, String>> clearAllCaches() {
        try {
            cacheManager.getCacheNames().forEach(cacheName -> {
                var cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    cache.clear();
                    log.info("Cleared cache: {}", cacheName);
                }
            });
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "All caches cleared successfully");
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to clear caches", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to clear caches: " + e.getMessage());
            errorResponse.put("status", "error");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Clear a specific cache.
     *
     * @param cacheName the name of the cache to clear
     * @return operation result
     */
    @PostMapping("/clear/{cacheName}")
    public ResponseEntity<Map<String, String>> clearCache(@PathVariable String cacheName) {
        try {
            var cache = cacheManager.getCache(cacheName);
            if (cache == null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Cache not found: " + cacheName);
                errorResponse.put("status", "error");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            cache.clear();
            log.info("Cleared cache: {}", cacheName);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Cache '" + cacheName + "' cleared successfully");
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to clear cache: {}", cacheName, e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to clear cache: " + e.getMessage());
            errorResponse.put("status", "error");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Warm up the cache by loading all URL mappings.
     *
     * @return operation result
     */
    @PostMapping("/warmup")
    public ResponseEntity<Map<String, String>> warmupCache() {
        try {
            cacheWarmingService.warmupCache();
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Cache warmup initiated (async operation)");
            response.put("status", "success");
            
            log.info("Cache warmup initiated");
            return ResponseEntity.accepted().body(response);
        } catch (Exception e) {
            log.error("Failed to initiate cache warmup", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to initiate cache warmup: " + e.getMessage());
            errorResponse.put("status", "error");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Warm up the cache with a limit on number of entries.
     *
     * @param limit maximum number of URLs to load into cache
     * @return operation result
     */
    @PostMapping("/warmup/{limit}")
    public ResponseEntity<Map<String, String>> warmupCacheWithLimit(@PathVariable int limit) {
        try {
            if (limit <= 0) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Limit must be greater than 0");
                errorResponse.put("status", "error");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            cacheWarmingService.warmupCacheWithLimit(limit);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Cache warmup initiated with limit: " + limit);
            response.put("status", "success");
            
            log.info("Cache warmup initiated with limit: {}", limit);
            return ResponseEntity.accepted().body(response);
        } catch (Exception e) {
            log.error("Failed to initiate cache warmup with limit", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to initiate cache warmup: " + e.getMessage());
            errorResponse.put("status", "error");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
