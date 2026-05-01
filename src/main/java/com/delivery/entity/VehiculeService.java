package com.delivery.service;

import com.delivery.dto.request.RequestDTOs.*;
import com.delivery.dto.response.ResponseDTOs.*;
import com.delivery.entity.User;
import com.delivery.entity.Vehicule;
import com.delivery.exception.DeliveryException;
import com.delivery.repository.UserRepository;
import com.delivery.repository.VehiculeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehiculeService {

    private final VehiculeRepository vehiculeRepository;
    private final UserRepository userRepository;

    public List<VehiculeResponse> getAllVehicules() {
        return vehiculeRepository.findAll()
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public VehiculeResponse getVehiculeByLivreur(Long livreurId) {
        Vehicule v = vehiculeRepository.findByLivreurId(livreurId)
                .orElseThrow(() -> DeliveryException.notFound("Aucun vehicule assigne"));
        return mapToResponse(v);
    }

    public VehiculeResponse creerVehicule(VehiculeRequest request) {
        if (vehiculeRepository.existsByImmatriculation(request.getImmatriculation())) {
            throw DeliveryException.badRequest("Immatriculation deja utilisee");
        }
        User livreur = null;
        if (request.getLivreurId() != null) {
            livreur = userRepository.findById(request.getLivreurId())
                    .orElseThrow(() -> DeliveryException.notFound("Livreur non trouve"));
        }
        Vehicule vehicule = Vehicule.builder()
                .marque(request.getMarque())
                .modele(request.getModele())
                .immatriculation(request.getImmatriculation())
                .type(request.getType())
                .disponible(true)
                .livreur(livreur)
                .build();
        return mapToResponse(vehiculeRepository.save(vehicule));
    }

    public VehiculeResponse assignerLivreur(Long vehiculeId, Long livreurId) {
        Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> DeliveryException.notFound("Vehicule non trouve"));
        User livreur = userRepository.findById(livreurId)
                .orElseThrow(() -> DeliveryException.notFound("Livreur non trouve"));
        vehicule.setLivreur(livreur);
        return mapToResponse(vehiculeRepository.save(vehicule));
    }

    public void supprimerVehicule(Long id) {
        vehiculeRepository.deleteById(id);
    }

    private VehiculeResponse mapToResponse(Vehicule v) {
        return VehiculeResponse.builder()
                .id(v.getId())
                .marque(v.getMarque())
                .modele(v.getModele())
                .immatriculation(v.getImmatriculation())
                .type(v.getType())
                .disponible(v.isDisponible())
                .livreurId(v.getLivreur() != null ? v.getLivreur().getId() : null)
                .livreurNom(v.getLivreur() != null ?
                        v.getLivreur().getNom() + " " + v.getLivreur().getPrenom() : "Non assigne")
                .createdAt(v.getCreatedAt())
                .build();
    }

    public VehiculeResponse updateVehicule(Long id, VehiculeRequest request) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> DeliveryException.notFound("Vehicule non trouve"));
        vehicule.setMarque(request.getMarque());
        vehicule.setModele(request.getModele());
        vehicule.setImmatriculation(request.getImmatriculation());
        vehicule.setType(request.getType());
        if (request.getLivreurId() != null) {
            User livreur = userRepository.findById(request.getLivreurId())
                    .orElseThrow(() -> DeliveryException.notFound("Livreur non trouve"));
            vehicule.setLivreur(livreur);
        }
        return mapToResponse(vehiculeRepository.save(vehicule));
    }
}