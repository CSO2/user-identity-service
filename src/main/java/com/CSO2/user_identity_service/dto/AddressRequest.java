package com.CSO2.user_identity_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequest {
    private String label;
    private String streetAddress;
    private String city;
    private String postalCode;
    private String country;
    private Boolean isDefaultShipping;
    private Boolean isDefaultBilling;
}
