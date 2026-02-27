package com.openlms.api.payment.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openlms.api.payment.dtos.responses.PaymentResponse;
import com.openlms.api.payment.services.PaymentService;

@RestController
@RequestMapping("/api/payment/webhook")
public class PaymentWebhookController {
    @Value("${xendit.webhook-token}")
    private String webhookToken;

    private final PaymentService paymentService;
    public PaymentWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/invoice")
    public ResponseEntity<PaymentResponse> handleInvoiceCallback(
        @RequestHeader("x-callback-token") String callbackToken,
        @RequestBody Map<String, Object> request
    ) {
        PaymentResponse response = paymentService.handleInvoiceCallback(callbackToken, request);
        return ResponseEntity.ok(response);
    }
}
