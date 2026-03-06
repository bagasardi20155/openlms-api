package com.openlms.api.commons.events;

import java.util.UUID;

public record MaterialCompletedEvent(
    UUID materialId,
    UUID classId,
    UUID studentId,
    String studentEmail,
    String classTitle,
    String materialTitle
) {

}
