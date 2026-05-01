package com.delivery.service;

import com.delivery.dto.response.ResponseDTOs.*;
import com.delivery.entity.Colis;
import com.delivery.entity.User;
import com.delivery.enums.ColisStatus;
import com.delivery.enums.Priorite;
import com.delivery.repository.ColisRepository;
import com.delivery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final ColisRepository colisRepository;
    private final UserRepository  userRepository;

    public ChatbotResponse repondre(String question, String email) {
        User livreur = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Livreur non trouve"));

        String q = question.toLowerCase().trim();

        // ===== QUESTION 1 : Combien de colis j'ai aujourd'hui =====
        if (q.contains("combien") && q.contains("colis") ||
                q.contains("nombre") && q.contains("colis") ||
                q.contains("colis") && q.contains("aujourd")) {
            return repondreCombienColis(livreur);
        }

        // ===== QUESTION 2 : Combien j'ai livré =====
        if ((q.contains("combien") && q.contains("livr")) ||
                (q.contains("livr") && q.contains("aujourd"))) {
            return repondreColisLivres(livreur);
        }

        // ===== QUESTION 3 : Colis le plus urgent =====
        if ((q.contains("plus") && q.contains("urgent")) ||
                (q.contains("urgent") || q.contains("express") ||
                        q.contains("priorite"))) {
            return repondreColisUrgent(livreur);
        }

        // ===== QUESTION 4 : Combien de livraisons restent =====
        if ((q.contains("combien") && q.contains("reste")) ||
                (q.contains("livraison") && q.contains("reste")) ||
                (q.contains("reste") && q.contains("livr")) ||
                q.contains("pas encore livr")) {
            return repondreLivraisonsRestantes(livreur);
        }

        // ===== QUESTION 5 : Quels colis j'ai livré aujourd'hui =====
        if ((q.contains("quel") && q.contains("livr")) ||
                (q.contains("liste") && q.contains("livr")) ||
                (q.contains("livr") && q.contains("aujourd"))) {
            return repondreListeColisLivres(livreur);
        }

        // ===== QUESTION 6 : Quel est mon classement =====
        if (q.contains("classement") || q.contains("rang") ||
                q.contains("position") || q.contains("place")) {
            return repondreClassement(livreur);
        }

        // ===== QUESTION 7 : Est-ce que je suis le meilleur =====
        if ((q.contains("meilleur") && q.contains("livreur")) ||
                q.contains("premier") || q.contains("top")) {
            return repondreMeilleurLivreur(livreur);
        }

        // ===== AIDE =====
        if (q.contains("aide") || q.contains("help") ||
                q.contains("que peux") || q.contains("question")) {
            return repondreAide();
        }

        // ===== QUESTION NON RECONNUE =====
        return ChatbotResponse.builder()
                .reponse("❓ Je n'ai pas compris votre question.\n\n" +
                        "Tapez 'aide' pour voir toutes les questions disponibles.")
                .type("ERREUR")
                .build();
    }

    // ================================================================
    // METHODE 1 — Combien de colis j'ai aujourd'hui
    // ================================================================
    private ChatbotResponse repondreCombienColis(User livreur) {
        LocalDateTime debut = LocalDate.now().atStartOfDay();
        LocalDateTime fin   = debut.plusDays(1);

        long total      = colisRepository.countByLivreurId(livreur.getId());
        long aujourdhui = colisRepository.countByLivreurIdAndCreatedAtBetween(
                livreur.getId(), debut, fin);
        long enAttente  = colisRepository.countByLivreurIdAndStatus(
                livreur.getId(), ColisStatus.ASSIGNE);
        long enCours    = colisRepository.countByLivreurIdAndStatus(
                livreur.getId(), ColisStatus.EN_COURS);

        return ChatbotResponse.builder()
                .reponse("📦 Vos colis du jour :\n\n" +
                        "📅 Assignes aujourd'hui : " + aujourdhui + "\n" +
                        "📊 Total assignes : "        + total      + "\n" +
                        "⏳ En attente : "             + enAttente  + "\n" +
                        "🚚 En cours : "               + enCours)
                .type("INFO")
                .build();
    }

    // ================================================================
    // METHODE 2 — Combien j'ai livré
    // ================================================================
    private ChatbotResponse repondreColisLivres(User livreur) {
        LocalDateTime debut = LocalDate.now().atStartOfDay();
        LocalDateTime fin   = debut.plusDays(1);

        long livresAujourdhui = colisRepository
                .countByLivreurIdAndStatusAndUpdatedAtBetween(
                        livreur.getId(), ColisStatus.LIVRE, debut, fin);
        long livresTotal = colisRepository.countByLivreurIdAndStatus(
                livreur.getId(), ColisStatus.LIVRE);

        return ChatbotResponse.builder()
                .reponse("✅ Livraisons effectuees :\n\n" +
                        "📅 Aujourd'hui : "  + livresAujourdhui + " livraisons\n" +
                        "📊 Total global : " + livresTotal      + " livraisons")
                .type("INFO")
                .build();
    }

    // ================================================================
    // METHODE 3 — Colis le plus urgent
    // ================================================================
    private ChatbotResponse repondreColisUrgent(User livreur) {

        // Chercher EXPRESS en premier
        List<Colis> express = colisRepository
                .findByLivreurIdAndPrioriteAndStatusOrderByCreatedAtAsc(
                        livreur.getId(), Priorite.EXPRESS, ColisStatus.ASSIGNE);

        if (!express.isEmpty()) {
            Colis c = express.get(0);
            return ChatbotResponse.builder()
                    .reponse("🔴 Colis le plus urgent (EXPRESS) :\n\n" +
                            "📋 Numero : "       + c.getNumeroSuivi()          + "\n" +
                            "👤 Destinataire : " + c.getDestinataireNom()      + "\n" +
                            "📍 Adresse : "      + c.getAdresseLivraison()     + "\n" +
                            "📞 Telephone : "    + c.getDestinataireTelephone())
                    .type("INFO")
                    .build();
        }

        // Sinon chercher URGENTE
        List<Colis> urgents = colisRepository
                .findByLivreurIdAndPrioriteAndStatusOrderByCreatedAtAsc(
                        livreur.getId(), Priorite.URGENTE, ColisStatus.ASSIGNE);

        if (!urgents.isEmpty()) {
            Colis c = urgents.get(0);
            return ChatbotResponse.builder()
                    .reponse("🟠 Colis le plus urgent (URGENTE) :\n\n" +
                            "📋 Numero : "       + c.getNumeroSuivi()          + "\n" +
                            "👤 Destinataire : " + c.getDestinataireNom()      + "\n" +
                            "📍 Adresse : "      + c.getAdresseLivraison()     + "\n" +
                            "📞 Telephone : "    + c.getDestinataireTelephone())
                    .type("INFO")
                    .build();
        }

        return ChatbotResponse.builder()
                .reponse("✅ Vous n'avez aucun colis urgent en attente.")
                .type("INFO")
                .build();
    }

    // ================================================================
    // METHODE 4 — Combien de livraisons restent
    // ================================================================
    private ChatbotResponse repondreLivraisonsRestantes(User livreur) {
        long assigne = colisRepository.countByLivreurIdAndStatus(
                livreur.getId(), ColisStatus.ASSIGNE);
        long enCours = colisRepository.countByLivreurIdAndStatus(
                livreur.getId(), ColisStatus.EN_COURS);
        long restants = assigne + enCours;

        if (restants == 0) {
            return ChatbotResponse.builder()
                    .reponse("🎉 Bravo ! Vous avez termine toutes vos livraisons.\n" +
                            "Il ne vous reste aucun colis a livrer.")
                    .type("INFO")
                    .build();
        }

        return ChatbotResponse.builder()
                .reponse("📦 Livraisons restantes :\n\n" +
                        "⏳ En attente : " + assigne  + " colis\n" +
                        "🚚 En cours : "   + enCours  + " colis\n" +
                        "📊 Total restant : " + restants + " colis")
                .type("INFO")
                .build();
    }

    // ================================================================
    // METHODE 5 — Quels colis j'ai livré aujourd'hui
    // ================================================================
    private ChatbotResponse repondreListeColisLivres(User livreur) {
        LocalDateTime debut = LocalDate.now().atStartOfDay();
        LocalDateTime fin   = debut.plusDays(1);

        List<Colis> liste = colisRepository
                .findByLivreurIdAndStatusAndUpdatedAtBetween(
                        livreur.getId(), ColisStatus.LIVRE, debut, fin);

        if (liste.isEmpty()) {
            return ChatbotResponse.builder()
                    .reponse("📭 Vous n'avez pas encore livre de colis aujourd'hui.")
                    .type("INFO")
                    .build();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("✅ Colis livres aujourd'hui (")
                .append(liste.size()).append(") :\n\n");

        for (Colis c : liste) {
            sb.append("• ").append(c.getNumeroSuivi())
                    .append(" → ").append(c.getDestinataireNom())
                    .append("\n");
        }

        return ChatbotResponse.builder()
                .reponse(sb.toString())
                .type("LISTE")
                .build();
    }

    // ================================================================
    // METHODE 6 — Quel est mon classement
    // ================================================================
    private ChatbotResponse repondreClassement(User livreur) {
        List<Object[]> classement = colisRepository.getClassementLivreurs();

        int position = 1;
        long mesLivraisons = 0;
        int total = classement.size();

        for (Object[] row : classement) {
            Long livreurId = (Long) row[0];
            Long nbLivraisons = (Long) row[1];
            if (livreurId.equals(livreur.getId())) {
                mesLivraisons = nbLivraisons;
                break;
            }
            position++;
        }

        String emoji = position == 1 ? "🥇" :
                position == 2 ? "🥈" :
                position == 3 ? "🥉" : "🏅";

        return ChatbotResponse.builder()
                .reponse("🏆 Votre classement :\n\n" +
                        emoji + " Vous etes " + position + "er sur " + total + " livreurs\n" +
                        "✅ Livraisons reussies : " + mesLivraisons + "\n\n" +
                        (position == 1 ? "🎉 Felicitations ! Vous etes le meilleur !" :
                                "💪 Continuez vos efforts pour monter dans le classement !"))
                .type("INFO")
                .build();
    }

    // ================================================================
    // METHODE 7 — Est-ce que je suis le meilleur livreur
    // ================================================================
    private ChatbotResponse repondreMeilleurLivreur(User livreur) {
        List<Object[]> classement = colisRepository.getClassementLivreurs();

        if (classement.isEmpty()) {
            return ChatbotResponse.builder()
                    .reponse("📊 Pas encore assez de donnees pour le classement.")
                    .type("INFO")
                    .build();
        }

        Long premierId = (Long) classement.get(0)[0];
        Long premierLivraisons = (Long) classement.get(0)[1];

        if (premierId.equals(livreur.getId())) {
            return ChatbotResponse.builder()
                    .reponse("🥇 OUI ! Vous etes le meilleur livreur !\n\n" +
                            "✅ Livraisons reussies : " + premierLivraisons + "\n" +
                            "🎉 Felicitations pour vos excellentes performances !")
                    .type("INFO")
                    .build();
        }

        // Trouver la position du livreur
        int position = 1;
        long mesLivraisons = 0;
        for (Object[] row : classement) {
            Long id = (Long) row[0];
            if (id.equals(livreur.getId())) {
                mesLivraisons = (Long) row[1];
                break;
            }
            position++;
        }

        long diff = premierLivraisons - mesLivraisons;

        return ChatbotResponse.builder()
                .reponse("💪 Pas encore, mais vous pouvez y arriver !\n\n" +
                        "📊 Votre position : " + position + "eme\n" +
                        "✅ Vos livraisons : " + mesLivraisons + "\n" +
                        "🥇 Le meilleur a : " + premierLivraisons + " livraisons\n" +
                        "📈 Il vous manque : " + diff + " livraisons\n\n" +
                        "🚀 Continuez comme ca !")
                .type("INFO")
                .build();
    }

    // ================================================================
    // AIDE
    // ================================================================
    private ChatbotResponse repondreAide() {
        return ChatbotResponse.builder()
                .reponse("🤖 Voici ce que je peux faire :\n\n" +
                        "1️⃣ Combien de colis j'ai aujourd'hui ?\n" +
                        "2️⃣ Combien j'ai livré ?\n" +
                        "3️⃣ Quel est le colis le plus urgent ?\n" +
                        "4️⃣ Combien de livraisons me restent ?\n" +
                        "5️⃣ Quels colis j'ai livré aujourd'hui ?\n" +
                        "6️⃣ Quel est mon classement ?\n" +
                        "7️⃣ Est-ce que je suis le meilleur livreur ?")
                .type("AIDE")
                .build();
    }
}