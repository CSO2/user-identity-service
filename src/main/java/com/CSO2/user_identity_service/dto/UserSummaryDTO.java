package com.CSO2.user_identity_service.dto;

import com.CSO2.user_identity_service.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSummaryDTO {
    private String id;
    private String email;
    private String fullName;
    private Role role;
}
