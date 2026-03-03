package com.qnocks.url_shortener_service.controller;

import com.qnocks.url_shortener_service.service.RedirectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/redirect")
@Tag(name = "Redirect", description = "APIs for redirecting shortened URLs to their original destinations")
public class RedirectControllerV1 {

    private final RedirectService redirectService;

    @GetMapping("/{shortCode}")
    @Operation(summary = "Redirect to original URL", description = "Redirects the client to the original URL associated with the given short code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Successfully redirected to original URL"),
            @ApiResponse(responseCode = "404", description = "Shortened URL not found")
    })
    public void redirectToOriginalUrl(
            @Parameter(description = "The short code to redirect", required = true)
            @PathVariable String shortCode,
            HttpServletRequest request,
            HttpServletResponse response) {
        String originalUrl = redirectService.getRedirectUrl(request, shortCode);

        response.setStatus(HttpStatus.FOUND.value());
        response.setHeader("Location", originalUrl);
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    }
}
