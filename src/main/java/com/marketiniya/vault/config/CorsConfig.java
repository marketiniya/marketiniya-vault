package com.marketiniya.vault.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow all origins for maximum compatibility with frontend apps
        configuration.setAllowedOriginPatterns(List.of("*"));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
        
        configuration.setAllowedHeaders(Arrays.asList(
            "Content-Type",
            "X-API-Key",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // Allow credentials if needed
        configuration.setAllowCredentials(true);
        
        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
