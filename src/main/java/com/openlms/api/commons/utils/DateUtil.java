package com.openlms.api.commons.utils;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public final class DateUtil {
    private DateUtil() {}

    public static Instant toInstant(OffsetDateTime odt) {
        return odt == null ? null : odt.toInstant();
    }

    public static OffsetDateTime toOffsetDateTime(Instant instant) {
        return instant == null ? null : instant.atOffset(ZoneOffset.UTC);
    }

    public static ZonedDateTime toZonedDateTime(Instant instant, String zoneId) {
        if (instant == null) return null;
        return instant.atZone(ZoneId.of(zoneId));
    }

    public static OffsetDateTime nowUtc() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }
}
