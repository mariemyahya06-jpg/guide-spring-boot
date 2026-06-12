-- ============================================================
--  GUIDE - Script de creation de la base MySQL (reference)
-- ============================================================
--  IMPORTANT : Hibernate (spring.jpa.hibernate.ddl-auto=update) cree et met a
--  jour les tables AUTOMATIQUEMENT au demarrage. Ce script n'est donc PAS
--  obligatoire ; il sert uniquement a :
--    - creer la base manuellement si vous le souhaitez,
--    - documenter le nom de la base et les relations.
--
--  Parametres utilises par l'application (application.properties) :
--    Base        : guide_db   (creee automatiquement si absente)
--    Utilisateur : root
--    Mot de passe: (vide par defaut - a adapter selon votre installation MySQL)
--    Port        : 3306
-- ============================================================

CREATE DATABASE IF NOT EXISTS guide_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE guide_db;

-- Les tables sont generees par JPA/Hibernate :
--   users (id, username, full_name, email, password, role, enabled, niveau, filiere_id)
--   filieres (id, nom, description)
--   modules  (id, nom, niveau, description, filiere_id)
--   ressources (id, titre, description, type, url, mots_cles, contenu, date_creation, module_id, auteur_id)
--   tutoriels  (id, titre, contenu, video_url, date_creation, module_id, auteur_id)
--   favoris      (id, user_id, ressource_id, date_ajout)
--   progressions (id, user_id, ressource_id, statut, pourcentage, date_maj)
--   questions    (id, contenu, reponse, date_creation, date_reponse, etudiant_id, mentor_id, ressource_id)
--
-- Relations principales :
--   filieres (1) ----< (N) modules (1) ----< (N) ressources / tutoriels
--   users (1) ----< (N) favoris / progressions >---- (1) ressources
--   users[etudiant] (1) ----< (N) questions >---- (1) users[mentor]
--
-- Donnees de demonstration (comptes admin / mentor / etudiant / mariem,
-- filieres, modules, ressources, etc.) : inserees au 1er demarrage par
-- la classe DataInitializer si la base est vide.
--
-- Les nouveaux etudiants peuvent aussi creer leur compte via la page /register
-- (role ETUDIANT uniquement, mot de passe encode en BCrypt, stocke dans MySQL).
