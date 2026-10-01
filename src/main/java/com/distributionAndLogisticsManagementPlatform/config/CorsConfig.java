package com.distributionAndLogisticsManagementPlatform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//Add your annotations here
public class CorsConfig implements WebMvcConfigurer {

    @Value("${app.allowed-origins:http://localhost:4200}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("POST", "GET", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Location", "Content-Disposition")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
