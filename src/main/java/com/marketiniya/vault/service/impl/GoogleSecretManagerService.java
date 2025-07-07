package com.marketiniya.vault.service.impl;

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.marketiniya.vault.constants.SecretNames;
import com.marketiniya.vault.model.SecretsResponse;
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
    public SecretsResponse getSecrets() {
        logger.info("🚀 Retrieving all secrets");
        String prodApiKey = getSecretValue(SecretNames.MARKETINYA_PROD_WEB_FIREBASE_API_KEY);
        String prodAppId = getSecretValue(SecretNames.MARKETINYA_PROD_WEB_FIREBASE_APP_ID);
        String prodAuthDomain = getSecretValue(SecretNames.MARKETINYA_PROD_WEB_FIREBASE_AUTH_DOMAIN);
        String prodMessagingSenderId = getSecretValue(SecretNames.MARKETINYA_PROD_WEB_FIREBASE_MESSAGING_SENDER_ID);
        String prodProjectId = getSecretValue(SecretNames.MARKETINYA_PROD_WEB_FIREBASE_PROJECT_ID);
        String prodStorageBucket = getSecretValue(SecretNames.MARKETINYA_PROD_WEB_FIREBASE_STORAGE_BUCKET);

        String wipApiKey = getSecretValue(SecretNames.MARKETINYA_WIP_WEB_FIREBASE_API_KEY);
        String wipAppId = getSecretValue(SecretNames.MARKETINYA_WIP_WEB_FIREBASE_APP_ID);
        String wipAuthDomain = getSecretValue(SecretNames.MARKETINYA_WIP_WEB_FIREBASE_AUTH_DOMAIN);
        String wipMeasurementId = getSecretValue(SecretNames.MARKETINYA_WIP_WEB_FIREBASE_MEASUREMENT_ID);
        String wipMessagingSenderId = getSecretValue(SecretNames.MARKETINYA_WIP_WEB_FIREBASE_MESSAGING_SENDER_ID);
        String wipProjectId = getSecretValue(SecretNames.MARKETINYA_WIP_WEB_FIREBASE_PROJECT_ID);
        String wipStorageBucket = getSecretValue(SecretNames.MARKETINYA_WIP_WEB_FIREBASE_STORAGE_BUCKET);
        logger.info("✅ Successfully retrieved all secrets");

        return new SecretsResponse(
                prodApiKey, prodAppId, prodAuthDomain, prodMessagingSenderId, prodProjectId, prodStorageBucket,
                wipApiKey, wipAppId, wipAuthDomain, wipMeasurementId, wipMessagingSenderId, wipProjectId, wipStorageBucket
        );
    }

    private String getSecretValue(String secretName) {
        try {
            SecretVersionName secretVersionName = SecretVersionName.of(projectId, secretName, LATEST_VERSION);
            AccessSecretVersionResponse response = client.accessSecretVersion(secretVersionName);
            return response.getPayload().getData().toStringUtf8();
        } catch (Exception e) {
            logger.warn("⚠️ Failed to retrieve secret: {} - Error: {}", secretName, e.getMessage());
            return null;
        }
    }
}
