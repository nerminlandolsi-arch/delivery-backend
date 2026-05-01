package com.delivery.enums;

public enum ColisStatus {
    EN_ATTENTE,       // Colis créé, pas encore assigné
    ASSIGNE,          // Assigné à un livreur
    EN_COURS,         // En cours de livraison
    LIVRE,            // Livré avec succès
    ECHEC,            // Tentative de livraison échouée
    RETOURNE          // Retourné à l'expéditeur
}
