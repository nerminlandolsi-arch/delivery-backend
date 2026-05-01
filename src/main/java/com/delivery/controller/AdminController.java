package com.delivery.controller;

import com.delivery.dto.response.ResponseDTOs.*;
import com.delivery.service.PositionService;
import com.delivery.service.StatistiquesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin - Dashboard", description = "Tableau de bord et suivi GPS (Admin)")
public class AdminController {

    private final StatistiquesService statistiquesService;
    private final PositionService positionService;

    @GetMapping("/statistiques")
    @Operation(summary = "Statistiques globales de la plateforme")
    public ResponseEntity<ApiResponse<StatistiquesResponse>> getStatistiquesGlobales() {
        return ResponseEntity.ok(ApiResponse.ok(statistiquesService.getStatistiquesGlobales()));
    }

    @GetMapping("/positions")
    @Operation(summary = "Dernières positions de tous les livreurs actifs")
    public ResponseEntity<ApiResponse<List<PositionResponse>>> getDernieresPositions() {
        return ResponseEntity.ok(ApiResponse.ok(positionService.getDernieresPositionsTousLivreurs()));
    }

    @GetMapping("/positions/{livreurId}")
    @Operation(summary = "Dernière position d'un livreur spécifique")
    public ResponseEntity<ApiResponse<PositionResponse>> getDernierePosition(
            @PathVariable Long livreurId) {
        return ResponseEntity.ok(ApiResponse.ok(positionService.getDernierePosition(livreurId)));
    }
}
