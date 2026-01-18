package com.openlms.api.classroom.helpers;

import java.util.UUID;

import org.springframework.security.oauth2.jwt.Jwt;

public final class JwtHelper {
    private JwtHelper () {}

    public static UUID userId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }

    public static String role(Jwt jwt) {
        return jwt.getClaimAsString("role");
    }
}
