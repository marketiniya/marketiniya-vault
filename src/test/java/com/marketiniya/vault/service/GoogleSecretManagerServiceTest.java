package com.marketiniya.vault.service;

import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.StatusCode;
import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretPayload;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.protobuf.ByteString;
import com.marketiniya.vault.exception.VaultException;
import com.marketiniya.vault.model.SecretsResponse;
import com.marketiniya.vault.service.impl.GoogleSecretManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleSecretManagerServiceTest {

    @Mock
    private SecretManagerServiceClient mockClient;

    private GoogleSecretManagerService service;

    @BeforeEach
    void setUp() {
        service = new GoogleSecretManagerService(mockClient);
        String testProjectId = "test-project-123";
        ReflectionTestUtils.setField(service, "projectId", testProjectId);
    }

    @Test
    void getSecrets_Success_ReturnsAllSecrets() {
        AccessSecretVersionResponse response = AccessSecretVersionResponse.newBuilder()
                .setPayload(SecretPayload.newBuilder()
                        .setData(ByteString.copyFromUtf8("test-secret-value"))
                        .build())
                .build();

        when(mockClient.accessSecretVersion(any(SecretVersionName.class))).thenReturn(response);

        SecretsResponse result = service.getSecrets();

        assertNotNull(result);
        assertEquals("test-secret-value", result.MARKETINYA_PROD_WEB_FIREBASE_API_KEY());
        assertEquals("test-secret-value", result.MARKETINYA_PROD_WEB_FIREBASE_APP_ID());
        assertEquals("test-secret-value", result.MARKETINYA_PROD_WEB_FIREBASE_AUTH_DOMAIN());
        assertEquals("test-secret-value", result.MARKETINYA_PROD_WEB_FIREBASE_MESSAGING_SENDER_ID());
        assertEquals("test-secret-value", result.MARKETINYA_PROD_WEB_FIREBASE_PROJECT_ID());
        assertEquals("test-secret-value", result.MARKETINYA_PROD_WEB_FIREBASE_STORAGE_BUCKET());

        assertEquals("test-secret-value", result.MARKETINYA_WIP_WEB_FIREBASE_API_KEY());
        assertEquals("test-secret-value", result.MARKETINYA_WIP_WEB_FIREBASE_APP_ID());
        assertEquals("test-secret-value", result.MARKETINYA_WIP_WEB_FIREBASE_AUTH_DOMAIN());
        assertEquals("test-secret-value", result.MARKETINYA_WIP_WEB_FIREBASE_MEASUREMENT_ID());
        assertEquals("test-secret-value", result.MARKETINYA_WIP_WEB_FIREBASE_MESSAGING_SENDER_ID());
        assertEquals("test-secret-value", result.MARKETINYA_WIP_WEB_FIREBASE_PROJECT_ID());
        assertEquals("test-secret-value", result.MARKETINYA_WIP_WEB_FIREBASE_STORAGE_BUCKET());

        verify(mockClient, times(13)).accessSecretVersion(any(SecretVersionName.class));
    }

    @Test
    void getSecrets_GoogleApiException_ThrowsVaultExceptionWithGoogleDetails() {
        ApiException googleException = createMockApiException("Secret not found", 404);
        when(mockClient.accessSecretVersion(any(SecretVersionName.class))).thenThrow(googleException);

        VaultException exception = assertThrows(VaultException.class, () -> service.getSecrets());
        
        assertEquals("Secret not found", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void getSecrets_GenericException_ThrowsVaultExceptionWith500() {
        RuntimeException genericException = new RuntimeException("Connection timeout");
        when(mockClient.accessSecretVersion(any(SecretVersionName.class))).thenThrow(genericException);

        VaultException exception = assertThrows(VaultException.class, () -> service.getSecrets());
        
        assertEquals("Connection timeout", exception.getMessage());
        assertEquals(500, exception.getStatusCode());
    }

    @Test
    void getSecrets_PermissionDenied_ThrowsVaultExceptionWith403() {
        ApiException permissionException = createMockApiException("Permission denied", 403);
        when(mockClient.accessSecretVersion(any(SecretVersionName.class))).thenThrow(permissionException);

        VaultException exception = assertThrows(VaultException.class, () -> service.getSecrets());
        
        assertEquals("Permission denied", exception.getMessage());
        assertEquals(403, exception.getStatusCode());
    }

    private ApiException createMockApiException(String message, int httpStatusCode) {
        StatusCode statusCode = mock(StatusCode.class);
        StatusCode.Code code = mock(StatusCode.Code.class);
        
        when(code.getHttpStatusCode()).thenReturn(httpStatusCode);
        when(statusCode.getCode()).thenReturn(code);
        
        ApiException exception = mock(ApiException.class);
        when(exception.getMessage()).thenReturn(message);
        when(exception.getStatusCode()).thenReturn(statusCode);
        
        return exception;
    }
}
