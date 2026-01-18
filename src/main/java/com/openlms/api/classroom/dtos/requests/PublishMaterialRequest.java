package com.openlms.api.classroom.dtos.requests;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record PublishMaterialRequest(
    @NotNull UUID materialId
) {

}
