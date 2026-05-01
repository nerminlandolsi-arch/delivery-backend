package com.delivery.controller;

import com.delivery.dto.request.RequestDTOs.*;
import com.delivery.dto.response.ResponseDTOs.*;
import com.delivery.service.VehiculeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin/vehicules")
@RequiredArgsConstructor
public class VehiculeController {

    private final VehiculeService vehiculeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<VehiculeResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(vehiculeService.getAllVehicules()));
    }

    @GetMapping("/livreur/{livreurId}")
    public ResponseEntity<ApiResponse<VehiculeResponse>> getByLivreur(
            @PathVariable Long livreurId) {
        return ResponseEntity.ok(ApiResponse.ok(
                vehiculeService.getVehiculeByLivreur(livreurId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VehiculeResponse>> create(
            @RequestBody VehiculeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Vehicule cree",
                vehiculeService.creerVehicule(request)));
    }

    @PatchMapping("/{id}/assigner/{livreurId}")
    public ResponseEntity<ApiResponse<VehiculeResponse>> assigner(
            @PathVariable Long id, @PathVariable Long livreurId) {
        return ResponseEntity.ok(ApiResponse.ok("Livreur assigne",
                vehiculeService.assignerLivreur(id, livreurId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        vehiculeService.supprimerVehicule(id);
        return ResponseEntity.ok(ApiResponse.ok("Vehicule supprime"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VehiculeResponse>> update(
            @PathVariable Long id,
            @RequestBody VehiculeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Vehicule mis a jour",
                vehiculeService.updateVehicule(id, request)));
    }
}