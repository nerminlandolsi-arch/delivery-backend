package com.delivery.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

public class ResponseDTOs {

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public static <T> ApiResponse<T> ok(String message, T data) {
            return new ApiResponse<>(true, message, data);
        }

        public static <T> ApiResponse<T> ok(T data) {
            return new ApiResponse<>(true, "Succès", data);
        }

        public static ApiResponse<Void> ok(String message) {
            return new ApiResponse<>(true, message, null);
        }
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AuthResponse {
        private String accessToken;
        private String refreshToken;
        private String tokenType;
        private UserResponse user;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UserResponse {
        private Long id;
        private String nom;
        private String prenom;
        private String email;
        private String telephone;
        private String role;
        private String photoUrl;
        private boolean actif;
        private LocalDateTime createdAt;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ColisResponse {
        private Long id;
        private String numeroSuivi;
        private String description;
        private Double poids;
        private String dimensions;
        private String expediteurNom;
        private String expediteurTelephone;
        private String adresseEnlevement;
        private Double latEnlevement;
        private Double lngEnlevement;
        private String destinataireNom;
        private String destinataireTelephone;
        private String adresseLivraison;
        private Double latLivraison;
        private Double lngLivraison;
        private String status;
        private String priorite;
        private UserResponse livreur;
        private String notesLivreur;
        private String photoPreuveUrl;
        private String codeBarres;
        private String barcodeImage;
        private LocalDateTime dateLivraisonPrevue;
        private LocalDateTime dateLivraisonReelle;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<HistoriqueResponse> historique;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class HistoriqueResponse {
        private Long id;
        private String status;
        private String description;
        private Double latitude;
        private Double longitude;
        private String updatedBy;
        private LocalDateTime createdAt;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class PositionResponse {
        private Long livreurId;
        private String livreurNom;
        private Double latitude;
        private Double longitude;
        private Double vitesse;
        private LocalDateTime timestamp;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class NotificationResponse {
        private Long id;
        private String titre;
        private String message;
        private boolean lue;
        private String type;
        private Long referenceId;
        private LocalDateTime createdAt;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class StatistiquesResponse {
        private long totalColis;
        private long colisEnAttente;
        private long colisEnCours;
        private long colisLivres;
        private long colisEchoues;
        private long totalLivreurs;
        private long livreursActifs;
        private double tauxReussite;
        private long colisAujourdHui;
        private long livraisonsAujourdHui;
        private List<LivreurStatsResponse> topLivreurs;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class LivreurStatsResponse {
        private Long livreurId;
        private String livreurNom;
        private long totalLivraisons;
        private long livraisonsReussies;
        private double tauxReussite;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class VehiculeResponse {
        private Long id;
        private String marque;
        private String modele;
        private String immatriculation;
        private String type;
        private boolean disponible;
        private Long livreurId;
        private String livreurNom;
        private LocalDateTime createdAt;
    }

    @Getter @Setter
    public static class VehiculeRequest {
        private String marque;
        private String modele;
        private String immatriculation;
        private String type;
        private Long livreurId;
    }

    // ===== CHATBOT =====
    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ChatbotResponse {
        private String reponse;
        private String type;
    }
}
