package com.delivery.controller;

import com.delivery.dto.request.RequestDTOs.*;
import com.delivery.dto.response.ResponseDTOs.*;
import com.delivery.entity.User;
import com.delivery.service.PositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/positions")
@RequiredArgsConstructor
@Tag(name = "GPS - Positions", description = "Suivi GPS des livreurs en temps réel")
@SecurityRequirement(name = "bearerAuth")
public class PositionController {

    private final PositionService positionService;

    @PostMapping("/update")
    @Operation(summary = "Mettre à jour sa position (livreur)")
    @PreAuthorize("hasAuthority('ROLE_LIVREUR')")
    public ResponseEntity<ApiResponse<PositionResponse>> updatePosition(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdatePositionRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Position mise à jour",
                positionService.updatePosition(user.getId(), request)));
    }

    @GetMapping("/livreur/{livreurId}")
    @Operation(summary = "Obtenir la dernière position d'un livreur (admin)")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PositionResponse>> getDernierePosition(
            @PathVariable Long livreurId) {
        return ResponseEntity.ok(ApiResponse.ok(
                positionService.getDernierePosition(livreurId)));
    }

    @GetMapping("/tous")
    @Operation(summary = "Obtenir les dernières positions de tous les livreurs actifs (admin)")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<PositionResponse>>> getToutesLesPositions() {
        return ResponseEntity.ok(ApiResponse.ok(
                positionService.getDernieresPositionsTousLivreurs()));
    }
}
