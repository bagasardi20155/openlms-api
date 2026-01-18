package com.openlms.api.commons.securities;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final long expMinutes;

    public JwtService(
        JwtEncoder jwtEncoder,
        @Value("${app.security.jwt.issuer}") String issuer,
        @Value("${app.security.jwt.exp-minutes}") long expMinutes
    ) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.expMinutes = expMinutes;
    }

    public String issueToken(String userId, String email, String role, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expMinutes * 60);

        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(exp)
                .subject(userId)
                .claim("email", email)
                .claim("role", role);

        if (extraClaims != null) {
            extraClaims.forEach(claims::claim);
        }

        return jwtEncoder.encode(JwtEncoderParameters.from(claims.build())).getTokenValue();
    }
}
