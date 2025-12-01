package com.CSO2.user_identity_service.controller;

import com.CSO2.user_identity_service.dto.request.AddressRequest;
import com.CSO2.user_identity_service.dto.response.AddressDTO;
import com.CSO2.user_identity_service.service.AddressService;
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
class AddressControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AddressService addressService;

    @InjectMocks
    private AddressController addressController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");

        mockMvc = MockMvcBuilders.standaloneSetup(addressController)
                .setCustomArgumentResolvers(putAuthenticationPrincipal(userDetails))
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getUserAddresses_ShouldReturnOk() throws Exception {
        when(addressService.getUserAddressesByEmail("test@example.com")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users/me/addresses"))
                .andExpect(status().isOk());
    }

    @Test
    void addAddress_ShouldReturnCreated() throws Exception {
        AddressRequest request = new AddressRequest();
        when(addressService.addAddressByEmail(anyString(), any(AddressRequest.class)))
                .thenReturn(AddressDTO.builder().build());

        mockMvc.perform(post("/api/users/me/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateAddress_ShouldReturnOk() throws Exception {
        AddressRequest request = new AddressRequest();
        when(addressService.updateAddressByEmail(anyString(), anyString(), any(AddressRequest.class)))
                .thenReturn(AddressDTO.builder().build());

        mockMvc.perform(put("/api/users/me/addresses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteAddress_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/me/addresses/1"))
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
