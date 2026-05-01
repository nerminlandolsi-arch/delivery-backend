package com.delivery.service;

import com.delivery.util.BarcodeUtil;
import com.delivery.util.BarcodeUtil;
import com.delivery.dto.request.RequestDTOs.*;
import com.delivery.dto.response.ResponseDTOs.*;
import com.delivery.entity.*;
import com.delivery.enums.ColisStatus;
import com.delivery.enums.Priorite;
import com.delivery.exception.DeliveryException;
import com.delivery.repository.*;
import com.delivery.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ColisService {

    private final BarcodeUtil barcodeUtil;
    private final ColisRepository colisRepository;
    private final UserRepository userRepository;
    private final HistoriqueColisRepository historiqueRepository;
    private final FileStorageUtil fileStorageUtil;
    private final NotificationService notificationService;

    @Transactional
    public ColisResponse createColis(CreateColisRequest request, User admin) {
        Colis colis = Colis.builder()
                .expediteurNom(request.getExpediteurNom())
                .expediteurTelephone(request.getExpediteurTelephone())
                .adresseEnlevement(request.getAdresseEnlevement())
                .latEnlevement(request.getLatEnlevement())
                .lngEnlevement(request.getLngEnlevement())
                .destinataireNom(request.getDestinataireNom())
                .destinataireTelephone(request.getDestinataireTelephone())
                .adresseLivraison(request.getAdresseLivraison())
                .latLivraison(request.getLatLivraison())
                .lngLivraison(request.getLngLivraison())
                .description(request.getDescription())
                .poids(request.getPoids())
                .dimensions(request.getDimensions())
                .priorite(request.getPriorite() != null ?
                        Priorite.valueOf(request.getPriorite().toUpperCase()) : Priorite.NORMALE)
                .status(ColisStatus.EN_ATTENTE)
                .build();

        if (request.getDateLivraisonPrevue() != null) {
            colis.setDateLivraisonPrevue(LocalDateTime.parse(request.getDateLivraisonPrevue()));
        }

        colis = colisRepository.save(colis);
        ajouterHistorique(colis, ColisStatus.EN_ATTENTE, "Colis créé", null, null, admin);

        log.info("Colis created: {}", colis.getNumeroSuivi());
        return mapToColisResponse(colis, true);
    }

    public List<ColisResponse> getAllColis() {
        return colisRepository.findAll()
                .stream().map(c -> mapToColisResponse(c, false)).collect(Collectors.toList());
    }

    public List<ColisResponse> getColisByStatus(String status) {
        ColisStatus colisStatus = ColisStatus.valueOf(status.toUpperCase());
        return colisRepository.findByStatus(colisStatus)
                .stream().map(c -> mapToColisResponse(c, false)).collect(Collectors.toList());
    }

    public ColisResponse getColisById(Long id) {
        Colis colis = findColisOrThrow(id);
        return mapToColisResponse(colis, true);
    }

    public ColisResponse getColisByNumeroSuivi(String numeroSuivi) {
        Colis colis = colisRepository.findByNumeroSuivi(numeroSuivi)
                .orElseThrow(() -> DeliveryException.notFound("Colis non trouvé: " + numeroSuivi));
        return mapToColisResponse(colis, true);
    }

    public List<ColisResponse> getMesColisByLivreur(Long livreurId) {
        return colisRepository.findByLivreurId(livreurId)
                .stream().map(c -> mapToColisResponse(c, true)).collect(Collectors.toList());
    }

    public List<ColisResponse> getColisEnCoursForLivreur(Long livreurId) {
        return colisRepository.findColisEnCoursForLivreur(livreurId)
                .stream().map(c -> mapToColisResponse(c, true)).collect(Collectors.toList());
    }

    @Transactional
    public ColisResponse assignerLivreur(Long colisId, AssignerColisRequest request, User admin) {
        Colis colis = findColisOrThrow(colisId);

        if (colis.getStatus() == ColisStatus.LIVRE || colis.getStatus() == ColisStatus.RETOURNE) {
            throw DeliveryException.badRequest("Ce colis est déjà finalisé");
        }

        User livreur = userRepository.findById(request.getLivreurId())
                .orElseThrow(() -> DeliveryException.notFound("Livreur non trouvé"));

        if (!livreur.isActif()) {
            throw DeliveryException.badRequest("Ce livreur est inactif");
        }

        colis.setLivreur(livreur);
        colis.setStatus(ColisStatus.ASSIGNE);
        colis = colisRepository.save(colis);

        ajouterHistorique(colis, ColisStatus.ASSIGNE,
                "Assigné au livreur " + livreur.getNom() + " " + livreur.getPrenom(),
                null, null, admin);

        // Notification push au livreur
        notificationService.envoyerNotification(livreur,
                "Nouveau colis assigné",
                "Le colis " + colis.getNumeroSuivi() + " vous a été assigné",
                "COLIS_ASSIGNE", colis.getId());

        return mapToColisResponse(colis, true);
    }

    @Transactional
    public ColisResponse updateStatus(Long colisId, UpdateStatusRequest request, User currentUser) {
        Colis colis = findColisOrThrow(colisId);

        ColisStatus newStatus;
        try {
            newStatus = ColisStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw DeliveryException.badRequest("Statut invalide: " + request.getStatus());
        }

        // Validation : un livreur ne peut modifier que ses propres colis
        if (currentUser.getRole().name().equals("ROLE_LIVREUR")) {
            if (colis.getLivreur() == null || !colis.getLivreur().getId().equals(currentUser.getId())) {
                throw DeliveryException.forbidden("Vous ne pouvez pas modifier ce colis");
            }
        }

        ColisStatus oldStatus = colis.getStatus();
        colis.setStatus(newStatus);
        if (request.getNotes() != null) colis.setNotesLivreur(request.getNotes());

        if (newStatus == ColisStatus.LIVRE) {
            colis.setDateLivraisonReelle(LocalDateTime.now());
        }

        colis = colisRepository.save(colis);

        String description = request.getNotes() != null ? request.getNotes()
                : "Statut mis à jour: " + oldStatus.name() + " → " + newStatus.name();

        ajouterHistorique(colis, newStatus, description,
                request.getLatitude(), request.getLongitude(), currentUser);

        log.info("Colis {} status updated: {} -> {}", colis.getNumeroSuivi(), oldStatus, newStatus);
        return mapToColisResponse(colis, true);
    }

    @Transactional
    public ColisResponse uploadPhotoPreuve(Long colisId, MultipartFile file, User livreur) {
        Colis colis = findColisOrThrow(colisId);

        if (colis.getLivreur() == null || !colis.getLivreur().getId().equals(livreur.getId())) {
            throw DeliveryException.forbidden("Vous ne pouvez pas modifier ce colis");
        }

        if (colis.getPhotoPreuveUrl() != null) {
            fileStorageUtil.deleteFile(colis.getPhotoPreuveUrl());
        }

        String photoUrl = fileStorageUtil.saveFile(file, "preuves");
        colis.setPhotoPreuveUrl(photoUrl);
        colis = colisRepository.save(colis);

        ajouterHistorique(colis, colis.getStatus(), "Photo de preuve ajoutée",
                null, null, livreur);

        return mapToColisResponse(colis, true);
    }

    @Transactional
    public void deleteColis(Long id) {
        Colis colis = findColisOrThrow(id);
        if (colis.getStatus() == ColisStatus.EN_COURS) {
            throw DeliveryException.badRequest("Impossible de supprimer un colis en cours de livraison");
        }
        colisRepository.delete(colis);
    }

    // ===== PRIVATE HELPERS =====

    private Colis findColisOrThrow(Long id) {
        return colisRepository.findById(id)
                .orElseThrow(() -> DeliveryException.notFound("Colis non trouvé avec l'id: " + id));
    }

    private void ajouterHistorique(Colis colis, ColisStatus status, String description,
                                    Double lat, Double lng, User user) {
        HistoriqueColis h = HistoriqueColis.builder()
                .colis(colis)
                .status(status)
                .description(description)
                .latitude(lat)
                .longitude(lng)
                .updatedBy(user)
                .build();
        historiqueRepository.save(h);
    }

    public ColisResponse mapToColisResponse(Colis colis, boolean withHistorique) {
        ColisResponse.ColisResponseBuilder builder = ColisResponse.builder()
                .id(colis.getId())
                .numeroSuivi(colis.getNumeroSuivi())
                .codeBarres(colis.getCodeBarres())
                .barcodeImage(colis.getCodeBarres() != null ?
                        barcodeUtil.generateBarcodeBase64(colis.getCodeBarres()) : null)
                .description(colis.getDescription())
                .poids(colis.getPoids())
                .dimensions(colis.getDimensions())
                .expediteurNom(colis.getExpediteurNom())
                .expediteurTelephone(colis.getExpediteurTelephone())
                .adresseEnlevement(colis.getAdresseEnlevement())
                .latEnlevement(colis.getLatEnlevement())
                .lngEnlevement(colis.getLngEnlevement())
                .destinataireNom(colis.getDestinataireNom())
                .destinataireTelephone(colis.getDestinataireTelephone())
                .adresseLivraison(colis.getAdresseLivraison())
                .latLivraison(colis.getLatLivraison())
                .lngLivraison(colis.getLngLivraison())
                .status(colis.getStatus().name())
                .priorite(colis.getPriorite().name())
                .notesLivreur(colis.getNotesLivreur())
                .photoPreuveUrl(colis.getPhotoPreuveUrl())
                .dateLivraisonPrevue(colis.getDateLivraisonPrevue())
                .dateLivraisonReelle(colis.getDateLivraisonReelle())
                .createdAt(colis.getCreatedAt())
                .updatedAt(colis.getUpdatedAt());

        if (colis.getLivreur() != null) {
            builder.livreur(AuthService.mapToUserResponse(colis.getLivreur()));
        }

        if (withHistorique && colis.getHistorique() != null) {
            builder.historique(colis.getHistorique().stream().map(h ->
                    HistoriqueResponse.builder()
                            .id(h.getId())
                            .status(h.getStatus().name())
                            .description(h.getDescription())
                            .latitude(h.getLatitude())
                            .longitude(h.getLongitude())
                            .updatedBy(h.getUpdatedBy() != null ?
                                    h.getUpdatedBy().getNom() + " " + h.getUpdatedBy().getPrenom() : null)
                            .createdAt(h.getCreatedAt())
                            .build()
            ).collect(Collectors.toList()));
        }

        return builder.build();
    }

    public ColisResponse findByCodeBarres(String codeBarres) {
        Colis colis = colisRepository.findByCodeBarres(codeBarres)
                .orElseThrow(() -> DeliveryException.notFound(
                        "Colis introuvable avec ce code-barres: " + codeBarres));
        return mapToColisResponse(colis, true);
    }

    public String getBarcodeImage(Long id) {
        Colis colis = findColisOrThrow(id);
        return barcodeUtil.generateBarcodeBase64(colis.getCodeBarres());
    }
}
