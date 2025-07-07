package com.marketiniya.vault.controller;

import com.marketiniya.vault.model.SecretResponse;
import com.marketiniya.vault.service.SecretService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;



import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VaultController.class)
class VaultControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SecretService secretService;
    
    @Test
    @WithMockUser
    void getSecret_ShouldReturnOk_WhenSecretTypeRequested() throws Exception {
        // Given
        String secretName = "test-secret";
        SecretResponse response = SecretResponse.success(secretName, "test-value", "latest");
        when(secretService.getSecret(secretName, null)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/vault/secrets")
                .param("name", secretName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(secretName))
                .andExpect(jsonPath("$.value").value("test-value"))
                .andExpect(jsonPath("$.state").value("ENABLED"));
    }

    @Test
    @WithMockUser
    void getSecret_ShouldReturnOk_WhenSecretVersionRequested() throws Exception {
        // Given
        String secretName = "test-secret";
        String version = "1";
        SecretResponse response = SecretResponse.success(secretName, "test-value", version);
        when(secretService.getSecret(secretName, version)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/vault/secrets")
                .param("name", secretName)
                .param("version", version))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(secretName))
                .andExpect(jsonPath("$.version").value(version));
    }

    @Test
    @WithMockUser
    void getSecret_ShouldReturnBadRequest_WhenMissingName() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/vault/secrets"))
                .andExpect(status().isBadRequest());
    }
}
