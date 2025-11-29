package com.CSO2.user_identity_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "addresses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String userId;

    private String label;

    private String streetAddress;

    private String city;

    private String postalCode;

    private String country;

    private Boolean isDefaultShipping;

    private Boolean isDefaultBilling;
}
