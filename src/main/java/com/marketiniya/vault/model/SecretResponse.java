package com.marketiniya.vault.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SecretResponse(
        @NotBlank String name,
        String value,
        String version,
        SecretState state
) {
    public static SecretResponse success(String name, String value, String version) {
        return new SecretResponse(name, value, version, SecretState.ENABLED);
    }

    public static SecretResponse notFound(String name) {
        return new SecretResponse(name, null, null, SecretState.NOT_FOUND);
    }
}
