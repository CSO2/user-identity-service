package com.CSO2.user_identity_service.repository;

import com.CSO2.user_identity_service.model.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, String> {
    List<PaymentMethod> findByUserId(String userId);

    Optional<PaymentMethod> findByUserIdAndId(String userId, String id);
}
