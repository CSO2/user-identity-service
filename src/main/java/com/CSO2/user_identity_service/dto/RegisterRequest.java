package com.CSO2.user_identity_service.dto;

import com.CSO2.user_identity_service.model.enums.PreferredStore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private PreferredStore preferredStore;
}
