package com.openlms.api.classroom.dtos.responses;

import java.util.UUID;

public record MaterialResponse(
    UUID id,
    String title,
    String contentType,
    String content,
    String path,
    int position
) {

}
