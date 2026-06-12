# GUIDE — Portail d'Orientation et de Suivi Étudiant

Application web (projet universitaire) qui accompagne les nouveaux étudiants dans leur
orientation et leur suivi : filières, modules, ressources pédagogiques, tutoriels,
mentorat, favoris, suivi de progression et questions/réponses.

**Stack :** Spring Boot 3.2 · Spring Security · Spring Data JPA · Thymeleaf · Bean Validation · MySQL (H2 en option) · Java 17 · Maven.

---

## 1. Idée du projet

Quand un étudiant arrive à l'université, il a du mal à connaître les filières disponibles,
les modules de chaque filière, où trouver des ressources adaptées à son niveau et comment
contacter un encadrant. **GUIDE** rassemble tout cela dans une seule plateforme organisée :

- l'étudiant **parcourt et recherche** les ressources (par filière, niveau, mot-clé), les
  **ajoute en favoris**, **suit sa progression** et **pose des questions** ;
- le **mentor** publie des ressources et des tutoriels et **répond** aux questions ;
- l'**administrateur** gère tout le contenu et consulte un **journal des actions**.

---

## 2. Technologies utilisées

| Technologie | Rôle |
|-------------|------|
| Java 17 | Langage de programmation |
| Spring Boot 3.2 | Framework principal + serveur Tomcat embarqué |
| Spring Web (MVC) | Contrôleurs et routes HTTP |
| Spring Data JPA | Accès aux données, recherche, pagination, tri |
| Spring Security | Authentification, rôles, hachage BCrypt |
| Thymeleaf (+ extras security) | Moteur de templates HTML |
| Bean Validation | Validation des formulaires |
| MySQL | Base de données principale (persistante) |
| H2 | Base en mémoire (profil de test optionnel) |
| Maven / Maven Wrapper | Build et exécution sans installer Maven |
| Lombok | Réduction du code répétitif (getters/setters) |

---

## 3. Prérequis

- **JDK 17** (ou supérieur)
- **MySQL 8** en service (ou utiliser le profil H2, sans rien installer)
- Connexion internet au **premier lancement** (le wrapper télécharge Maven). Maven n'a pas
  besoin d'être installé : les scripts `mvnw` / `mvnw.cmd` s'en chargent.

---

## 4. Configurer et lancer MySQL

L'application utilise **MySQL par défaut**. La base `guide_db` est **créée automatiquement**
au premier démarrage (option `createDatabaseIfNotExist=true`).

1. Démarrez le service MySQL (via XAMPP/Laragon, ou Services Windows → MySQL → Démarrer).
2. Si votre utilisateur `root` a un mot de passe, renseignez-le dans
   `src/main/resources/application.properties` :
   ```properties
   spring.datasource.username=root
   spring.datasource.password=VOTRE_MOT_DE_PASSE
   ```
   Paramètres par défaut : base `guide_db`, utilisateur `root`, mot de passe vide, port `3306`.
3. (Optionnel) Le fichier `database.sql` à la racine documente la base ; il n'est **pas**
   obligatoire car Hibernate crée et met à jour les tables automatiquement (`ddl-auto=update`).

---

## 5. Lancer le projet

Depuis PowerShell (Windows), à la racine du projet :

```powershell
cd guide-spring-boot
.\mvnw.cmd spring-boot:run
```

Sous Linux / macOS :
```bash
./mvnw spring-boot:run
```

Puis ouvrez : **http://localhost:8080**

Build complet (génère un .jar exécutable dans `target/`) :
```powershell
.\mvnw.cmd clean package
```

---

## 6. Lancer avec H2 (option, sans MySQL)

Pour une démonstration rapide sans installer MySQL (base en mémoire, repartie de zéro à
chaque lancement) :
```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=h2"
```
Console H2 : http://localhost:8080/h2-console (JDBC URL : `jdbc:h2:mem:guide_db`).

---

## 7. Comptes de démonstration

Créés automatiquement au démarrage (classe `DataInitializer`) :

| Rôle | Identifiant | Mot de passe |
|------|-------------|--------------|
| Admin | `admin` | `admin123` |
| Mentor | `mentor` | `mentor123` |
| Étudiant (rempli) | `etudiant` | `etudiant123` |
| Étudiant (vierge) | `mariem` | `mariem123` |

> Ce sont des comptes de **démonstration** uniquement (aucune donnée réelle ni secret).

---

## 8. Rôles et permissions

| Rôle | Permissions |
|------|-------------|
| **ADMIN** | Gère filières, modules, ressources, utilisateurs et questions ; consulte le journal |
| **MENTOR** | Ajoute/modifie ressources et tutoriels ; répond aux questions des étudiants |
| **ETUDIANT** | Parcourt et recherche les ressources, gère ses favoris, suit sa progression, pose des questions |

Protection des routes : `/admin/**` → ADMIN, `/mentor/**` → MENTOR (et ADMIN),
`/etudiant/**` → ETUDIANT. Pages d'erreur dédiées : **403**, **404**, **500**.

---

## 9. Principales fonctionnalités

- **Inscription publique** d'un nouvel étudiant via `/register` (toujours créé en rôle
  **ETUDIANT**, mot de passe haché en BCrypt, enregistré dans MySQL).
- **Connexion** et redirection automatique vers l'espace correspondant au rôle.
- **Catalogue** : recherche par filière / niveau / mot-clé, pagination et tri, cartes cliquables.
- **Détail d'une ressource** : informations + contenu pédagogique lisible **dans l'application**
  (affiché via le bouton « Consulter le contenu », sans lien externe).
- **Espace étudiant** : favoris, « Mes ressources suivies » (statut + progression), questions.
- **Espace mentor** : gestion des ressources/tutoriels et réponses aux questions.
- **CRUD complet** côté admin sur toutes les entités.
- **Suppression sécurisée** des utilisateurs (les données liées sont traitées proprement,
  le compte `admin` principal est protégé, aucune page d'erreur Whitelabel).

### Inscription : étudiants uniquement

La page `/register` crée **toujours** un compte avec le rôle **ETUDIANT** (aucun choix de
rôle n'est exposé). Pour transformer un étudiant en **mentor**, l'administrateur modifie son
rôle depuis **Gestion des utilisateurs** (le changement est enregistré dans MySQL et pris en
compte à la prochaine connexion de l'utilisateur).

### Centre d'administration & Journal

- **Centre d'administration** (`/admin/centre`) : page d'accueil de l'administration, avec des
  cartes cliquables vers chaque section (filières, modules, ressources, utilisateurs,
  questions, journal).
- **Journal d'administration** (`/admin/journal`) : historique des actions (CREATE / UPDATE /
  DELETE) avec date, administrateur, type d'élément et détail.

---

## 10. Structure du projet

```
guide-spring-boot/
├── pom.xml                 · dépendances et configuration Maven
├── mvnw / mvnw.cmd         · Maven Wrapper (lancement sans installer Maven)
├── .mvn/wrapper/           · configuration du wrapper
├── database.sql            · script SQL de référence (informatif)
├── diagramme-de-classes.*  · diagramme des entités (png / svg / mermaid)
├── README.md
└── src/main/
    ├── java/com/guide/portail/
    │   ├── entity/        · User, Role, Filiere, Module, Ressource, Tutoriel,
    │   │                    Question, Favori, Progression, AdminActionLog...
    │   ├── repository/    · interfaces Spring Data JPA
    │   ├── service/       · logique métier
    │   ├── controller/    · contrôleurs MVC
    │   └── config/        · SecurityConfig, DataInitializer, EntityConverters
    └── resources/
        ├── application.properties        · config MySQL (par défaut)
        ├── application-h2.properties      · profil H2 (option)
        ├── static/css/style.css
        └── templates/                    · vues Thymeleaf + fragments + pages d'erreur
```

---

## 11. Ouvrir le projet dans un IDE

- **IntelliJ IDEA** : *File → Open* puis sélectionnez le dossier `guide-spring-boot`
  (projet Maven détecté automatiquement). Exécutez la classe `GuideApplication`.
- **VS Code** : ouvrez le dossier, installez l'extension *Extension Pack for Java*, puis
  lancez `GuideApplication` ou utilisez `./mvnw spring-boot:run`.

---

## 12. Notes

- Les mots de passe sont hachés avec **BCrypt**.
- `DataInitializer` est **idempotent** : il crée les données manquantes et met à jour les
  descriptions/contenus des ressources **sans supprimer** les utilisateurs, favoris,
  progressions, questions ou le journal.
- Le schéma est généré par **Hibernate** (`ddl-auto=update`) ; `database.sql` est fourni à
  titre de référence uniquement.
