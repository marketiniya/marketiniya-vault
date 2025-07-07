package com.marketiniya.vault.controller;

import com.marketiniya.vault.model.SecretsResponse;
import com.marketiniya.vault.service.SecretService;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Objects;

@RestController
@RequestMapping("/api/vault")
@Validated
public class VaultController {
    private final SecretService secretService;
    private final CacheManager cacheManager;

    public VaultController(SecretService secretService, CacheManager cacheManager) {
        this.secretService = secretService;
        this.cacheManager = cacheManager;
    }

    @GetMapping("/secrets")
    public ResponseEntity<SecretsResponse> getSecrets() {
        SecretsResponse response = secretService.getSecrets();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cache/clear")
    public ResponseEntity<String> clearCache() {
        Objects.requireNonNull(cacheManager.getCache("secrets")).clear();
        return ResponseEntity.ok("Cache cleared successfully");
    }
}
