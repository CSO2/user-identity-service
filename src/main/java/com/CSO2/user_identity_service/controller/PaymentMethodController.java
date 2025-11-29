package com.CSO2.user_identity_service.controller;

import com.CSO2.user_identity_service.dto.response.PaymentMethodDTO;
import com.CSO2.user_identity_service.dto.request.PaymentMethodRequest;
import com.CSO2.user_identity_service.service.PaymentMethodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/me/payment-methods")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    public PaymentMethodController(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    @GetMapping
    public ResponseEntity<List<PaymentMethodDTO>> getUserPaymentMethods(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(paymentMethodService.getUserPaymentMethodsByEmail(userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<PaymentMethodDTO> addPaymentMethod(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PaymentMethodRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentMethodService.addPaymentMethodByEmail(userDetails.getUsername(), req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentMethodDTO> updatePaymentMethod(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id,
            @RequestBody PaymentMethodRequest req) {
        return ResponseEntity.ok(paymentMethodService.updatePaymentMethodByEmail(userDetails.getUsername(), id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentMethod(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {
        paymentMethodService.deletePaymentMethodByEmail(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}
