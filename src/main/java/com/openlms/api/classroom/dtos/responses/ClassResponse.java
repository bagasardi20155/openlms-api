package com.openlms.api.classroom.dtos.responses;

import java.util.UUID;

public record ClassResponse(
    UUID id,
    UUID teacherId,
    String title,
    String description,
    boolean published
) {

}
