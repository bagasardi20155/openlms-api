package com.openlms.api.classroom.helpers;

import org.springframework.security.oauth2.jwt.Jwt;

import com.openlms.api.commons.exceptions.DomainException;
import com.openlms.api.commons.exceptions.ErrorCode;

public class RequireRole {
    public static void requireRole(Jwt jwt, String requiredRole) {
        String role = JwtHelper.role(jwt);
        if (!requiredRole.equals(role)) {
            throw new DomainException(ErrorCode.FORBIDDEN, "Forbidden");
        }
    }
}
