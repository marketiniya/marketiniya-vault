package com.marketiniya.vault.controller;

import com.marketiniya.vault.model.SecretsResponse;
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
    void getSecrets_ShouldReturnOk_WhenAllSecretsRequested() throws Exception {
        // Given
        SecretsResponse response = new SecretsResponse(
            "prod-api-key", "prod-app-id", "prod-auth-domain", "prod-sender-id", "prod-project-id", "prod-storage",
            "wip-api-key", "wip-app-id", "wip-auth-domain", "wip-measurement-id", "wip-sender-id", "wip-project-id", "wip-storage"
        );
        when(secretService.getSecrets()).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/vault/secrets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.MARKETINYA_PROD_WEB_FIREBASE_API_KEY").value("prod-api-key"))
                .andExpect(jsonPath("$.MARKETINYA_WIP_WEB_FIREBASE_API_KEY").value("wip-api-key"));
    }
}
