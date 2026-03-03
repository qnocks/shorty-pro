package com.qnocks.url_shortener_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Redis Cache Configuration for URL Shortener Service.
 * 
 * Uses standard Spring Data Redis support with Spring Boot auto-configuration.
 * 
 * Configuration is provided entirely through application.yml:
 * - Spring Boot auto-detects and configures the Redis client (Lettuce by default)
 * - Connection pooling is handled automatically with sensible defaults
 * - RedisCacheManager is auto-configured based on cache properties
 * - TTL, key prefix, and other settings are defined in application.yml
 * 
 * This approach follows Spring Boot conventions and requires minimal code.
 * Redis connection details are externalized via environment variables:
 * - REDIS_HOST (default: localhost)
 * - REDIS_PORT (default: 6379)
 * - REDIS_PASSWORD (default: empty)
 */
@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisCacheConfig implements CachingConfigurer {

    private final CacheErrorHandler cacheErrorHandler;

    @Override
    public org.springframework.cache.interceptor.CacheErrorHandler errorHandler() {
        return cacheErrorHandler;
    }

    /*
    * No need to custom redisTemplate because we cache String -> String and can use built-in StringRedisTemplate
    *
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        var config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        var template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
     */
}
