package com.qnocks.shorty.url_shortener_service.service;

import com.qnocks.shorty.url_shortener_service.dto.CreateShortUrlDto;

public interface UrlService {

    String shortenUrl(CreateShortUrlDto createShortUrlDto);

    String getOriginalUrl(String shorUrl);
}
