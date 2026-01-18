package com.openlms.api.classroom.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openlms.api.classroom.dtos.requests.CreateMaterialRequest;
import com.openlms.api.classroom.dtos.requests.UpdateMaterialRequest;
import com.openlms.api.classroom.dtos.responses.MaterialResponse;
import com.openlms.api.classroom.dtos.responses.ProgressResponse;
import com.openlms.api.classroom.services.MaterialService;
import com.openlms.api.commons.apis.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/class/{classId}/material")
public class MaterialController {
    private final MaterialService materialService;
    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @GetMapping
    public ApiResponse<List<MaterialResponse>> list(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID classId
    ) {
        return ApiResponse.ok(materialService.listMaterials(classId, jwt));
    }

    @PostMapping
    public ApiResponse<MaterialResponse> create(
        @PathVariable UUID classId,
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody @Valid CreateMaterialRequest request
    ) {
        return ApiResponse.ok(materialService.create(classId, jwt, request));
    }

    @PatchMapping("/{materialId}")
    public ApiResponse<MaterialResponse> update(
        @PathVariable UUID classId,
        @PathVariable UUID materialId,
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody UpdateMaterialRequest request
    ) {
        return ApiResponse.ok(materialService.update(classId, materialId, jwt, request));
    }

    @GetMapping("/progress")
    public ApiResponse<ProgressResponse> progress(
        @PathVariable UUID classId,
        @AuthenticationPrincipal Jwt jwt
    ) {
        return ApiResponse.ok(materialService.getProgress(classId, jwt));
    }
}
