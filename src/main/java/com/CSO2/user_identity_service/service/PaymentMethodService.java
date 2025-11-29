package com.CSO2.user_identity_service.service;

import com.CSO2.user_identity_service.dto.PaymentMethodDTO;
import com.CSO2.user_identity_service.dto.PaymentMethodRequest;
import com.CSO2.user_identity_service.model.PaymentMethod;
import com.CSO2.user_identity_service.model.User;
import com.CSO2.user_identity_service.repository.PaymentMethodRepository;
import com.CSO2.user_identity_service.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;

    public PaymentMethodService(PaymentMethodRepository paymentMethodRepository, UserRepository userRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.userRepository = userRepository;
    }

    public PaymentMethodDTO addPaymentMethod(String userId, PaymentMethodRequest req) {
        return createPaymentMethod(userId, req);
    }

    public PaymentMethodDTO addPaymentMethodByEmail(String email, PaymentMethodRequest req) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return createPaymentMethod(user.getId(), req);
    }

    private PaymentMethodDTO createPaymentMethod(String userId, PaymentMethodRequest req) {
        // If this is being set as default, unset other defaults
        if (req.getIsDefault() != null && req.getIsDefault()) {
            paymentMethodRepository.findByUserId(userId).forEach(pm -> {
                pm.setIsDefault(false);
                paymentMethodRepository.save(pm);
            });
        }

        PaymentMethod paymentMethod = PaymentMethod.builder()
                .userId(userId)
                .provider(req.getProvider())
                .lastFourDigits(req.getLastFourDigits())
                .token(req.getToken())
                .expiryDate(req.getExpiryDate())
                .isDefault(req.getIsDefault() != null ? req.getIsDefault() : false)
                .build();

        paymentMethodRepository.save(paymentMethod);
        return mapToPaymentMethodDTO(paymentMethod);
    }

    public List<PaymentMethodDTO> getUserPaymentMethods(String userId) {
        return paymentMethodRepository.findByUserId(userId).stream()
                .map(this::mapToPaymentMethodDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentMethodDTO> getUserPaymentMethodsByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return getUserPaymentMethods(user.getId());
    }

    public void deletePaymentMethod(String userId, String paymentMethodId) {
        PaymentMethod paymentMethod = paymentMethodRepository.findByUserIdAndId(userId, paymentMethodId)
                .orElseThrow(() -> new RuntimeException("Payment method not found or does not belong to user"));
        paymentMethodRepository.delete(paymentMethod);
    }

    public void deletePaymentMethodByEmail(String email, String paymentMethodId) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        deletePaymentMethod(user.getId(), paymentMethodId);
    }

    public PaymentMethodDTO updatePaymentMethod(String userId, String paymentMethodId, PaymentMethodRequest req) {
        PaymentMethod paymentMethod = paymentMethodRepository.findByUserIdAndId(userId, paymentMethodId)
                .orElseThrow(() -> new RuntimeException("Payment method not found or does not belong to user"));

        // If this is being set as default, unset other defaults
        if (req.getIsDefault() != null && req.getIsDefault()) {
            paymentMethodRepository.findByUserId(userId).forEach(pm -> {
                if (!pm.getId().equals(paymentMethodId)) {
                    pm.setIsDefault(false);
                    paymentMethodRepository.save(pm);
                }
            });
        }

        if (req.getProvider() != null)
            paymentMethod.setProvider(req.getProvider());
        if (req.getLastFourDigits() != null)
            paymentMethod.setLastFourDigits(req.getLastFourDigits());
        if (req.getToken() != null)
            paymentMethod.setToken(req.getToken());
        if (req.getExpiryDate() != null)
            paymentMethod.setExpiryDate(req.getExpiryDate());
        if (req.getIsDefault() != null)
            paymentMethod.setIsDefault(req.getIsDefault());

        paymentMethodRepository.save(paymentMethod);
        return mapToPaymentMethodDTO(paymentMethod);
    }

    public PaymentMethodDTO updatePaymentMethodByEmail(String email, String paymentMethodId, PaymentMethodRequest req) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return updatePaymentMethod(user.getId(), paymentMethodId, req);
    }

    private PaymentMethodDTO mapToPaymentMethodDTO(PaymentMethod paymentMethod) {
        return PaymentMethodDTO.builder()
                .id(paymentMethod.getId())
                .provider(paymentMethod.getProvider())
                .lastFourDigits(paymentMethod.getLastFourDigits())
                .expiryDate(paymentMethod.getExpiryDate())
                .isDefault(paymentMethod.getIsDefault())
                .build();
    }
}
