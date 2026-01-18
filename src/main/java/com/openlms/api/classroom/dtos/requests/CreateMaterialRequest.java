package com.openlms.api.classroom.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMaterialRequest(
    @NotBlank String title,
    @NotNull String contentType,
    String content,
    String path, // should be s3
    Integer position,
    Boolean published
) {

}
