package com.CSO2.user_identity_service.controller;

import com.CSO2.user_identity_service.dto.request.ChangePasswordRequest;
import com.CSO2.user_identity_service.dto.request.UpdateProfileRequest;
import com.CSO2.user_identity_service.dto.response.UserDetailDTO;
import com.CSO2.user_identity_service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getProfile_ShouldReturnOk_WhenUserIsAuthenticated() throws Exception {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");

        when(userService.getProfileByEmail("test@example.com")).thenReturn(UserDetailDTO.builder().build());

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(putAuthenticationPrincipal(userDetails))
                .build();

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk());
    }

    @Test
    void updateProfile_ShouldReturnOk_WhenRequestIsValid() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFullName("Updated Name");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(putAuthenticationPrincipal(userDetails))
                .build();

        when(userService.updateProfileByEmail(anyString(), any(UpdateProfileRequest.class)))
                .thenReturn(UserDetailDTO.builder().build());

        mockMvc.perform(put("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void changePassword_ShouldReturnOk_WhenRequestIsValid() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPassword");
        request.setNewPassword("newPassword");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(putAuthenticationPrincipal(userDetails))
                .build();

        mockMvc.perform(put("/api/users/me/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
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
