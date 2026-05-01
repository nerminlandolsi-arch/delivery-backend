package com.delivery.service;

import com.delivery.dto.response.ResponseDTOs.*;
import com.delivery.enums.ColisStatus;
import com.delivery.enums.Role;
import com.delivery.repository.ColisRepository;
import com.delivery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatistiquesService {

    private final ColisRepository colisRepository;
    private final UserRepository userRepository;

    public StatistiquesResponse getStatistiquesGlobales() {
        long totalColis = colisRepository.count();
        long enAttente = colisRepository.countByStatus(ColisStatus.EN_ATTENTE);
        long enCours = colisRepository.countByStatus(ColisStatus.EN_COURS)
                + colisRepository.countByStatus(ColisStatus.ASSIGNE);
        long livres = colisRepository.countByStatus(ColisStatus.LIVRE);
        long echoues = colisRepository.countByStatus(ColisStatus.ECHEC);
        long totalLivreurs = userRepository.findByRole(Role.ROLE_LIVREUR).size();
        long livreursActifs = userRepository.findAllLivreursActifs().size();

        double tauxReussite = totalColis > 0 ?
                Math.round(((double) livres / totalColis) * 10000.0) / 100.0 : 0.0;

        LocalDateTime debutJour = LocalDate.now().atStartOfDay();
        LocalDateTime finJour = debutJour.plusDays(1);
        long colisAujourdHui = colisRepository.countByPeriode(debutJour, finJour);
        long livraisonsAujourdHui = colisRepository.countLivresParPeriode(debutJour, finJour);

        List<LivreurStatsResponse> topLivreurs = new ArrayList<>();
        try {
            topLivreurs = getTopLivreurs();
        } catch (Exception e) {
            log.warn("Erreur top livreurs: {}", e.getMessage());
        }

        return StatistiquesResponse.builder()
                .totalColis(totalColis)
                .colisEnAttente(enAttente)
                .colisEnCours(enCours)
                .colisLivres(livres)
                .colisEchoues(echoues)
                .totalLivreurs(totalLivreurs)
                .livreursActifs(livreursActifs)
                .tauxReussite(tauxReussite)
                .colisAujourdHui(colisAujourdHui)
                .livraisonsAujourdHui(livraisonsAujourdHui)
                .topLivreurs(topLivreurs)
                .build();
    }

    public StatistiquesResponse getStatistiquesLivreur(Long livreurId) {
        long total = colisRepository.countByLivreurId(livreurId);
        long livres = colisRepository.countByLivreurIdAndStatus(livreurId, ColisStatus.LIVRE);
        long enCours = colisRepository.countByLivreurIdAndStatus(livreurId, ColisStatus.EN_COURS);
        long echoues = colisRepository.countByLivreurIdAndStatus(livreurId, ColisStatus.ECHEC);
        double taux = total > 0 ? Math.round(((double) livres / total) * 10000.0) / 100.0 : 0.0;

        LocalDateTime debutJour = LocalDate.now().atStartOfDay();
        long livraisonsAujourdHui = colisRepository.countLivresParPeriode(debutJour, debutJour.plusDays(1));

        return StatistiquesResponse.builder()
                .totalColis(total)
                .colisLivres(livres)
                .colisEnCours(enCours)
                .colisEchoues(echoues)
                .tauxReussite(taux)
                .livraisonsAujourdHui(livraisonsAujourdHui)
                .build();
    }

    private List<LivreurStatsResponse> getTopLivreurs() {
        List<Object[]> results = colisRepository.findTopLivreurs();
        if (results == null || results.isEmpty()) return new ArrayList<>();

        return results.stream()
                .limit(5)
                .filter(row -> row != null && row[0] != null)
                .map(row -> {
                    try {
                        Long livreurId = ((Number) row[0]).longValue();
                        long totalLivraisons = ((Number) row[1]).longValue();
                        long reussies = colisRepository.countByLivreurIdAndStatus(
                                livreurId, ColisStatus.LIVRE);
                        double taux = totalLivraisons > 0 ?
                                Math.round(((double) reussies / totalLivraisons) * 10000.0) / 100.0 : 0.0;

                        return userRepository.findById(livreurId).map(u ->
                                LivreurStatsResponse.builder()
                                        .livreurId(livreurId)
                                        .livreurNom(u.getNom() + " " + u.getPrenom())
                                        .totalLivraisons(totalLivraisons)
                                        .livraisonsReussies(reussies)
                                        .tauxReussite(taux)
                                        .build()
                        ).orElse(null);
                    } catch (Exception e) {
                        log.warn("Erreur livreur stats: {}", e.getMessage());
                        return null;
                    }
                })
                .filter(s -> s != null)
                .collect(Collectors.toList());
    }
}