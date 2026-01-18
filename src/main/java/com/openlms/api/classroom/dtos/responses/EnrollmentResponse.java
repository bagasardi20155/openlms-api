package com.openlms.api.classroom.dtos.responses;

import java.util.UUID;

public record EnrollmentResponse(
    UUID enrollmentId,
    UUID classId,
    UUID studentId,
    String status
) {

}
