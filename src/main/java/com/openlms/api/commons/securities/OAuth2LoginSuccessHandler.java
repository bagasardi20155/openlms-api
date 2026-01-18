package com.openlms.api.commons.securities;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final String frontendCallbackUrl;

    public OAuth2LoginSuccessHandler(
        JwtService jwtService,
        @Value("${app.security.frontend-callback-url}") String frontendCallbackUrl
    ) {
        this.jwtService = jwtService;
        this.frontendCallbackUrl = frontendCallbackUrl;
    }

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication) throws IOException {

        // For Google OIDC, principal is usually OidcUser
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof OidcUser oidcUser)) {
            response.sendRedirect(frontendCallbackUrl + "?error=unsupported_principal");
            return;
        }

        String email = oidcUser.getEmail();
        String googleSub = oidcUser.getSubject();

        // TODO: upsert user + user_identity in DB here (or call an AuthService)
        // For now: issue token with placeholder userId/role
        String userId = "TODO_DB_USER_ID";
        String role = "STUDENT";

        String token = jwtService.issueToken(
                userId,
                email,
                role,
                Map.of("provider", "GOOGLE", "google_sub", googleSub)
        );

        String redirect = UriComponentsBuilder.fromUriString(frontendCallbackUrl)
                .queryParam("token", token)
                .build()
                .toUriString();

        response.sendRedirect(redirect);
    }
}
