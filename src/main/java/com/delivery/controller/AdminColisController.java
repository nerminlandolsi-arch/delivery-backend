package com.delivery.controller;

import com.delivery.dto.request.RequestDTOs.*;
import com.delivery.dto.response.ResponseDTOs.*;
import com.delivery.entity.User;
import com.delivery.service.ColisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin/colis")
@RequiredArgsConstructor
@Tag(name = "Admin - Colis", description = "Gestion complète des colis (Admin)")
public class AdminColisController {

    private final ColisService colisService;

    @PostMapping
    @Operation(summary = "Créer un nouveau colis")
    public ResponseEntity<ApiResponse<ColisResponse>> createColis(
            @Valid @RequestBody CreateColisRequest request,
            @AuthenticationPrincipal User admin) {
        ColisResponse colis = colisService.createColis(request, admin);
        return ResponseEntity.status(201).body(ApiResponse.ok("Colis créé avec succès", colis));
    }

    @GetMapping
    @Operation(summary = "Lister tous les colis")
    public ResponseEntity<ApiResponse<List<ColisResponse>>> getAllColis(
            @RequestParam(required = false) String status) {
        List<ColisResponse> colis = status != null ?
                colisService.getColisByStatus(status) : colisService.getAllColis();
        return ResponseEntity.ok(ApiResponse.ok(colis));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un colis par ID")
    public ResponseEntity<ApiResponse<ColisResponse>> getColisById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(colisService.getColisById(id)));
    }

    @GetMapping("/suivi/{numeroSuivi}")
    @Operation(summary = "Suivre un colis par numéro de suivi")
    public ResponseEntity<ApiResponse<ColisResponse>> getColisByNumeroSuivi(
            @PathVariable String numeroSuivi) {
        return ResponseEntity.ok(ApiResponse.ok(colisService.getColisByNumeroSuivi(numeroSuivi)));
    }

    @PutMapping("/{id}/assigner")
    @Operation(summary = "Assigner un colis à un livreur")
    public ResponseEntity<ApiResponse<ColisResponse>> assignerLivreur(
            @PathVariable Long id,
            @Valid @RequestBody AssignerColisRequest request,
            @AuthenticationPrincipal User admin) {
        ColisResponse colis = colisService.assignerLivreur(id, request, admin);
        return ResponseEntity.ok(ApiResponse.ok("Colis assigné avec succès", colis));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Mettre à jour le statut d'un colis")
    public ResponseEntity<ApiResponse<ColisResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request,
            @AuthenticationPrincipal User admin) {
        ColisResponse colis = colisService.updateStatus(id, request, admin);
        return ResponseEntity.ok(ApiResponse.ok("Statut mis à jour", colis));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un colis")
    public ResponseEntity<ApiResponse<Void>> deleteColis(@PathVariable Long id) {
        colisService.deleteColis(id);
        return ResponseEntity.ok(ApiResponse.ok("Colis supprimé avec succès"));
    }
}
