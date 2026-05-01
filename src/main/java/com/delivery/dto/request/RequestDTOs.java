package com.delivery.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

public class RequestDTOs {

    @Getter @Setter
    public static class LoginRequest {
        @NotBlank(message = "Email obligatoire")
        @Email(message = "Email invalide")
        private String email;

        @NotBlank(message = "Mot de passe obligatoire")
        private String password;
    }

    @Getter @Setter
    public static class RegisterLivreurRequest {
        @NotBlank(message = "Nom obligatoire")
        private String nom;

        @NotBlank(message = "Prénom obligatoire")
        private String prenom;

        @NotBlank @Email(message = "Email invalide")
        private String email;

        @NotBlank
        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
        private String password;

        @NotBlank(message = "Téléphone obligatoire")
        @Pattern(regexp = "^[+]?[0-9]{8,15}$", message = "Numéro de téléphone invalide")
        private String telephone;
    }

    @Getter @Setter
    public static class CreateColisRequest {
        @NotBlank(message = "Nom expéditeur obligatoire")
        private String expediteurNom;

        private String expediteurTelephone;

        @NotBlank(message = "Adresse d'enlèvement obligatoire")
        private String adresseEnlevement;

        private Double latEnlevement;
        private Double lngEnlevement;

        @NotBlank(message = "Nom destinataire obligatoire")
        private String destinataireNom;

        @NotBlank(message = "Téléphone destinataire obligatoire")
        private String destinataireTelephone;

        @NotBlank(message = "Adresse de livraison obligatoire")
        private String adresseLivraison;

        private Double latLivraison;
        private Double lngLivraison;

        private String description;
        private Double poids;
        private String dimensions;
        private String priorite;
        private String dateLivraisonPrevue; // ISO format
    }

    @Data
    public static class CreateLivreurRequest {
        private String nom;
        private String prenom;
        private String email;
        private String password;
        private String telephone;
    }

    @Getter @Setter
    public static class AssignerColisRequest {
        @NotNull(message = "ID livreur obligatoire")
        private Long livreurId;
    }

    @Getter @Setter
    public static class UpdateStatusRequest {
        @NotBlank(message = "Statut obligatoire")
        private String status;

        private String notes;
        private Double latitude;
        private Double longitude;
    }

    @Getter @Setter
    public static class UpdatePositionRequest {
        @NotNull(message = "Latitude obligatoire")
        private Double latitude;

        @NotNull(message = "Longitude obligatoire")
        private Double longitude;

        private Double vitesse;
        private Float precisionMetres;
    }

    @Getter @Setter
    public static class UpdateFcmTokenRequest {
        @NotBlank(message = "Token FCM obligatoire")
        private String fcmToken;
    }

    @Getter @Setter
    public static class UpdateProfileRequest {
        private String nom;
        private String prenom;
        private String telephone;
        private String currentPassword;
        private String newPassword;
    }

    // ===== CHATBOT =====
    @Getter @Setter
    public static class ChatbotRequest {
        private String question;
    }
}
