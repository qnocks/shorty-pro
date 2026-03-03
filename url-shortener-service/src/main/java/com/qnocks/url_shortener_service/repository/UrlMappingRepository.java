package com.qnocks.url_shortener_service.repository;

import com.qnocks.url_shortener_service.entity.UrlMapping;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlMappingRepository extends CrudRepository<UrlMapping, String> {

    @Query("SELECT short_url FROM url_mappings WHERE original_url = :originalUrl")
    Optional<String> findShortUrlByOriginalUrl(String originalUrl);

    @Query("SELECT original_url FROM url_mappings WHERE short_url = :shortUrl")
    Optional<String> findOriginalUrlByShortUrl(String shortUrl);

    boolean existsByShortUrl(String shortKey);
}
