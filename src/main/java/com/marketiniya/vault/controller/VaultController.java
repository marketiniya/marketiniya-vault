package com.marketiniya.vault.controller;

import com.marketiniya.vault.model.SecretResponse;
import com.marketiniya.vault.model.SecretState;
import com.marketiniya.vault.service.SecretService;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vault")
@Validated
public class VaultController {

    private static final Logger logger = LoggerFactory.getLogger(VaultController.class);
    private final SecretService secretService;

    public VaultController(SecretService secretService) {
        this.secretService = secretService;
    }

    @GetMapping("/secrets")
    public ResponseEntity<SecretResponse> getSecret(
            @RequestParam @NotBlank String name,
            @RequestParam(required = false) String version) {

        logger.info("🚀 API Request: GET /api/vault/secrets?name={}", name);
        SecretResponse response = secretService.getSecret(name, version);
        return buildResponse(response);
    }

    private ResponseEntity<SecretResponse> buildResponse(SecretResponse response) {
        return SecretState.NOT_FOUND.equals(response.state()) ? ResponseEntity.notFound().build() : ResponseEntity.ok(response);
    }
}
