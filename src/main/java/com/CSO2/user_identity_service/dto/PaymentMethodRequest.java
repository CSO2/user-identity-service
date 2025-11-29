package com.CSO2.user_identity_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethodRequest {
    private String provider;
    private String lastFourDigits;
    private String token;
    private String expiryDate;
    private Boolean isDefault;
}
