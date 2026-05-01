package com.delivery.entity;

import com.delivery.enums.ColisStatus;
import com.delivery.enums.Priorite;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "colis")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Colis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_suivi", nullable = false, unique = true)
    private String numeroSuivi;

    @Column(name = "code_barres", unique = true)
    private String codeBarres;

    @Column(name = "description")
    private String description;

    @Column(name = "poids")
    private Double poids; // en kg

    @Column(name = "dimensions")
    private String dimensions; // ex: "30x20x15 cm"

    // Expéditeur
    @Column(name = "expediteur_nom", nullable = false)
    private String expediteurNom;

    @Column(name = "expediteur_telephone")
    private String expediteurTelephone;

    @Column(name = "adresse_enlevement", nullable = false)
    private String adresseEnlevement;

    @Column(name = "lat_enlevement")
    private Double latEnlevement;

    @Column(name = "lng_enlevement")
    private Double lngEnlevement;

    // Destinataire
    @Column(name = "destinataire_nom", nullable = false)
    private String destinataireNom;

    @Column(name = "destinataire_telephone", nullable = false)
    private String destinataireTelephone;

    @Column(name = "adresse_livraison", nullable = false)
    private String adresseLivraison;

    @Column(name = "lat_livraison")
    private Double latLivraison;

    @Column(name = "lng_livraison")
    private Double lngLivraison;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ColisStatus status = ColisStatus.EN_ATTENTE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Priorite priorite = Priorite.NORMALE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livreur_id")
    private User livreur;

    @Column(name = "notes_livreur")
    private String notesLivreur;

    @Column(name = "photo_preuve_url")
    private String photoPreuveUrl;

    @Column(name = "date_livraison_prevue")
    private LocalDateTime dateLivraisonPrevue;

    @Column(name = "date_livraison_reelle")
    private LocalDateTime dateLivraisonReelle;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "colis", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<HistoriqueColis> historique = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (numeroSuivi == null) {
            numeroSuivi = "COL-" + System.currentTimeMillis();
        }
        if (codeBarres == null) {
            codeBarres = String.valueOf(System.currentTimeMillis());
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
