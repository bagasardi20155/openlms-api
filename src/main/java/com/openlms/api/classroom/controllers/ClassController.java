package com.openlms.api.classroom.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openlms.api.classroom.dtos.requests.CreateClassRequest;
import com.openlms.api.classroom.dtos.requests.PublishClassRequest;
import com.openlms.api.classroom.dtos.responses.ClassResponse;
import com.openlms.api.classroom.services.ClassService;
import com.openlms.api.commons.apis.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/class")
public class ClassController {
    private final ClassService classService;
    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    @PostMapping
    public ApiResponse<ClassResponse> create(
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody @Valid CreateClassRequest request
    ) {
        return ApiResponse.ok(classService.createClass(jwt, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<ClassResponse> get(
        @AuthenticationPrincipal Jwt jwt, 
        @PathVariable UUID id
    ) {
        return ApiResponse.ok(classService.getClass(id, jwt));
    }

    @GetMapping("/taught-classes")
    public ApiResponse<List<ClassResponse>> taughtClasses(
        @AuthenticationPrincipal Jwt jwt
    ) {
        return ApiResponse.ok(classService.taughtClasses(jwt));
    }

    @PostMapping("/publish")
    public ApiResponse<ClassResponse> publish(
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody @Valid PublishClassRequest request
    ) {
        return ApiResponse.ok(classService.publish(jwt, request));
    }
}
