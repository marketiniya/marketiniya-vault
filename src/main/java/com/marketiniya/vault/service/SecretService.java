package com.marketiniya.vault.service;

import com.marketiniya.vault.model.SecretResponse;

public interface SecretService {
    SecretResponse getSecret(String secretName, String version);
}
