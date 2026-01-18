package com.openlms.api.classroom.dtos.requests;

public record UpdateMaterialRequest(
    String title,
    String content,
    String path,
    Integer position,
    Boolean published
) {

}
