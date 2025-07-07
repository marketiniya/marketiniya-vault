package com.marketiniya.vault.service.impl;

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.marketiniya.vault.model.SecretResponse;
import com.marketiniya.vault.service.SecretService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleSecretManagerService implements SecretService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleSecretManagerService.class);
    private static final String LATEST_VERSION = "latest";

    private final SecretManagerServiceClient client;
    private final String projectId;

    public GoogleSecretManagerService(
            SecretManagerServiceClient client,
            @Value("${google.cloud.project-id}") String projectId) {
        this.client = client;
        this.projectId = projectId;
    }

    @Override
    public SecretResponse getSecret(String secretName, String version) {
        try {
            String currentVersion = (version == null || version.trim().isEmpty()) ? LATEST_VERSION : version;

            SecretVersionName secretVersionName = SecretVersionName.of(projectId, secretName, currentVersion);

            AccessSecretVersionResponse response = client.accessSecretVersion(secretVersionName);
            String secretValue = response.getPayload().getData().toStringUtf8();

            logger.info("✅ Successfully retrieved secret: {}", secretName);
            return SecretResponse.success(secretName, secretValue, currentVersion);
        } catch (Exception e) {
            logger.error("❌ Failed to retrieve secret: {} version: {} - Error: {}", secretName, version, e.getMessage());
            return SecretResponse.notFound(secretName);
        }
    }
}
