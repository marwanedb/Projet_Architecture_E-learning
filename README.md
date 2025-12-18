# 🎓 Plateforme E-Learning Microservices

Bienvenue sur la **Plateforme E-Learning**, une application distribuée robuste et évolutive conçue pour faciliter l'apprentissage en ligne. Ce projet repose sur une architecture **Microservices** moderne utilisant l'écosystème **Spring Boot** et **Spring Cloud**.

## 🏗️ Architecture

Le système est décomposé en services autonomes, chacun responsable d'un domaine métier spécifique, communiquant via REST et OpenFeign.

### Services Principaux

| Service | Port | Description |
| :--- | :--- | :--- |
| **Discovery Service** (Eureka) | `8761` | Registre de services pour la découverte dynamique. |
| **API Gateway** | `8080` | Point d'entrée unique, routage, et filtrage de sécurité (JWT). |
| **Auth Service** | `8081` | Gestion de l'identité, authentification (JWT) et rôles (Student, Professor, Admin). |
| **Student Service** | `8082` | Gestion des profils étudiants. |
| **Professor Service** | `8083` | Gestion des profils professeurs et de leurs cours. |
| **Admin Service** | `8084` | Tableau de bord administrateur, modération et logs d'audit. |
| **Catalog Service** | `8085` | Gestion du catalogue de cours, modules, et quiz. |
| **Learning Service** | `8086` | Suivi des inscriptions, progression et tentatives de quiz. |

---

## 🛠️ Technologies Utilisées

*   **Langage** : Java 17
*   **Framework** : Spring Boot 3.x
*   **Microservices** :
    *   **Spring Cloud Netflix Eureka** (Service Discovery)
    *   **Spring Cloud Gateway** (API Gateway)
    *   **Spring Cloud OpenFeign** (Communication Inter-services)
*   **Sécurité** : Spring Security, JWT (JSON Web Tokens), BCrypt
*   **Base de Données** : MySQL (Une base de données par service pour une isolation stricte)
*   **Persistance** : Spring Data JPA, Hibernate
*   **Outils & Utilitaires** : Lombok, Maven, Postman

---

## 🚀 Fonctionnalités Clés

*   **Authentification & Sécurité**
    *   Inscription et connexion sécurisées (JWT).
    *   Gestion des rôles (RBAC) : Étudiant, Professeur, Administrateur.
    *   Gateway sécurisée validant les tokens avant le routage.

*   **Catalogue de Cours**
    *   Création, mise à jour et suppression de cours (Professeurs).
    *   Organisation en Chapitres, Modules et Leçons.
    *   Gestion des médias (vidéos, images).
    *   Recherche et filtrage de cours.

*   **Expérience d'Apprentissage**
    *   Inscription aux cours.
    *   Suivi de la progression en temps réel (%).
    *   Système de Quiz avec notation automatique.

*   **Administration**
    *   Tableau de bord global avec statistiques (Utilisateurs, Inscriptions, etc.).
    *   Verrouillage/Déverrouillage de comptes utilisateurs.
    *   Journalisation des actions d'administration (Audit Logs).

---

## ⚙️ Installation et Démarrage

### Prérequis
*   JDK 17 ou supérieur
*   Maven 3.8+
*   MySQL 8.0+

### Étapes

1.  **Cloner le dépôt**
    ```bash
    git clone https://github.com/votre-username/e-learning-platform.git
    cd e-learning-platform
    ```

2.  **Configuration Base de Données**
    *   Assurez-vous que MySQL est en cours d'exécution.
    *   Les services sont configurés pour créer automatiquement leurs bases de données (via `createDatabaseIfNotExist=true`). Vérifiez les fichiers `application.yml` de chaque service si nécessaire pour les identifiants (`root`/`password` par défaut souvent, à adapter).

3.  **Compilation**
    Compilez tous les services (à la racine ou dans chaque dossier) :
    ```bash
    mvn clean install
    ```

4.  **Lancement (Ordre Recommandé)**
    Lancez chaque service dans un terminal séparé :
    1.  `discovery-service` (Attendre le démarrage complet)
    2.  `api-gateway`
    3.  `auth-service`
    4.  `catalog-service`
    5.  `student-service`
    6.  `professor-service`
    7.  `learning-service`
    8.  `admin-service`

    ```bash
    cd [nom-du-service]
    mvn spring-boot:run
    ```

---

## 📡 Documentation API

Une collection **Postman** complète est incluse dans le projet pour tester tous les endpoints.

### Exemples d'Endpoints

*   **Auth** : `POST /auth/login`
*   **Cours** : `GET /catalog-service/courses`
*   **Inscription** : `POST /learning-service/enrollments`
*   **Admin** : `GET /admin-service/admin/dashboard/stats`

---

## 👨‍💻 Auteurs

Developpé dans le cadre du projet d'architecture microservices.
