package com.openlms.api.commons.events;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EnrollmentCreatedEvent(
    UUID enrollmentId,
    UUID classId,
    UUID studentId,
    String studentEmail,
    String classTitle,
    OffsetDateTime enrolledAt
) {

}
