package com.delivery.controller;

import com.delivery.dto.request.RequestDTOs.*;
import com.delivery.dto.response.ResponseDTOs.*;
import com.delivery.service.LivreurService;
import com.delivery.service.StatistiquesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin/livreurs")
@RequiredArgsConstructor
@Tag(name = "Admin - Livreurs", description = "Gestion des livreurs (Admin)")
public class AdminLivreurController {

    private final LivreurService livreurService;
    private final StatistiquesService statistiquesService;

    @GetMapping
    @Operation(summary = "Lister tous les livreurs")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllLivreurs(
            @RequestParam(defaultValue = "false") boolean actifsOnly) {
        List<UserResponse> livreurs = actifsOnly ?
                livreurService.getLivreursActifs() : livreurService.getAllLivreurs();
        return ResponseEntity.ok(ApiResponse.ok(livreurs));
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau livreur")
    public ResponseEntity<ApiResponse<UserResponse>> creerLivreur(
            @Valid @RequestBody RegisterLivreurRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Livreur créé avec succès",
                livreurService.creerLivreur(request)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un livreur par ID")
    public ResponseEntity<ApiResponse<UserResponse>> getLivreurById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(livreurService.getLivreurById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un livreur")
    public ResponseEntity<ApiResponse<UserResponse>> updateLivreur(
            @PathVariable Long id,
            @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Livreur mis à jour", livreurService.updateLivreur(id, request)));
    }

    @PatchMapping("/{id}/toggle-actif")
    @Operation(summary = "Activer/désactiver un livreur")
    public ResponseEntity<ApiResponse<UserResponse>> toggleActif(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Statut mis à jour", livreurService.toggleActif(id)));
    }

    @PostMapping("/{id}/photo")
    @Operation(summary = "Uploader la photo d'un livreur")
    public ResponseEntity<ApiResponse<UserResponse>> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.ok("Photo mise à jour", livreurService.uploadPhoto(id, file)));
    }

    @GetMapping("/{id}/statistiques")
    @Operation(summary = "Statistiques d'un livreur")
    public ResponseEntity<ApiResponse<StatistiquesResponse>> getStatistiquesLivreur(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(statistiquesService.getStatistiquesLivreur(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un livreur")
    public ResponseEntity<ApiResponse<Void>> deleteLivreur(@PathVariable Long id) {
        livreurService.deleteLivreur(id);
        return ResponseEntity.ok(ApiResponse.ok("Livreur supprimé avec succès"));
    }
}
