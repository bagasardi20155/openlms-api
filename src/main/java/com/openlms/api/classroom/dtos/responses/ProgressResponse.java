package com.openlms.api.classroom.dtos.responses;

public record ProgressResponse(
    double totalMaterials,
    double completedMaterials,
    double progressPercent
) {

}
