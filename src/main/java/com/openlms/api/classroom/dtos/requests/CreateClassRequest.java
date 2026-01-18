package com.openlms.api.classroom.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateClassRequest(
    @NotBlank @Size(min = 3, max = 255) String title,
    String description
) {

}
