package com.CSO2.user_identity_service.controller;

import com.CSO2.user_identity_service.dto.request.PaymentMethodRequest;
import com.CSO2.user_identity_service.dto.response.PaymentMethodDTO;
import com.CSO2.user_identity_service.service.PaymentMethodService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PaymentMethodControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentMethodService paymentMethodService;

    @InjectMocks
    private PaymentMethodController paymentMethodController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");

        mockMvc = MockMvcBuilders.standaloneSetup(paymentMethodController)
                .setCustomArgumentResolvers(putAuthenticationPrincipal(userDetails))
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getUserPaymentMethods_ShouldReturnOk() throws Exception {
        when(paymentMethodService.getUserPaymentMethodsByEmail("test@example.com")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users/me/payment-methods"))
                .andExpect(status().isOk());
    }

    @Test
    void addPaymentMethod_ShouldReturnCreated() throws Exception {
        PaymentMethodRequest request = new PaymentMethodRequest();
        when(paymentMethodService.addPaymentMethodByEmail(anyString(), any(PaymentMethodRequest.class)))
                .thenReturn(PaymentMethodDTO.builder().build());

        mockMvc.perform(post("/api/users/me/payment-methods")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void updatePaymentMethod_ShouldReturnOk() throws Exception {
        PaymentMethodRequest request = new PaymentMethodRequest();
        when(paymentMethodService.updatePaymentMethodByEmail(anyString(), anyString(), any(PaymentMethodRequest.class)))
                .thenReturn(PaymentMethodDTO.builder().build());

        mockMvc.perform(put("/api/users/me/payment-methods/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void deletePaymentMethod_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/me/payment-methods/1"))
                .andExpect(status().isNoContent());
    }

    private org.springframework.web.method.support.HandlerMethodArgumentResolver putAuthenticationPrincipal(
            UserDetails userDetails) {
        return new org.springframework.web.method.support.HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(org.springframework.core.MethodParameter parameter) {
                return parameter.getParameterType().isAssignableFrom(UserDetails.class);
            }

            @Override
            public Object resolveArgument(org.springframework.core.MethodParameter parameter,
                    org.springframework.web.method.support.ModelAndViewContainer mavContainer,
                    org.springframework.web.context.request.NativeWebRequest webRequest,
                    org.springframework.web.bind.support.WebDataBinderFactory binderFactory) throws Exception {
                return userDetails;
            }
        };
    }
}
