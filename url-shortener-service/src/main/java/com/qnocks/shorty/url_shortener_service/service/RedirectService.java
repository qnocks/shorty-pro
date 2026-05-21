package com.qnocks.shorty.url_shortener_service.service;

import jakarta.servlet.http.HttpServletRequest;

public interface RedirectService {

    String getRedirectUrl(HttpServletRequest request, String shortCode);
}
