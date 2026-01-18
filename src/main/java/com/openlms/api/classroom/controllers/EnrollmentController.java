package com.openlms.api.classroom.controllers;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openlms.api.classroom.dtos.responses.EnrollmentResponse;
import com.openlms.api.classroom.services.EnrollmentService;
import com.openlms.api.commons.apis.ApiResponse;

@RestController
@RequestMapping("/api/class")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/{classId}/enroll")
    public ApiResponse<EnrollmentResponse> enroll(
        @AuthenticationPrincipal Jwt jwt, 
        @PathVariable UUID classId
    ) {
        return ApiResponse.ok(enrollmentService.enroll(classId, jwt));
    }
}
