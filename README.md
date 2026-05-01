# 📦 Delivery Backend API — PFE

Backend REST API complet pour une application de livraison de colis, développé avec **Spring Boot 3**, **MySQL** et **JWT**.

---

## 🛠️ Stack Technique

| Composant        | Technologie              |
|------------------|--------------------------|
| Framework        | Spring Boot 3.2          |
| Language         | Java 17                  |
| Base de données  | MySQL 8+                 |
| Authentification | JWT (jjwt 0.11.5)        |
| Notifications    | Firebase FCM             |
| Documentation    | Swagger UI (SpringDoc)   |
| Build            | Maven                    |

---

## 📁 Structure du projet

```
src/main/java/com/delivery/
├── config/               # SecurityConfig, WebConfig, FirebaseConfig, OpenApiConfig, DataInitializer
├── controller/           # AuthController, AdminController, AdminColisController, AdminLivreurController, LivreurController
├── dto/
│   ├── request/          # RequestDTOs (Login, Register, CreateColis, UpdateStatus...)
│   └── response/         # ResponseDTOs (ApiResponse, AuthResponse, ColisResponse...)
├── entity/               # User, Colis, HistoriqueColis, PositionLivreur, Notification
├── enums/                # Role, ColisStatus, Priorite
├── exception/            # DeliveryException, GlobalExceptionHandler
├── repository/           # UserRepository, ColisRepository, PositionLivreurRepository...
├── security/             # JwtUtil, JwtAuthenticationFilter, CustomUserDetailsService
├── service/              # AuthService, ColisService, LivreurService, PositionService, NotificationService, StatistiquesService
└── util/                 # FileStorageUtil
```

---

## ⚙️ Installation & Démarrage

### Prérequis
- Java 17+
- Maven 3.8+
- MySQL 8+

### 1. Cloner le projet
```bash
git clone <your-repo-url>
cd delivery-backend
```

### 2. Créer la base de données MySQL
```sql
CREATE DATABASE delivery_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configurer `application.properties`
Modifiez les valeurs dans `src/main/resources/application.properties` :
```properties
spring.datasource.username=VOTRE_USERNAME_MYSQL
spring.datasource.password=VOTRE_PASSWORD_MYSQL
```

### 4. (Optionnel) Configurer Firebase FCM
Si vous voulez activer les notifications push :
1. Allez sur [Firebase Console](https://console.firebase.google.com)
2. Créez un projet → Paramètres → Comptes de service → Générer une clé privée
3. Renommez le fichier en `firebase-service-account.json`
4. Placez-le dans `src/main/resources/`

> Sans ce fichier, l'application fonctionne normalement mais sans push notifications.

### 5. Lancer l'application
```bash
mvn spring-boot:run
```

L'API sera disponible sur : **http://localhost:8080/api**

---

## 🔑 Comptes de démonstration

Au premier démarrage, ces comptes sont créés automatiquement :

| Rôle    | Email                    | Mot de passe   |
|---------|--------------------------|----------------|
| Admin   | admin@delivery.com       | Admin@1234     |
| Livreur | livreur1@delivery.com    | Livreur@1234   |
| Livreur | livreur2@delivery.com    | Livreur@1234   |
| Livreur | livreur3@delivery.com    | Livreur@1234   |

---

## 📖 Documentation API (Swagger)

Swagger UI disponible sur : **http://localhost:8080/api/swagger-ui.html**

---

## 🔐 Authentification JWT

Toutes les requêtes (sauf `/auth/**`) nécessitent un header :
```
Authorization: Bearer <votre_token_jwt>
```

### Obtenir un token
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@delivery.com",
  "password": "Admin@1234"
}
```

---

## 📌 Endpoints principaux

### Auth
| Méthode | URL                        | Description                |
|---------|----------------------------|----------------------------|
| POST    | /auth/login                | Connexion (Admin/Livreur)  |
| POST    | /auth/register/livreur     | Inscription livreur        |
| POST    | /auth/refresh              | Rafraîchir le token        |

### Admin — Colis
| Méthode | URL                             | Description                    |
|---------|---------------------------------|--------------------------------|
| GET     | /admin/colis                    | Lister tous les colis          |
| GET     | /admin/colis?status=EN_ATTENTE  | Filtrer par statut             |
| POST    | /admin/colis                    | Créer un colis                 |
| GET     | /admin/colis/{id}               | Détail d'un colis              |
| GET     | /admin/colis/suivi/{numero}     | Rechercher par numéro de suivi |
| PUT     | /admin/colis/{id}/assigner      | Assigner à un livreur          |
| PATCH   | /admin/colis/{id}/status        | Changer le statut              |
| DELETE  | /admin/colis/{id}               | Supprimer un colis             |

### Admin — Livreurs
| Méthode | URL                                 | Description                 |
|---------|-------------------------------------|-----------------------------|
| GET     | /admin/livreurs                     | Lister tous les livreurs    |
| GET     | /admin/livreurs?actifsOnly=true     | Livreurs actifs seulement   |
| GET     | /admin/livreurs/{id}                | Détail d'un livreur         |
| PUT     | /admin/livreurs/{id}                | Modifier un livreur         |
| PATCH   | /admin/livreurs/{id}/toggle-actif   | Activer / désactiver        |
| GET     | /admin/livreurs/{id}/statistiques   | Statistiques d'un livreur   |
| DELETE  | /admin/livreurs/{id}                | Supprimer un livreur        |

### Admin — Dashboard & GPS
| Méthode | URL                         | Description                         |
|---------|-----------------------------|-------------------------------------|
| GET     | /admin/statistiques         | Statistiques globales               |
| GET     | /admin/positions            | Positions de tous les livreurs      |
| GET     | /admin/positions/{livreurId}| Position d'un livreur               |

### Livreur (App Mobile)
| Méthode | URL                              | Description                     |
|---------|----------------------------------|---------------------------------|
| GET     | /livreur/colis                   | Mes colis assignés              |
| GET     | /livreur/colis/en-cours          | Mes colis en cours              |
| GET     | /livreur/colis/{id}              | Détail d'un colis               |
| PATCH   | /livreur/colis/{id}/status       | Mettre à jour le statut         |
| POST    | /livreur/colis/{id}/photo-preuve | Uploader photo de preuve        |
| POST    | /livreur/position                | Envoyer ma position GPS         |
| GET     | /livreur/notifications           | Mes notifications               |
| PATCH   | /livreur/notifications/{id}/lue  | Marquer notification comme lue  |
| GET     | /livreur/profil                  | Mon profil                      |
| PUT     | /livreur/profil                  | Modifier mon profil             |
| PATCH   | /livreur/fcm-token               | Mettre à jour token FCM         |
| GET     | /livreur/statistiques            | Mes statistiques                |

---

## 📊 Statuts des colis

```
EN_ATTENTE → ASSIGNE → EN_COURS → LIVRE
                              ↘ ECHEC → RETOURNE
```

---

## 🏗️ Modèle de données

### Tables MySQL générées automatiquement
- **users** — Admin et livreurs
- **colis** — Colis à livrer
- **historique_colis** — Timeline des changements de statut
- **positions_livreur** — Historique GPS des livreurs
- **notifications** — Notifications push

---

## 🧪 Exemples de requêtes

### Créer un colis
```http
POST /api/admin/colis
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "expediteurNom": "Boutique ABC",
  "expediteurTelephone": "+21671000000",
  "adresseEnlevement": "Avenue Habib Bourguiba, Tunis",
  "latEnlevement": 36.8189,
  "lngEnlevement": 10.1658,
  "destinataireNom": "Ali Ben Salah",
  "destinataireTelephone": "+21698000000",
  "adresseLivraison": "Rue de la Liberté, La Marsa",
  "latLivraison": 36.8786,
  "lngLivraison": 10.3238,
  "description": "Colis fragile",
  "poids": 2.5,
  "priorite": "URGENTE"
}
```

### Mettre à jour la position GPS (Livreur)
```http
POST /api/livreur/position
Authorization: Bearer <livreur_token>
Content-Type: application/json

{
  "latitude": 36.8354,
  "longitude": 10.2480,
  "vitesse": 45.5,
  "precisionMetres": 10.0
}
```

### Mettre à jour le statut d'un colis (Livreur)
```http
PATCH /api/livreur/colis/1/status
Authorization: Bearer <livreur_token>
Content-Type: application/json

{
  "status": "EN_COURS",
  "notes": "Colis récupéré, en route",
  "latitude": 36.8189,
  "longitude": 10.1658
}
```

---

## 📝 Notes pour le PFE

- Le projet utilise **Spring Boot 3 + Java 17** (dernières versions stables)
- Architecture **MVC** avec couche Service bien séparée
- **Validation des données** avec Bean Validation sur tous les DTOs
- **Gestion des erreurs** centralisée avec `GlobalExceptionHandler`
- **Swagger UI** intégré pour documenter et tester l'API
- Les **notifications push Firebase** sont optionnelles (désactivées si pas de credentials)
- Les **photos** (preuves de livraison, profils) sont stockées localement dans `uploads/`
