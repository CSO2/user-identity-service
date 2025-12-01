package com.CSO2.user_identity_service.service;

import com.CSO2.user_identity_service.dto.request.PaymentMethodRequest;
import com.CSO2.user_identity_service.dto.response.PaymentMethodDTO;
import com.CSO2.user_identity_service.model.PaymentMethod;
import com.CSO2.user_identity_service.model.User;
import com.CSO2.user_identity_service.repository.PaymentMethodRepository;
import com.CSO2.user_identity_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentMethodServiceTest {

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PaymentMethodService paymentMethodService;

    private User user;
    private PaymentMethod paymentMethod;
    private PaymentMethodRequest paymentMethodRequest;

    @BeforeEach
    void setUp() {
        user = User.builder().id("userId").email("test@example.com").build();
        paymentMethod = PaymentMethod.builder()
                .id("paymentMethodId")
                .userId("userId")
                .provider("Visa")
                .lastFourDigits("1234")
                .token("token")
                .expiryDate("12/25")
                .isDefault(true)
                .build();
        paymentMethodRequest = new PaymentMethodRequest();
        paymentMethodRequest.setProvider("Visa");
        paymentMethodRequest.setLastFourDigits("1234");
        paymentMethodRequest.setToken("token");
        paymentMethodRequest.setExpiryDate("12/25");
        paymentMethodRequest.setIsDefault(true);
    }

    @Test
    void addPaymentMethodByEmail_ShouldReturnPaymentMethodDTO_WhenUserExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(paymentMethodRepository.findByUserId("userId")).thenReturn(Collections.emptyList());
        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(paymentMethod);

        PaymentMethodDTO result = paymentMethodService.addPaymentMethodByEmail("test@example.com",
                paymentMethodRequest);

        assertNotNull(result);
        assertEquals("Visa", result.getProvider());
        verify(paymentMethodRepository).save(any(PaymentMethod.class));
    }

    @Test
    void getUserPaymentMethodsByEmail_ShouldReturnListOfPaymentMethodDTO_WhenUserExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(paymentMethodRepository.findByUserId("userId")).thenReturn(Collections.singletonList(paymentMethod));

        List<PaymentMethodDTO> result = paymentMethodService.getUserPaymentMethodsByEmail("test@example.com");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Visa", result.get(0).getProvider());
    }

    @Test
    void updatePaymentMethodByEmail_ShouldReturnPaymentMethodDTO_WhenPaymentMethodExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(paymentMethodRepository.findByUserIdAndId("userId", "paymentMethodId"))
                .thenReturn(Optional.of(paymentMethod));
        when(paymentMethodRepository.findByUserId("userId")).thenReturn(Collections.singletonList(paymentMethod));
        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(paymentMethod);

        PaymentMethodDTO result = paymentMethodService.updatePaymentMethodByEmail("test@example.com", "paymentMethodId",
                paymentMethodRequest);

        assertNotNull(result);
        assertEquals("Visa", result.getProvider());
        verify(paymentMethodRepository, atLeastOnce()).save(any(PaymentMethod.class));
    }

    @Test
    void deletePaymentMethodByEmail_ShouldDeletePaymentMethod_WhenPaymentMethodExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(paymentMethodRepository.findByUserIdAndId("userId", "paymentMethodId"))
                .thenReturn(Optional.of(paymentMethod));

        paymentMethodService.deletePaymentMethodByEmail("test@example.com", "paymentMethodId");

        verify(paymentMethodRepository).delete(paymentMethod);
    }
}
