package com.delivery.config;

import com.delivery.entity.Colis;
import com.delivery.entity.User;
import com.delivery.enums.ColisStatus;
import com.delivery.enums.Priorite;
import com.delivery.enums.Role;
import com.delivery.repository.ColisRepository;
import com.delivery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ColisRepository colisRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createAdminIfNotExists();
        createSampleLivreursIfNotExists();
        createSampleColisIfNotExists();
    }

    private void createAdminIfNotExists() {
        if (!userRepository.existsByEmail("admin@delivery.com")) {
            User admin = User.builder()
                    .nom("Admin")
                    .prenom("Système")
                    .email("admin@delivery.com")
                    .password(passwordEncoder.encode("Admin@1234"))
                    .telephone("+21600000000")
                    .role(Role.ROLE_ADMIN)
                    .actif(true)
                    .build();
            userRepository.save(admin);
            log.info("========================================");
            log.info("  Compte admin créé:");
            log.info("  Email    : admin@delivery.com");
            log.info("  Password : Admin@1234");
            log.info("========================================");
        }
    }

    private void createSampleLivreursIfNotExists() {
        if (userRepository.findAllLivreursActifs().isEmpty()) {

            User livreur1 = User.builder()
                    .nom("Ben Salem")
                    .prenom("Mohamed")
                    .email("livreur1@delivery.com")
                    .password(passwordEncoder.encode("Livreur@1234"))
                    .telephone("+21611111111")
                    .role(Role.ROLE_LIVREUR)
                    .actif(true)
                    .build();

            User livreur2 = User.builder()
                    .nom("Trabelsi")
                    .prenom("Sarra")
                    .email("livreur2@delivery.com")
                    .password(passwordEncoder.encode("Livreur@1234"))
                    .telephone("+21622222222")
                    .role(Role.ROLE_LIVREUR)
                    .actif(true)
                    .build();

            User livreur3 = User.builder()
                    .nom("Gharbi")
                    .prenom("Yassine")
                    .email("livreur3@delivery.com")
                    .password(passwordEncoder.encode("Livreur@1234"))
                    .telephone("+21633333333")
                    .role(Role.ROLE_LIVREUR)
                    .actif(true)
                    .build();

            userRepository.save(livreur1);
            userRepository.save(livreur2);
            userRepository.save(livreur3);

            log.info("3 livreurs de test créés (password: Livreur@1234)");
        }
    }

    private void createSampleColisIfNotExists() {
        if (colisRepository.count() == 0) {
            Colis colis1 = Colis.builder()
                    .expediteurNom("Boutique Tunis Centre")
                    .expediteurTelephone("+21671000001")
                    .adresseEnlevement("Avenue Habib Bourguiba, Tunis 1001")
                    .latEnlevement(36.8189)
                    .lngEnlevement(10.1658)
                    .destinataireNom("Karim Mansour")
                    .destinataireTelephone("+21698765432")
                    .adresseLivraison("Rue de la Liberté, La Marsa, Tunis")
                    .latLivraison(36.8786)
                    .lngLivraison(10.3238)
                    .description("Colis électronique fragile")
                    .poids(2.5)
                    .dimensions("30x20x15 cm")
                    .priorite(Priorite.URGENTE)
                    .status(ColisStatus.EN_ATTENTE)
                    .build();

            Colis colis2 = Colis.builder()
                    .expediteurNom("Shop Online Sfax")
                    .expediteurTelephone("+21674000002")
                    .adresseEnlevement("Avenue de l'Armée, Sfax 3000")
                    .latEnlevement(34.7406)
                    .lngEnlevement(10.7603)
                    .destinataireNom("Amira Bouzid")
                    .destinataireTelephone("+21691234567")
                    .adresseLivraison("Cité El Amal, Sousse 4000")
                    .latLivraison(35.8245)
                    .lngLivraison(10.6346)
                    .description("Vêtements - 3 articles")
                    .poids(1.2)
                    .dimensions("40x30x10 cm")
                    .priorite(Priorite.NORMALE)
                    .status(ColisStatus.EN_ATTENTE)
                    .build();

            Colis colis3 = Colis.builder()
                    .expediteurNom("Tech Store Ariana")
                    .expediteurTelephone("+21671000003")
                    .adresseEnlevement("Centre Commercial Ariana, Ariana 2080")
                    .latEnlevement(36.8610)
                    .lngEnlevement(10.1927)
                    .destinataireNom("Nour Hamdi")
                    .destinataireTelephone("+21655543210")
                    .adresseLivraison("Rue Ibn Khaldoun, Ben Arous 2013")
                    .latLivraison(36.7525)
                    .lngLivraison(10.2337)
                    .description("Accessoires informatiques")
                    .poids(0.8)
                    .dimensions("25x20x8 cm")
                    .priorite(Priorite.EXPRESS)
                    .status(ColisStatus.EN_ATTENTE)
                    .build();

            colisRepository.save(colis1);
            colisRepository.save(colis2);
            colisRepository.save(colis3);

            log.info("3 colis de démonstration créés");
        }
    }
}
