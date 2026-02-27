package com.openlms.api.payment.dtos.responses;

import java.util.UUID;

public record PaymentResponse(
    UUID id,
    String email,
    String role,
    String status
) {

}