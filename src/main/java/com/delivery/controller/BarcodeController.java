package com.delivery.controller;

import com.delivery.dto.response.ResponseDTOs.*;
import com.delivery.service.ColisService;
import com.delivery.util.BarcodeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/colis")
@RequiredArgsConstructor
@Tag(name = "Admin - Codes-barres", description = "Scan et recherche par code-barres")
public class BarcodeController {

    private final ColisService colisService;
    private final BarcodeUtil barcodeUtil;

    @GetMapping("/scan/{codeBarres}")
    @Operation(summary = "Trouver un colis par son code-barres")
    public ResponseEntity<ApiResponse<ColisResponse>> findByCodeBarres(
            @PathVariable String codeBarres) {
        ColisResponse colis = colisService.findByCodeBarres(codeBarres);
        return ResponseEntity.ok(ApiResponse.ok("Colis trouve", colis));
    }

    @GetMapping("/{id}/barcode")
    @Operation(summary = "Obtenir l'image du code-barres d'un colis")
    public ResponseEntity<ApiResponse<String>> getBarcodeImage(
            @PathVariable Long id) {
        String barcodeBase64 = colisService.getBarcodeImage(id);
        return ResponseEntity.ok(ApiResponse.ok("Code-barres genere", barcodeBase64));
    }

    @GetMapping("/scan/livreur/{codeBarres}")
    @Operation(summary = "Livreur - Trouver un colis par code-barres")
    public ResponseEntity<ApiResponse<ColisResponse>> scanByLivreur(
            @PathVariable String codeBarres) {
        ColisResponse colis = colisService.findByCodeBarres(codeBarres);
        return ResponseEntity.ok(ApiResponse.ok("Colis trouve", colis));
    }
}
