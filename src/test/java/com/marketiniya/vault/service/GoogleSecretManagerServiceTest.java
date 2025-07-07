package com.marketiniya.vault.service;

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretPayload;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.protobuf.ByteString;
import com.marketiniya.vault.model.SecretResponse;
import com.marketiniya.vault.model.SecretState;
import com.marketiniya.vault.service.impl.GoogleSecretManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleSecretManagerServiceTest {
    
    @Mock
    private SecretManagerServiceClient mockClient;
    
    private GoogleSecretManagerService secretService;
    private static final String PROJECT_ID = "test-project";
    private static final String SECRET_NAME = "test-secret";
    private static final String SECRET_VALUE = "test-value";
    
    @BeforeEach
    void setUp() {
        secretService = new GoogleSecretManagerService(mockClient, PROJECT_ID);
    }
    
    @Test
    void getSecret_ShouldReturnSecretResponse_WhenSecretExists() {
        // Given
        AccessSecretVersionResponse mockResponse = createMockResponse();
        when(mockClient.accessSecretVersion(any(SecretVersionName.class))).thenReturn(mockResponse);
        
        // When
        SecretResponse result = secretService.getSecret(SECRET_NAME, "latest");
        
        // Then
        assertNotNull(result);
        assertEquals(SECRET_NAME, result.name());
        assertEquals(SECRET_VALUE, result.value());
        assertEquals("latest", result.version());
        assertEquals(SecretState.ENABLED, result.state());
    }
    
    @Test
    void getSecret_ShouldReturnNotFound_WhenSecretDoesNotExist() {
        // Given
        when(mockClient.accessSecretVersion(any(SecretVersionName.class))).thenThrow(new RuntimeException("Secret not found"));
        
        // When
        SecretResponse result = secretService.getSecret(SECRET_NAME, "latest");
        
        // Then
        assertNotNull(result);
        assertEquals(SECRET_NAME, result.name());
        assertNull(result.value());
        assertEquals(SecretState.NOT_FOUND, result.state());
    }
    
    private AccessSecretVersionResponse createMockResponse() {
        SecretPayload payload = SecretPayload.newBuilder()
                .setData(ByteString.copyFromUtf8(GoogleSecretManagerServiceTest.SECRET_VALUE))
                .setDataCrc32C(System.currentTimeMillis() / 1000)
                .build();
        
        return AccessSecretVersionResponse.newBuilder()
                .setPayload(payload)
                .build();
    }
}
