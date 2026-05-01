package com.delivery.controller;

import com.delivery.dto.request.RequestDTOs.*;
import com.delivery.dto.response.ResponseDTOs.*;
import com.delivery.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Login, register, refresh token")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Connexion (Admin ou Livreur)")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Connexion réussie", response));
    }

    @PostMapping("/register/livreur")
    @Operation(summary = "Inscription d'un nouveau livreur")
    public ResponseEntity<ApiResponse<AuthResponse>> registerLivreur(
            @Valid @RequestBody RegisterLivreurRequest request) {
        AuthResponse response = authService.registerLivreur(request);
        return ResponseEntity.status(201).body(ApiResponse.ok("Compte créé avec succès", response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Rafraîchir le token JWT")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @RequestHeader("Refresh-Token") String refreshToken) {
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.ok("Token rafraîchi", response));
    }
}
