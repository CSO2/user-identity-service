package com.CSO2.user_identity_service.controller;

import com.CSO2.user_identity_service.dto.request.LoginRequest;
import com.CSO2.user_identity_service.dto.request.RegisterRequest;
import com.CSO2.user_identity_service.dto.response.AuthenticationResponse;
import com.CSO2.user_identity_service.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void register_ShouldReturnCreated_WhenRequestIsValid() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setFullName("Test User");
        request.setPhone("1234567890");

        when(authService.register(any(RegisterRequest.class))).thenReturn(AuthenticationResponse.builder().build());

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void login_ShouldReturnOk_WhenRequestIsValid() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(authService.login(any(LoginRequest.class))).thenReturn(AuthenticationResponse.builder().build());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void refresh_ShouldReturnOk_WhenRefreshTokenIsValid() throws Exception {
        String refreshToken = "validRefreshToken";

        when(authService.refresh(any(String.class))).thenReturn(AuthenticationResponse.builder().build());

        mockMvc.perform(post("/api/auth/refresh")
                .param("refreshToken", refreshToken))
                .andExpect(status().isOk());
    }
}
