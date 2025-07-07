package com.marketiniya.vault.controller;

import com.marketiniya.vault.model.SecretsResponse;
import com.marketiniya.vault.service.SecretService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vault")
@Validated
public class VaultController {
    private final SecretService secretService;

    public VaultController(SecretService secretService) {
        this.secretService = secretService;
    }

    @GetMapping("/secrets")
    public ResponseEntity<SecretsResponse> getSecrets() {
        SecretsResponse response = secretService.getSecrets();
        return ResponseEntity.ok(response);
    }
}
