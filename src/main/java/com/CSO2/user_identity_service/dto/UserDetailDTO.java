package com.CSO2.user_identity_service.dto;

import com.CSO2.user_identity_service.model.enums.PreferredStore;
import com.CSO2.user_identity_service.model.enums.Role;
import com.CSO2.user_identity_service.model.enums.Tier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailDTO {
    private String id;
    private String email;
    private String fullName;
    private String phone;
    private Role role;
    private PreferredStore preferredStore;
    private Integer loyaltyPoints;
    private Tier tier;
    private Boolean newsletterSubscribed;
}
