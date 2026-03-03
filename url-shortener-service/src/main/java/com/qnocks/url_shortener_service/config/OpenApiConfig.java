package com.qnocks.url_shortener_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.openapi")
public class OpenApiConfig {

    private String title;
    private String description;
    private String contactName;
    private String contactEmail;

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .description(description)
                        .contact(new Contact()
                                .name(contactName)
                                .email(contactEmail)));
    }
}