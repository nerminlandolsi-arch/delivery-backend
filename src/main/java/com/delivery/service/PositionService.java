package com.delivery.service;

import com.delivery.dto.request.RequestDTOs.UpdatePositionRequest;
import com.delivery.dto.response.ResponseDTOs.PositionResponse;
import com.delivery.entity.PositionLivreur;
import com.delivery.entity.User;
import com.delivery.enums.Role;
import com.delivery.exception.DeliveryException;
import com.delivery.repository.PositionLivreurRepository;
import com.delivery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PositionService {

    private final PositionLivreurRepository positionRepository;
    private final UserRepository userRepository;

    @Transactional
    public PositionResponse updatePosition(Long livreurId, UpdatePositionRequest request) {
        User livreur = userRepository.findById(livreurId)
                .orElseThrow(() -> DeliveryException.notFound("Livreur non trouvé"));

        PositionLivreur position = PositionLivreur.builder()
                .livreur(livreur)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .vitesse(request.getVitesse())
                .precisionMetres(request.getPrecisionMetres())
                .build();

        positionRepository.save(position);

        return buildPositionResponse(livreur, position);
    }

    public PositionResponse getDernierePosition(Long livreurId) {
        User livreur = userRepository.findById(livreurId)
                .orElseThrow(() -> DeliveryException.notFound("Livreur non trouvé"));

        PositionLivreur position = positionRepository
                .findDernierePositionByLivreurId(livreurId)
                .orElseThrow(() -> DeliveryException.notFound("Aucune position disponible pour ce livreur"));

        return buildPositionResponse(livreur, position);
    }

    public List<PositionResponse> getDernieresPositionsTousLivreurs() {
        List<User> livreurs = userRepository.findAllLivreursActifs();

        return livreurs.stream()
                .map(l -> positionRepository.findDernierePositionByLivreurId(l.getId())
                        .map(p -> buildPositionResponse(l, p))
                        .orElse(null))
                .filter(p -> p != null)
                .collect(Collectors.toList());
    }

    private PositionResponse buildPositionResponse(User livreur, PositionLivreur position) {
        return PositionResponse.builder()
                .livreurId(livreur.getId())
                .livreurNom(livreur.getNom() + " " + livreur.getPrenom())
                .latitude(position.getLatitude())
                .longitude(position.getLongitude())
                .vitesse(position.getVitesse())
                .timestamp(position.getTimestamp())
                .build();
    }
}
