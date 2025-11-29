package com.CSO2.user_identity_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment_methods")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String userId;

    private String provider;

    private String lastFourDigits;

    private String token;

    private String expiryDate;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDefault = false;
}
