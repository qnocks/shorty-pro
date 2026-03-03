package com.qnocks.url_shortener_service.controller;

import com.qnocks.url_shortener_service.dto.CreateShortUrlDto;
import com.qnocks.url_shortener_service.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/urls")
@Tag(name = "URL", description = "APIs for creating and retrieving shortened URLs")
public class UrlControllerV1 {

    private final UrlService urlService;

    @PostMapping
    @Operation(summary = "Create a shortened URL", description = "Generates a shortened URL for the provided original URL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created shortened URL",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "abc123"))),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    public String shortenUrl(@RequestBody @Validated CreateShortUrlDto createShortUrlDto) {
        return urlService.shortenUrl(createShortUrlDto);
    }

    @GetMapping
    @Operation(summary = "Get original URL", description = "Retrieves the original URL for a given shortened URL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved original URL",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "https://example.com/long-url"))),
            @ApiResponse(responseCode = "404", description = "Shortened URL not found")
    })
    public String getOriginalUrl(
            @Parameter(description = "The shortened URL code", required = true)
            @RequestParam String shortUrl) {
        return urlService.getOriginalUrl(shortUrl);
    }
}
