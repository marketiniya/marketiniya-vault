package com.marketiniya.vault.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketiniya.vault.model.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${vault.api.key:}")
    private String apiKey;

    private final CorsConfigurationSource corsConfigurationSource;
    private final ObjectMapper objectMapper;

    public SecurityConfig(CorsConfigurationSource corsConfigurationSource, ObjectMapper objectMapper) {
        this.corsConfigurationSource = corsConfigurationSource;
        this.objectMapper = objectMapper.findAndRegisterModules();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                );

        if (apiKey != null && !apiKey.isBlank()) {
            http.addFilterBefore(new ApiKeyAuthenticationFilter(apiKey), UsernamePasswordAuthenticationFilter.class);
        }
        
        return http.build();
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, ex) -> writeErrorResponse(response,
                ErrorResponse.of("AUTHENTICATION_ERROR", "Missing or invalid API key", 401, request.getRequestURI()),
                HttpServletResponse.SC_UNAUTHORIZED
        );
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) -> writeErrorResponse(response,
                ErrorResponse.of("ACCESS_DENIED", "Access denied", 403, request.getRequestURI()),
                HttpServletResponse.SC_FORBIDDEN
        );
    }

    private void writeErrorResponse(HttpServletResponse response, ErrorResponse error, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), error);
    }
}
