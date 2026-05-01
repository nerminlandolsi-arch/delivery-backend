package com.delivery.controller;

import com.delivery.dto.request.RequestDTOs.*;
import com.delivery.dto.response.ResponseDTOs.*;
import com.delivery.entity.User;
import com.delivery.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@RestController
@RequestMapping("/livreur")
@RequiredArgsConstructor
@Tag(name = "Livreur", description = "Endpoints pour l'application mobile livreur")
public class LivreurController {

    private final ColisService colisService;
    private final PositionService positionService;
    private final NotificationService notificationService;
    private final StatistiquesService statistiquesService;
    private final LivreurService livreurService;

    // ---- COLIS ----

    @GetMapping("/colis")
    @Operation(summary = "Mes colis assignés")
    public ResponseEntity<ApiResponse<List<ColisResponse>>> mesColis(
            @AuthenticationPrincipal User livreur) {
        return ResponseEntity.ok(ApiResponse.ok(colisService.getMesColisByLivreur(livreur.getId())));
    }

    @GetMapping("/colis/en-cours")
    @Operation(summary = "Mes colis en cours de livraison")
    public ResponseEntity<ApiResponse<List<ColisResponse>>> mesColisEnCours(
            @AuthenticationPrincipal User livreur) {
        return ResponseEntity.ok(ApiResponse.ok(colisService.getColisEnCoursForLivreur(livreur.getId())));
    }

    @GetMapping("/colis/{id}")
    @Operation(summary = "Détail d'un colis")
    public ResponseEntity<ApiResponse<ColisResponse>> getColisById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(colisService.getColisById(id)));
    }

    @PatchMapping("/colis/{id}/status")
    @Operation(summary = "Mettre à jour le statut d'un colis")
    public ResponseEntity<ApiResponse<ColisResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request,
            @AuthenticationPrincipal User livreur) {
        ColisResponse colis = colisService.updateStatus(id, request, livreur);
        return ResponseEntity.ok(ApiResponse.ok("Statut mis à jour", colis));
    }

    @PostMapping("/colis/{id}/photo-preuve")
    @Operation(summary = "Uploader la photo de preuve de livraison")
    public ResponseEntity<ApiResponse<ColisResponse>> uploadPhotoPreuve(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User livreur) {
        ColisResponse colis = colisService.uploadPhotoPreuve(id, file, livreur);
        return ResponseEntity.ok(ApiResponse.ok("Photo ajoutée avec succès", colis));
    }

    // ---- GPS POSITION ----

    @PostMapping("/position")
    @Operation(summary = "Envoyer ma position GPS")
    public ResponseEntity<ApiResponse<PositionResponse>> updatePosition(
            @Valid @RequestBody UpdatePositionRequest request,
            @AuthenticationPrincipal User livreur) {
        PositionResponse pos = positionService.updatePosition(livreur.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Position mise à jour", pos));
    }

    // ---- NOTIFICATIONS ----

    @GetMapping("/notifications")
    @Operation(summary = "Mes notifications")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotifications(
            @AuthenticationPrincipal User livreur) {
        List<NotificationResponse> notifs = notificationService.getNotificationsUser(livreur.getId())
                .stream().map(n -> NotificationResponse.builder()
                        .id(n.getId())
                        .titre(n.getTitre())
                        .message(n.getMessage())
                        .lue(n.isLue())
                        .type(n.getType())
                        .referenceId(n.getReferenceId())
                        .createdAt(n.getCreatedAt())
                        .build())
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(notifs));
    }

    @GetMapping("/notifications/non-lues/count")
    @Operation(summary = "Nombre de notifications non lues")
    public ResponseEntity<ApiResponse<Long>> getNombreNonLues(@AuthenticationPrincipal User livreur) {
        return ResponseEntity.ok(ApiResponse.ok(notificationService.getNombreNonLues(livreur.getId())));
    }

    @PatchMapping("/notifications/{id}/lue")
    @Operation(summary = "Marquer une notification comme lue")
    public ResponseEntity<ApiResponse<Void>> marquerLue(
            @PathVariable Long id,
            @AuthenticationPrincipal User livreur) {
        notificationService.marquerCommeLue(id, livreur.getId());
        return ResponseEntity.ok(ApiResponse.ok("Notification marquée comme lue"));
    }

    @PatchMapping("/notifications/toutes-lues")
    @Operation(summary = "Marquer toutes les notifications comme lues")
    public ResponseEntity<ApiResponse<Void>> marquerToutesLues(@AuthenticationPrincipal User livreur) {
        notificationService.marquerToutesCommeLues(livreur.getId());
        return ResponseEntity.ok(ApiResponse.ok("Toutes les notifications marquées comme lues"));
    }

    // ---- PROFIL ----

    @GetMapping("/profil")
    @Operation(summary = "Mon profil")
    public ResponseEntity<ApiResponse<UserResponse>> monProfil(@AuthenticationPrincipal User livreur) {
        return ResponseEntity.ok(ApiResponse.ok(AuthService.mapToUserResponse(livreur)));
    }

    @PutMapping("/profil")
    @Operation(summary = "Modifier mon profil")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfil(
            @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal User livreur) {
        UserResponse updated = livreurService.updateLivreur(livreur.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Profil mis à jour", updated));
    }

    @PostMapping("/profil/photo")
    @Operation(summary = "Modifier ma photo de profil")
    public ResponseEntity<ApiResponse<UserResponse>> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User livreur) {
        return ResponseEntity.ok(ApiResponse.ok("Photo mise à jour",
                livreurService.uploadPhoto(livreur.getId(), file)));
    }

    @PatchMapping("/fcm-token")
    @Operation(summary = "Mettre à jour le token FCM pour les notifications push")
    public ResponseEntity<ApiResponse<Void>> updateFcmToken(
            @Valid @RequestBody UpdateFcmTokenRequest request,
            @AuthenticationPrincipal User livreur) {
        livreurService.updateFcmToken(livreur.getId(), request.getFcmToken());
        return ResponseEntity.ok(ApiResponse.ok("Token FCM mis à jour"));
    }

    // ---- STATISTIQUES ----

    @GetMapping("/statistiques")
    @Operation(summary = "Mes statistiques de livraison")
    public ResponseEntity<ApiResponse<StatistiquesResponse>> mesStatistiques(
            @AuthenticationPrincipal User livreur) {
        return ResponseEntity.ok(ApiResponse.ok(statistiquesService.getStatistiquesLivreur(livreur.getId())));
    }

    @GetMapping("/colis/scan/{codeBarres}")
    @Operation(summary = "Scanner un colis par code-barres")
    public ResponseEntity<ApiResponse<ColisResponse>> scanColis(
            @PathVariable String codeBarres) {
        ColisResponse colis = colisService.findByCodeBarres(codeBarres);
        return ResponseEntity.ok(ApiResponse.ok("Colis trouve", colis));
    }


}
