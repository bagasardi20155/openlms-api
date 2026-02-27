package com.openlms.api.payment.controllers;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openlms.api.payment.services.PaymentService;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> checkout(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        double amount = 1000000;

        Map<String, Object> invoice = paymentService.createInvoice(userId, "VIP", amount);

        String paymentUrl = (String) invoice.get("invoice_url");
        return ResponseEntity.ok(Map.of("payment_url", paymentUrl));
    }
}
