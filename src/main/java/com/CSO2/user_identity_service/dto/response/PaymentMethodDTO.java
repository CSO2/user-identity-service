package com.CSO2.user_identity_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethodDTO {
    private String id;
    private String provider;
    private String lastFourDigits;
    private String expiryDate;
    private Boolean isDefault;
}
