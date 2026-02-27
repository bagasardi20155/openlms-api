package com.openlms.api.commons.securities;

import java.time.OffsetDateTime;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.openlms.api.commons.exceptions.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
            JwtAuthConverter jwtAuthConverter
    ) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/send-otp").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/verify-otp").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/payment/webhook/**").permitAll()
                .requestMatchers("/oauth2/**", "/login/**").permitAll()
                .anyRequest().authenticated()
            )

            // google oauth2 login flow
            // .oauth2Login(oauth2 -> oauth2
            //     .successHandler(oAuth2LoginSuccessHandler)
            // )

            // bearer jwt validation
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)) // [web:69]
            )

            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
            )
            
            .cors(cors -> {});

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    JwtEncoder jwtEncoder(@Value("${app.security.jwt.secret}") String secret) {
        SecretKeySpec key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        return new NimbusJwtEncoder(new com.nimbusds.jose.jwk.source.ImmutableSecret<>(key));
    }


    @Bean
    JwtDecoder jwtDecoder(
        @Value("${app.security.jwt.secret}") String secret,
        @Value("${app.security.jwt.issuer}") String issuer
    ) {
        var key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));
        return decoder;
    }


    private AuthenticationEntryPoint authenticationEntryPoint() {
        var mapper = new com.fasterxml.jackson.databind.ObjectMapper();

        return (HttpServletRequest request,
                HttpServletResponse response,
                AuthenticationException authException) -> {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            ErrorResponse err = new ErrorResponse(
                    "UNAUTHORIZED",
                    "Unauthorized",
                    null,
                    401,
                    request.getRequestURI(),
                    OffsetDateTime.now(),
                    null
            );

            response.getWriter().write(mapper.writeValueAsString(err));
        };
    }

    private AccessDeniedHandler accessDeniedHandler() {
        var mapper = new com.fasterxml.jackson.databind.ObjectMapper();

        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            ErrorResponse err = new ErrorResponse(
                    "FORBIDDEN",
                    "Forbidden",
                    null,
                    403,
                    request.getRequestURI(),
                    OffsetDateTime.now(),
                    null
            );

            response.getWriter().write(mapper.writeValueAsString(err));
        };
    }
}
