package com.openlms.api.commons.utils;

import java.util.Map;

import org.springframework.security.oauth2.jwt.Jwt;

public class JwtClaimsUtil {
    private JwtClaimsUtil() {}

    public static String requireString(Jwt jwt, String claim) {
        String val = jwt.getClaimAsString(claim);
        if (val == null || val.isBlank()) throw new IllegalArgumentException("Missing claim: " + claim);
        return val;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getMap(Jwt jwt, String claim) {
        Object obj = jwt.getClaim(claim);
        return (obj instanceof Map<?, ?> m) ? (Map<String, Object>) m : null;
    }
}
