package com.openlms.api.payment.services;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.openlms.api.auth.domains.User;
import com.openlms.api.auth.domains.VipStatus;
import com.openlms.api.auth.repositories.UserRepository;
import com.openlms.api.commons.exceptions.DomainException;
import com.openlms.api.commons.exceptions.ErrorCode;
import com.openlms.api.commons.utils.DateUtil;
import com.openlms.api.payment.dtos.responses.PaymentResponse;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class PaymentService {
    @Value("${xendit.base-url}")
    private String baseUrl;
    
    @Value("${xendit.webhook-token}")
    private String webhookToken;
    
    @Autowired
    private RestTemplate xenditRestTemplate;
    
    private final UserRepository userRepository;
    public PaymentService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private PaymentResponse mapToResponse(User user, String paymentStatus) {
        return new PaymentResponse(
            user.getId(),
            user.getEmail(),
            user.getRole().name(),
            paymentStatus
        );
    }

    public Map<String, Object> createInvoice(UUID userId, String plan, double amount) {
        if (!userRepository.existsById(userId)) {
            throw new DomainException(ErrorCode.NOT_FOUND, "User Not Found");
        }

        if (userRepository.findById(userId).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "User Not Found")).getVipStatus() == VipStatus.VIP) {
            throw new DomainException(ErrorCode.BAD_REQUEST, "User already VIP");
        }

        String externalId = "SUB_" + userId + "_" + System.currentTimeMillis();
        Map<String, Object> body = new HashMap<>();
        body.put("external_id", externalId);
        body.put("amount", amount);
        body.put("description", "Subscription - " + plan);
        body.put("currency", "IDR");
        body.put("expiration_date", DateUtil.nowUtc().plusSeconds(15 * 60).toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = xenditRestTemplate.postForEntity(
            baseUrl + "/v2/invoices", request, Map.class
        );

        return response.getBody();
    }

    public PaymentResponse handleInvoiceCallback(String callbackToken, Map<String, Object> request) {
        if (!callbackToken.equals(webhookToken)) {
            throw new RuntimeException("Invalid callback token");
        }

        String externalId = (String) request.get("external_id");
        String status = (String) request.get("status");
        String description = (String) request.get("description");
        String plan = ((String) request.get("description")).replace("Subscription - ", "");

        if ("PAID".equals(status)) {
            UUID userId = extractUserId(externalId);
            // should be in another callback, then set in Pembayaran Berhasil callback in xendit
            activateSubscription(userId, plan);
            return mapToResponse(userRepository.findById(userId).orElse(null), description);
        } else {
            throw new RuntimeException("Payment failed or pending");
        }
    }

    private UUID extractUserId(String externalId) {
        String[] parts = externalId.split("_");
        return UUID.fromString(parts[1]);
    }

    @Transactional
    public void activateSubscription(UUID userId, String plan) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setVipStatus(VipStatus.VIP);
        user.setVipExpiresAt(OffsetDateTime.now().plusMonths(60));
        userRepository.save(user);
    }
}
