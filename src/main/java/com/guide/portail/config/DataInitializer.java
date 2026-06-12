package com.guide.portail.config;

import com.guide.portail.entity.*;
import com.guide.portail.entity.Module;
import com.guide.portail.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Initialise des donnees de demonstration de maniere IDEMPOTENTE et met a jour
 * automatiquement les descriptions / contenus des ressources existantes
 * (chaque module a un contenu pedagogique specifique : resume, objectifs, exemple...).
 *
 * Comptes : admin/admin123 . mentor/mentor123 . etudiant/etudiant123 . mariem/mariem123
 */
@Configuration
public class DataInitializer {

    @Bean
    @SuppressWarnings("unused")
    CommandLineRunner initData(UserRepository userRepo,
                               FiliereRepository filiereRepo,
                               ModuleRepository moduleRepo,
                               RessourceRepository ressourceRepo,
                               TutorielRepository tutorielRepo,
                               QuestionRepository questionRepo,
                               FavoriRepository favoriRepo,
                               ProgressionRepository progressionRepo,
                               AdminActionLogRepository adminLogRepo,
                               PasswordEncoder encoder) {
        return args -> {
            Random rnd = new Random(42);

            // ============== MENTORS (idempotents) ==============
            String[][] mentorData = {
                {"mentor",  "Mohamed Lemine Saleh"},
                {"mentor2", "Fatimetou Mint Ahmed"},
                {"mentor3", "Ahmed Vall Mokhtar"},
                {"mentor4", "Khadijetou Mint Ba"},
                {"mentor5", "Sidi Mohamed Abdel"},
            };
            List<User> mentors = new ArrayList<>();
            for (String[] m : mentorData) {
                User u = userRepo.findByUsername(m[0]).orElseGet(() ->
                        userRepo.save(new User(m[0], m[1], m[0] + "@guide.mr",
                                encoder.encode("mentor123"), Role.MENTOR)));
                mentors.add(u);
            }

            // ============== ADMIN (idempotent) ==============
            if (userRepo.findByUsername("admin").isEmpty()) {
                userRepo.save(new User("admin", "Administrateur Systeme", "admin@guide.mr",
                        encoder.encode("admin123"), Role.ADMIN));
            }

            // ============== FILIERES + MODULES + RESSOURCES (idempotent par nom) ==============
            String[][] filieresData = {
                {"Informatique",   "Etude des systemes informatiques et du developpement logiciel."},
                {"Mathematiques",  "Analyse, algebre, probabilites et mathematiques appliquees."},
                {"Physique",       "Mecanique, electromagnetisme, thermodynamique et optique."},
                {"Chimie",         "Chimie generale, organique, analytique et biochimie."},
                {"Biologie",       "Biologie cellulaire, genetique, microbiologie et ecologie."},
                {"Gestion",        "Comptabilite, management, marketing et gestion financiere."},
                {"Economie",       "Microeconomie, macroeconomie et economie internationale."},
                {"Droit",          "Droit civil, penal, constitutionnel et des affaires."},
                {"Anglais",        "Grammaire, comprehension, expression et litterature anglaise."},
                {"Arabe",          "Grammaire, litterature et rhetorique de la langue arabe."},
                {"Reseaux",        "Reseaux informatiques, securite et administration systeme."},
                {"Genie Logiciel", "Conception, tests, UML et gestion de projets logiciels."},
            };
            Map<String, String[][]> modulesParFiliere = new LinkedHashMap<>();
            modulesParFiliere.put("Informatique", new String[][]{
                {"Algorithmique","L1"},{"Programmation Java","L1"},{"Bases de donnees","L2"},
                {"Developpement Web","L2"},{"Systemes d'exploitation","L3"}});
            modulesParFiliere.put("Mathematiques", new String[][]{
                {"Analyse","L1"},{"Algebre lineaire","L1"},{"Probabilites","L2"},
                {"Statistiques","L2"},{"Topologie","L3"}});
            modulesParFiliere.put("Physique", new String[][]{
                {"Mecanique du point","L1"},{"Electromagnetisme","L2"},
                {"Thermodynamique","L2"},{"Optique","L3"}});
            modulesParFiliere.put("Chimie", new String[][]{
                {"Chimie generale","L1"},{"Chimie organique","L2"},
                {"Chimie analytique","L2"},{"Biochimie","L3"}});
            modulesParFiliere.put("Biologie", new String[][]{
                {"Biologie cellulaire","L1"},{"Genetique","L2"},
                {"Microbiologie","L2"},{"Ecologie","L3"}});
            modulesParFiliere.put("Gestion", new String[][]{
                {"Comptabilite generale","L1"},{"Management","L1"},
                {"Marketing","L2"},{"Gestion financiere","L3"}});
            modulesParFiliere.put("Economie", new String[][]{
                {"Microeconomie","L1"},{"Macroeconomie","L2"},
                {"Economie internationale","L3"}});
            modulesParFiliere.put("Droit", new String[][]{
                {"Droit civil","L1"},{"Droit penal","L2"},
                {"Droit constitutionnel","L2"},{"Droit des affaires","L3"}});
            modulesParFiliere.put("Anglais", new String[][]{
                {"Grammaire anglaise","L1"},{"Comprehension ecrite","L1"},
                {"Expression orale","L2"},{"Litterature anglaise","L3"}});
            modulesParFiliere.put("Arabe", new String[][]{
                {"Grammaire arabe","L1"},{"Litterature arabe","L2"},{"Rhetorique","L3"}});
            modulesParFiliere.put("Reseaux", new String[][]{
                {"Reseaux informatiques","L2"},{"Securite reseau","L3"},
                {"Administration systeme","L3"}});
            modulesParFiliere.put("Genie Logiciel", new String[][]{
                {"Genie logiciel","L2"},{"UML et conception","L2"},
                {"Tests logiciels","L3"},{"Gestion de projet","L3"}});

            Map<String, Filiere> filiereByNom = new LinkedHashMap<>();
            List<Ressource> allRessources = new ArrayList<>();
            int filieresAjoutees = 0;

            for (String[] f : filieresData) {
                Filiere existante = filiereRepo.findByNom(f[0]).orElse(null);
                if (existante != null) { filiereByNom.put(f[0], existante); continue; }
                Filiere filiere = filiereRepo.save(new Filiere(f[0], f[1]));
                filiereByNom.put(f[0], filiere);
                filieresAjoutees++;
                Module premierModule = null;
                for (String[] md : modulesParFiliere.get(f[0])) {
                    Module module = moduleRepo.save(new Module(md[0], md[1],
                            "Module de " + md[0] + " (" + md[1] + ") - filiere " + f[0] + ".", filiere));
                    if (premierModule == null) premierModule = module;
                    String[] notions = notions(md[0]);
                    String theme = String.join(", ", notions);
                    String mc = (md[0] + ", " + f[0] + ", " + notions[0]).toLowerCase();

                    Ressource rCours = new Ressource("Cours : " + md[0],
                            "Ce cours presente " + theme + ".",
                            "PDF", "", mc, module, mentors.get(rnd.nextInt(mentors.size())));
                    rCours.setContenu(contenu("PDF", md[0], f[0], notions));
                    allRessources.add(ressourceRepo.save(rCours));

                    Ressource rTd = new Ressource("TD et exercices : " + md[0],
                            "Serie d'exercices corriges portant sur " + theme + ".",
                            "EXERCICES", "", mc, module, mentors.get(rnd.nextInt(mentors.size())));
                    rTd.setContenu(contenu("EXERCICES", md[0], f[0], notions));
                    allRessources.add(ressourceRepo.save(rTd));

                    Ressource rVid = new Ressource("Video : introduction a " + md[0],
                            "Video pedagogique illustrant " + theme + ".",
                            "VIDEO", "", mc, module, mentors.get(rnd.nextInt(mentors.size())));
                    rVid.setContenu(contenu("VIDEO", md[0], f[0], notions));
                    allRessources.add(ressourceRepo.save(rVid));
                }
                if (premierModule != null) {
                    tutorielRepo.save(new Tutoriel("Comment reussir en " + premierModule.getNom(),
                            "Conseils et methode de travail pour bien reussir ce module : organisation, "
                                + "exercices reguliers et revisions.",
                            "", premierModule, mentors.get(rnd.nextInt(mentors.size()))));
                }
            }

            // ============== ETUDIANTS DEMO (idempotents) ==============
            boolean etudiantsExistaient = !userRepo.findByRole(Role.ETUDIANT).isEmpty();
            User etudiantDemo = userRepo.findByUsername("etudiant").orElseGet(() -> {
                User e = new User("etudiant", "Mariem Etudiante", "etudiant@guide.mr",
                        encoder.encode("etudiant123"), Role.ETUDIANT);
                e.setFiliere(filiereByNom.get("Informatique")); e.setNiveau("L1");
                return userRepo.save(e);
            });
            if (userRepo.findByUsername("mariem").isEmpty()) {
                User mariem = new User("mariem", "Mariem (Compte de test)", "mariem@guide.mr",
                        encoder.encode("mariem123"), Role.ETUDIANT);
                mariem.setFiliere(filiereByNom.get("Informatique")); mariem.setNiveau("L1");
                userRepo.save(mariem);
            }

            if (!etudiantsExistaient && !allRessources.isEmpty()) {
                String[] noms = {
                    "Aminetou Sidi","Brahim Ould Cheikh","Khadijetou Ba","Mohamed Abdallahi",
                    "Aicha Mint Mohamed","Sidi Ahmed Taleb","Fatma Zahra Ely","Yacoub Diallo",
                    "Nezha El Moctar","Oumar Sow","Salma Bint Ely","Cheikh Tijane","Vatma Mint Sidi",
                    "Abdoulaye Ba","Zeinabou Ahmed","Moussa Camara","Houda Mohamed","Ismail Ould Brahim",
                    "Leila Mint Abdel","Hamza Konate","Mariem Lemrabott","Saleh Ould Daddah","Nafissa Sy","Bilal Traore"};
                List<Filiere> filieres = new ArrayList<>(filiereByNom.values());
                List<User> etudiants = new ArrayList<>(); etudiants.add(etudiantDemo);
                for (int i = 0; i < noms.length; i++) {
                    String un = "etudiant" + (i + 2);
                    User e = new User(un, noms[i], un + "@guide.mr", encoder.encode("etudiant123"), Role.ETUDIANT);
                    e.setFiliere(filieres.get(rnd.nextInt(filieres.size())));
                    e.setNiveau(new String[]{"L1","L2","L3"}[rnd.nextInt(3)]);
                    etudiants.add(userRepo.save(e));
                }
                for (int i = 0; i < 10 && i < etudiants.size(); i++) {
                    List<Ressource> c = new ArrayList<>(allRessources); Collections.shuffle(c, rnd);
                    int nb = 2 + rnd.nextInt(3);
                    for (int j = 0; j < nb; j++) favoriRepo.save(new Favori(etudiants.get(i), c.get(j)));
                }
                StatutProgression[] st = StatutProgression.values();
                for (int i = 0; i < 10 && i < etudiants.size(); i++) {
                    List<Ressource> c = new ArrayList<>(allRessources); Collections.shuffle(c, rnd);
                    int nb = 2 + rnd.nextInt(3);
                    for (int j = 0; j < nb; j++) {
                        Progression pr = new Progression(etudiants.get(i), c.get(j));
                        StatutProgression s = st[rnd.nextInt(st.length)];
                        pr.setStatut(s);
                        pr.setPourcentage(s == StatutProgression.TERMINE ? 100
                                : s == StatutProgression.EN_COURS ? 20 + rnd.nextInt(70) : 0);
                        progressionRepo.save(pr);
                    }
                }
                String[] qs = {
                    "Quelle est la difference entre une classe et un objet ?",
                    "Comment reviser efficacement pour l'examen de ce module ?",
                    "Pouvez-vous recommander un livre pour approfondir ce cours ?",
                    "Je n'ai pas compris la notion d'heritage, pouvez-vous expliquer ?",
                    "Quels sont les prerequis pour suivre ce module ?",
                    "Y a-t-il des exercices supplementaires disponibles ?",
                    "Comment installer l'environnement de travail necessaire ?",
                    "Quelle est la duree conseillee de revision par jour ?",
                    "Le TD de la semaine derniere est-il evalue ?",
                    "Pouvez-vous donner un exemple concret d'application ?",
                    "Quelle est la meilleure methode pour memoriser les formules ?",
                    "Comment se preparer a l'oral de ce module ?"};
                String[] rs = {
                    "Bonne question. Relisez le cours puis faites les exercices corriges.",
                    "Concentrez-vous sur les exemples du cours et entrainez-vous regulierement.",
                    "Oui, consultez les ressources du module et la video d'introduction.",
                    "L'heritage permet a une classe d'utiliser les attributs et methodes d'une autre.",
                    "Aucun prerequis particulier, mais une bonne base du module precedent aide."};
                for (int i = 0; i < qs.length; i++) {
                    Question q = new Question();
                    q.setContenu(qs[i]);
                    q.setEtudiant(i < 3 ? etudiantDemo : etudiants.get(rnd.nextInt(etudiants.size())));
                    q.setRessource(allRessources.get(rnd.nextInt(allRessources.size())));
                    q.setDateCreation(LocalDateTime.now().minusDays(rnd.nextInt(20)));
                    if (i % 2 == 0) {
                        q.setReponse(rs[rnd.nextInt(rs.length)]);
                        q.setMentor(mentors.get(rnd.nextInt(mentors.size())));
                        q.setDateReponse(q.getDateCreation().plusDays(1));
                    }
                    questionRepo.save(q);
                }
            }

            // ============== MISE A JOUR IDEMPOTENTE DES DESCRIPTIONS / CONTENUS ==============
            int ressourcesMaj = 0;
            for (Ressource r : ressourceRepo.findAllWithModule()) {
                String titre = r.getTitre() == null ? "" : r.getTitre();
                boolean estSeed = titre.startsWith("Cours") || titre.startsWith("TD") || titre.startsWith("Video");
                if (!estSeed) continue;
                // Deja au nouveau format ? (le contenu pedagogique contient "Resume du cours :")
                if (r.getContenu() != null && r.getContenu().contains("Conseils rapides :")) continue;

                String moduleNom = r.getModule().getNom();
                String filiereNom = (r.getModule().getFiliere() != null) ? r.getModule().getFiliere().getNom() : "";
                String[] notions = notions(moduleNom);
                String theme = String.join(", ", notions);
                String type; String newDesc;
                if (titre.startsWith("Cours")) { type = "PDF"; newDesc = "Ce cours presente " + theme + "."; }
                else if (titre.startsWith("TD")) { type = "EXERCICES"; newDesc = "Serie d'exercices corriges portant sur " + theme + "."; }
                else { type = "VIDEO"; newDesc = "Video pedagogique illustrant " + theme + "."; }
                r.setDescription(newDesc);
                r.setContenu(contenu(type, moduleNom, filiereNom, notions));
                ressourceRepo.save(r);
                ressourcesMaj++;
            }
            System.out.println(">>> Ressources mises a jour (descriptions/contenu) : " + ressourcesMaj);

            if (adminLogRepo.count() == 0) {
                adminLogRepo.save(new AdminActionLog("admin", "CREATE", "Systeme", "Initialisation",
                        "Initialisation des donnees de demonstration GUIDE."));
            }
            System.out.println(">>> GUIDE init : " + filieresAjoutees + " filiere(s) ajoutee(s). Total = "
                    + userRepo.count() + " utilisateurs, " + filiereRepo.count() + " filieres, "
                    + moduleRepo.count() + " modules, " + ressourceRepo.count() + " ressources.");
        };
    }

    private static String[] notions(String module) {
        return NOTIONS.getOrDefault(module, new String[]{"les notions essentielles de " + module});
    }

    /** Contenu pedagogique adapte au TYPE de ressource (Cours / TD / Video). */
    private static String contenu(String type, String module, String filiere, String[] notions) {
        String theme = String.join(", ", notions);
        String resume = RESUMES.getOrDefault(module, "Ce module couvre " + theme + ".");
        String exemple = EXEMPLES.get(module);
        String exercice = EXERCICES.getOrDefault(module,
                "Choisissez une notion du module " + module + " et resolvez un exercice simple "
                + "en appliquant la methode vue en cours.");
        StringBuilder sb = new StringBuilder();

        switch (type) {
            case "EXERCICES":
                // ---------- TD / Exercices ----------
                sb.append("Objectif du TD :\n");
                sb.append("S'entrainer sur ").append(theme).append(".\n\n");
                sb.append("Notions essentielles :\n");
                for (String n : notions) sb.append("- ").append(n).append("\n");
                sb.append("\nExercice :\n").append(exercice).append("\n");
                sb.append("\nConseils rapides :\n");
                sb.append("- Cherchez d'abord seul avant de regarder la correction.\n");
                sb.append("- Verifiez chaque etape de votre raisonnement.");
                break;
            case "VIDEO":
                // ---------- Video ----------
                sb.append("Ce que vous allez apprendre :\n");
                sb.append("Apres cette video, vous comprendrez ").append(theme).append(".\n\n");
                sb.append("Points abordes dans la video :\n");
                for (String n : notions) sb.append("- ").append(n).append("\n");
                sb.append("\nActivite apres la video :\n");
                sb.append("Resumez en quelques phrases ce que vous avez retenu sur ").append(module)
                  .append(", puis essayez d'expliquer une notion a un camarade.\n");
                sb.append("\nConseils rapides :\n");
                sb.append("- Prenez des notes pendant la video.\n");
                sb.append("- Mettez la video en pause pour refaire les exemples.");
                break;
            default:
                // ---------- Cours (PDF) : mini-lecon ----------
                sb.append("Resume du cours :\n").append(resume).append("\n\n");
                sb.append("Notions essentielles :\n");
                for (String n : notions) sb.append("- ").append(n).append("\n");
                if (exemple != null) sb.append("\nExemple :\n").append(exemple).append("\n");
                sb.append("\nConseils rapides :\n");
                sb.append("- Reliez chaque notion a un exemple concret.\n");
                sb.append("- Resumez le cours avec vos propres mots, puis testez-vous.");
                break;
        }
        return sb.toString();
    }

    private static final Map<String, String[]> NOTIONS = Map.ofEntries(
        Map.entry("Algorithmique", new String[]{"les algorithmes","les variables","les conditions","les boucles","l'analyse de problemes"}),
        Map.entry("Programmation Java", new String[]{"les classes et objets","l'heritage","les interfaces","les exceptions","les collections"}),
        Map.entry("Bases de donnees", new String[]{"les tables et relations","le langage SQL","les cles primaires et etrangeres","la normalisation","les jointures"}),
        Map.entry("Developpement Web", new String[]{"HTML et CSS","JavaScript","le protocole HTTP","les formulaires","le responsive design"}),
        Map.entry("Systemes d'exploitation", new String[]{"les processus","la gestion de la memoire","les fichiers","l'ordonnancement","les threads"}),
        Map.entry("Analyse", new String[]{"les limites","la continuite","la derivation","l'integration","les suites"}),
        Map.entry("Algebre lineaire", new String[]{"les vecteurs","les matrices","les systemes lineaires","les applications lineaires","les determinants"}),
        Map.entry("Probabilites", new String[]{"les evenements","les probabilites conditionnelles","les variables aleatoires","les lois usuelles","l'esperance"}),
        Map.entry("Statistiques", new String[]{"les series statistiques","les moyennes et ecarts-types","les graphiques","la correlation","l'echantillonnage"}),
        Map.entry("Topologie", new String[]{"les ensembles ouverts et fermes","les voisinages","la continuite topologique","la compacite","la connexite"}),
        Map.entry("Mecanique du point", new String[]{"les forces","les lois de Newton","le mouvement","l'energie cinetique et potentielle","la quantite de mouvement"}),
        Map.entry("Electromagnetisme", new String[]{"le champ electrique","le champ magnetique","la loi de Coulomb","les circuits","l'induction"}),
        Map.entry("Thermodynamique", new String[]{"la temperature et la chaleur","les principes de la thermodynamique","les gaz parfaits","l'entropie","les transformations"}),
        Map.entry("Optique", new String[]{"la reflexion et la refraction","les lentilles","les miroirs","la formation des images","les instruments optiques"}),
        Map.entry("Chimie generale", new String[]{"l'atome et la mole","le tableau periodique","les liaisons chimiques","les reactions","la stoechiometrie"}),
        Map.entry("Chimie organique", new String[]{"les fonctions organiques","les hydrocarbures","les mecanismes reactionnels","l'isomerie","la nomenclature"}),
        Map.entry("Chimie analytique", new String[]{"les methodes d'analyse","le dosage et la titrage","les mesures","la precision","l'interpretation des resultats"}),
        Map.entry("Biochimie", new String[]{"les glucides","les lipides","les proteines","les enzymes","le metabolisme"}),
        Map.entry("Biologie cellulaire", new String[]{"la structure de la cellule","les organites","la membrane cellulaire","le noyau","les fonctions cellulaires"}),
        Map.entry("Genetique", new String[]{"l'ADN et l'ARN","les genes","la replication","les mutations","l'heredite"}),
        Map.entry("Microbiologie", new String[]{"les bacteries","les virus","la culture microbienne","l'asepsie","les microorganismes"}),
        Map.entry("Ecologie", new String[]{"les ecosystemes","les chaines alimentaires","la biodiversite","les cycles biogeochimiques","l'environnement"}),
        Map.entry("Comptabilite generale", new String[]{"le bilan","le compte de resultat","les ecritures comptables","le journal","l'actif et le passif"}),
        Map.entry("Management", new String[]{"les fonctions du manager","la planification","l'organisation","la motivation","le leadership"}),
        Map.entry("Marketing", new String[]{"le marche","le comportement du consommateur","le mix marketing (4P)","la segmentation","la communication"}),
        Map.entry("Gestion financiere", new String[]{"l'analyse financiere","les investissements","le financement","la rentabilite","la tresorerie"}),
        Map.entry("Microeconomie", new String[]{"l'offre et la demande","le comportement du consommateur","les couts de production","les marches","l'equilibre"}),
        Map.entry("Macroeconomie", new String[]{"le PIB","l'inflation","le chomage","la politique monetaire","la croissance"}),
        Map.entry("Economie internationale", new String[]{"le commerce international","les echanges","la balance des paiements","les taux de change","la mondialisation"}),
        Map.entry("Droit civil", new String[]{"les regles civiles","les obligations","les contrats","la responsabilite","les biens"}),
        Map.entry("Droit penal", new String[]{"l'infraction","la peine","la responsabilite penale","le proces penal","les sanctions"}),
        Map.entry("Droit constitutionnel", new String[]{"la Constitution","la separation des pouvoirs","les institutions","les droits fondamentaux","l'Etat"}),
        Map.entry("Droit des affaires", new String[]{"les societes commerciales","les contrats commerciaux","le droit des entreprises","la concurrence","les obligations commerciales"}),
        Map.entry("Grammaire anglaise", new String[]{"les temps verbaux","la structure des phrases","les prepositions","les pronoms","l'accord"}),
        Map.entry("Comprehension ecrite", new String[]{"la lecture academique","le vocabulaire","l'analyse de texte","les idees principales","la synthese"}),
        Map.entry("Expression orale", new String[]{"la prononciation","le dialogue","la presentation orale","l'ecoute active","la fluidite"}),
        Map.entry("Litterature anglaise", new String[]{"les auteurs classiques","les genres litteraires","l'analyse de textes","les themes","les periodes litteraires"}),
        Map.entry("Grammaire arabe", new String[]{"la grammaire (nahw)","la conjugaison","la syntaxe","l'analyse grammaticale","les regles de la langue"}),
        Map.entry("Litterature arabe", new String[]{"la poesie arabe","la prose","les grands auteurs","les courants litteraires","l'analyse de textes"}),
        Map.entry("Rhetorique", new String[]{"les figures de style","l'eloquence","la metaphore","l'argumentation","la stylistique"}),
        Map.entry("Reseaux informatiques", new String[]{"le modele OSI et TCP/IP","les adresses IP","le routage","les protocoles","les equipements reseau"}),
        Map.entry("Securite reseau", new String[]{"les pare-feux","le chiffrement","les attaques courantes","l'authentification","la protection des donnees"}),
        Map.entry("Administration systeme", new String[]{"la gestion des systemes","les utilisateurs et permissions","les services","la supervision","les sauvegardes"}),
        Map.entry("Genie logiciel", new String[]{"le cycle de vie logiciel","les methodes agiles","la specification des besoins","la conception","la qualite logicielle"}),
        Map.entry("UML et conception", new String[]{"les diagrammes de classes","les cas d'utilisation","les diagrammes de sequence","la modelisation","la conception orientee objet"}),
        Map.entry("Tests logiciels", new String[]{"les tests unitaires","les tests d'integration","les tests fonctionnels","la couverture de code","la detection de bugs"}),
        Map.entry("Gestion de projet", new String[]{"la planification","le diagramme de Gantt","la gestion des risques","les ressources","le suivi d'avancement"})
    );

    private static final Map<String, String> RESUMES = Map.ofEntries(
        Map.entry("Algorithmique", "Un algorithme est une suite d'instructions permettant de resoudre un probleme. On y manipule des variables, des conditions (si/sinon) et des boucles (pour/tant que) pour controler l'execution et traiter les donnees."),
        Map.entry("Programmation Java", "Java est un langage oriente objet ou tout est organise en classes et objets. L'heritage permet de reutiliser du code, les interfaces definissent des contrats, et les exceptions gerent les erreurs a l'execution."),
        Map.entry("Bases de donnees", "Une base de donnees permet de stocker et d'organiser des informations dans des tables faites de lignes et de colonnes. Les cles primaires identifient chaque enregistrement, les cles etrangeres creent des relations, et le langage SQL sert a interroger les donnees."),
        Map.entry("Developpement Web", "Le developpement web combine HTML pour la structure, CSS pour la mise en forme et JavaScript pour l'interactivite. Le navigateur communique avec le serveur via le protocole HTTP pour afficher des pages dynamiques."),
        Map.entry("Systemes d'exploitation", "Un systeme d'exploitation gere les ressources de l'ordinateur : il cree et ordonnance les processus, alloue la memoire et organise les fichiers. Il fait le lien entre le materiel et les applications."),
        Map.entry("Analyse", "L'analyse etudie les fonctions a travers les notions de limite, de continuite et de derivee. L'integration permet de calculer des aires et de retrouver une fonction a partir de sa derivee."),
        Map.entry("Algebre lineaire", "L'algebre lineaire etudie les vecteurs, les matrices et les systemes d'equations lineaires. Les applications lineaires et les determinants servent a resoudre ces systemes et a analyser les transformations."),
        Map.entry("Probabilites", "Les probabilites mesurent la chance qu'un evenement se produise, entre 0 et 1. On y etudie les probabilites conditionnelles, les variables aleatoires et les lois usuelles comme la loi binomiale ou normale."),
        Map.entry("Statistiques", "Les statistiques decrivent et analysent des donnees a l'aide de moyennes, d'ecarts-types et de graphiques. La correlation mesure le lien entre deux variables, et l'echantillonnage estime un resultat sur une population."),
        Map.entry("Topologie", "La topologie etudie les proprietes des espaces qui se conservent par deformation continue. On y manipule les ensembles ouverts et fermes, les voisinages, la compacite et la connexite."),
        Map.entry("Mecanique du point", "La mecanique du point etudie le mouvement des objets sous l'effet des forces. Les lois de Newton relient force, masse et acceleration, et les energies cinetique et potentielle decrivent le mouvement."),
        Map.entry("Electromagnetisme", "L'electromagnetisme etudie les champs electrique et magnetique et leurs interactions. La loi de Coulomb decrit la force entre charges et l'induction explique la production de courant a partir d'un champ variable."),
        Map.entry("Thermodynamique", "La thermodynamique etudie les echanges de chaleur et de travail entre systemes. Ses deux principes decrivent la conservation de l'energie et l'evolution vers le desordre (entropie)."),
        Map.entry("Optique", "L'optique etudie la propagation de la lumiere via la reflexion et la refraction. Les lentilles et les miroirs forment des images, principe utilise dans les instruments optiques."),
        Map.entry("Chimie generale", "La chimie generale etudie la matiere a l'echelle de l'atome et de la mole. Le tableau periodique classe les elements, et les liaisons chimiques expliquent la formation des molecules et des reactions."),
        Map.entry("Chimie organique", "La chimie organique etudie les composes du carbone comme les hydrocarbures et les fonctions organiques. Elle s'interesse aux mecanismes reactionnels, a l'isomerie et a la nomenclature."),
        Map.entry("Chimie analytique", "La chimie analytique regroupe les methodes pour identifier et doser les substances. Le titrage, les mesures precises et l'interpretation des resultats permettent de determiner la composition d'un echantillon."),
        Map.entry("Biochimie", "La biochimie etudie les molecules du vivant : glucides, lipides et proteines. Les enzymes accelerent les reactions du metabolisme qui fournit l'energie a la cellule."),
        Map.entry("Biologie cellulaire", "La biologie cellulaire etudie la cellule, unite de base du vivant. Elle decrit la membrane, le noyau et les organites, et les fonctions assurees par la cellule comme la respiration et la division."),
        Map.entry("Genetique", "La genetique etudie l'heredite et l'information portee par l'ADN et l'ARN. Les genes sont copies lors de la replication, et les mutations peuvent modifier les caracteres transmis."),
        Map.entry("Microbiologie", "La microbiologie etudie les micro-organismes comme les bacteries et les virus. Elle s'interesse a leur culture, a l'asepsie et a leur role dans la sante et l'environnement."),
        Map.entry("Ecologie", "L'ecologie etudie les relations entre les etres vivants et leur milieu. Les ecosystemes reposent sur des chaines alimentaires et des cycles, et la biodiversite assure leur equilibre."),
        Map.entry("Comptabilite generale", "La comptabilite generale enregistre les operations d'une entreprise. Le bilan presente l'actif et le passif, le compte de resultat mesure le benefice ou la perte, et le journal trace les ecritures."),
        Map.entry("Management", "Le management consiste a organiser et diriger une equipe pour atteindre des objectifs. Il repose sur la planification, l'organisation, la motivation et le leadership."),
        Map.entry("Marketing", "Le marketing vise a repondre aux besoins du marche et des consommateurs. Il s'appuie sur le mix marketing (produit, prix, place, promotion) et sur la segmentation pour cibler les clients."),
        Map.entry("Gestion financiere", "La gestion financiere analyse la sante financiere de l'entreprise. Elle etudie les investissements, le financement, la rentabilite et la tresorerie pour assurer l'equilibre."),
        Map.entry("Microeconomie", "La microeconomie etudie les decisions des consommateurs et des entreprises. L'offre et la demande determinent les prix, et l'equilibre du marche resulte de leur rencontre."),
        Map.entry("Macroeconomie", "La macroeconomie etudie l'economie dans son ensemble via des indicateurs comme le PIB, l'inflation et le chomage. La politique monetaire influence la croissance et la stabilite."),
        Map.entry("Economie internationale", "L'economie internationale etudie les echanges entre pays. Elle analyse le commerce international, la balance des paiements et les taux de change dans un contexte de mondialisation."),
        Map.entry("Droit civil", "Le droit civil regit les relations entre personnes privees. Il encadre les obligations, les contrats, la responsabilite et les biens."),
        Map.entry("Droit penal", "Le droit penal definit les infractions et les peines qui les sanctionnent. Il fixe les regles de la responsabilite penale et du deroulement du proces."),
        Map.entry("Droit constitutionnel", "Le droit constitutionnel etudie l'organisation de l'Etat et la Constitution. Il repose sur la separation des pouvoirs et la garantie des droits fondamentaux."),
        Map.entry("Droit des affaires", "Le droit des affaires encadre l'activite des entreprises. Il regit les societes commerciales, les contrats commerciaux et les regles de la concurrence."),
        Map.entry("Grammaire anglaise", "La grammaire anglaise etudie la structure des phrases et l'emploi des temps verbaux. Elle traite aussi des prepositions, des pronoms et des regles d'accord."),
        Map.entry("Comprehension ecrite", "La comprehension ecrite developpe la lecture de textes academiques en anglais. Elle vise a enrichir le vocabulaire, a degager les idees principales et a produire des syntheses."),
        Map.entry("Expression orale", "L'expression orale developpe la capacite a communiquer en anglais. Elle travaille la prononciation, le dialogue, l'ecoute active et la fluidite."),
        Map.entry("Litterature anglaise", "La litterature anglaise etudie les grands auteurs et les genres litteraires. Elle analyse les textes, leurs themes et les periodes qui les ont produits."),
        Map.entry("Grammaire arabe", "La grammaire arabe (nahw) etudie la structure de la phrase et l'analyse grammaticale. Elle traite de la conjugaison, de la syntaxe et des cas (i'rab)."),
        Map.entry("Litterature arabe", "La litterature arabe etudie la poesie et la prose a travers les grands auteurs. Elle analyse les courants litteraires et les textes selon leur epoque."),
        Map.entry("Rhetorique", "La rhetorique (balagha) etudie l'art de bien s'exprimer et de convaincre. Elle traite des figures de style comme la metaphore et de l'argumentation."),
        Map.entry("Reseaux informatiques", "Les reseaux informatiques permettent a des machines de communiquer. Le modele OSI et TCP/IP organise les echanges, les adresses IP identifient les machines et le routage achemine les donnees."),
        Map.entry("Securite reseau", "La securite reseau protege les donnees et les systemes contre les attaques. Elle repose sur les pare-feux, le chiffrement et l'authentification des utilisateurs."),
        Map.entry("Administration systeme", "L'administration systeme consiste a installer, configurer et maintenir un systeme informatique. Elle inclut la gestion des utilisateurs et des permissions, des services et des sauvegardes, ainsi que la surveillance du systeme et la resolution des incidents."),
        Map.entry("Genie logiciel", "Le genie logiciel organise la creation d'un logiciel de qualite. Il couvre le cycle de vie, la specification des besoins, la conception et l'usage de methodes agiles."),
        Map.entry("UML et conception", "UML est un langage de modelisation pour concevoir un logiciel. Les diagrammes de classes, de cas d'utilisation et de sequence representent la structure et le comportement du systeme."),
        Map.entry("Tests logiciels", "Les tests logiciels verifient qu'un programme fonctionne correctement. Les tests unitaires, d'integration et fonctionnels detectent les bugs et mesurent la couverture du code."),
        Map.entry("Gestion de projet", "La gestion de projet planifie et suit l'avancement d'un travail. Elle utilise le diagramme de Gantt, la gestion des risques et la repartition des ressources.")
    );

    private static final Map<String, String> EXEMPLES = Map.ofEntries(
        Map.entry("Algorithmique", "Pour afficher les nombres de 1 a 5 : pour i de 1 a 5, afficher i."),
        Map.entry("Programmation Java", "class Etudiant { String nom; } puis Etudiant e = new Etudiant(); cree un objet a partir de la classe."),
        Map.entry("Bases de donnees", "SELECT nom FROM etudiants WHERE filiere = 'Informatique'; retourne les noms des etudiants en informatique."),
        Map.entry("Developpement Web", "La balise lien (a href) cree un lien, et la regle CSS color: blue; change la couleur du texte."),
        Map.entry("Analyse", "La derivee de f(x) = x^2 est f'(x) = 2x."),
        Map.entry("Algebre lineaire", "Multiplier la matrice identite par un vecteur redonne le meme vecteur."),
        Map.entry("Probabilites", "La probabilite d'obtenir pile avec une piece equilibree est 1/2."),
        Map.entry("Statistiques", "La moyenne de 4, 6 et 8 est (4 + 6 + 8) / 3 = 6."),
        Map.entry("Mecanique du point", "Une force de 10 N sur une masse de 2 kg donne une acceleration de 5 m/s2 (F = m x a)."),
        Map.entry("Microeconomie", "Si le prix d'un bien augmente, la quantite demandee diminue generalement."),
        Map.entry("Comptabilite generale", "Un achat de marchandises au comptant augmente le stock et diminue la tresorerie."),
        Map.entry("Reseaux informatiques", "L'adresse 192.168.1.1 identifie une machine sur un reseau local."),
        Map.entry("Administration systeme", "Creer un utilisateur lui donne acces au systeme avec des permissions definies."),
        Map.entry("Chimie generale", "La reaction 2 H2 + O2 donne 2 H2O respecte la conservation des atomes."),
        Map.entry("Genetique", "Une sequence d'ADN comme ATGC est copiee a l'identique lors de la replication.")
    );
    private static final Map<String, String> EXERCICES = Map.ofEntries(
        Map.entry("Algorithmique", "Ecrivez un algorithme qui calcule la somme des entiers de 1 a n (boucle + variable accumulateur)."),
        Map.entry("Programmation Java", "Creez une classe Etudiant avec les attributs nom et note, puis une methode afficher() qui les affiche."),
        Map.entry("Bases de donnees", "Ecrivez une requete SQL qui affiche les noms des etudiants inscrits en filiere 'Informatique'."),
        Map.entry("Developpement Web", "Creez une page HTML avec un titre et un paragraphe, puis colorez le titre en bleu avec du CSS."),
        Map.entry("Systemes d'exploitation", "Citez trois etats possibles d'un processus et expliquez le passage de 'pret' a 'en execution'."),
        Map.entry("Analyse", "Calculez la derivee de la fonction f(x) = 3x^2 + 2x - 5."),
        Map.entry("Algebre lineaire", "Resolvez le systeme : x + y = 5 et x - y = 1."),
        Map.entry("Probabilites", "On lance un de a six faces. Calculez la probabilite d'obtenir un nombre pair."),
        Map.entry("Statistiques", "Calculez la moyenne et l'etendue de la serie : 10, 12, 14, 16, 18."),
        Map.entry("Topologie", "Montrez que l'intervalle ouvert ]0, 1[ est un ensemble ouvert de R."),
        Map.entry("Mecanique du point", "Un objet de masse 2 kg subit une force de 10 N. Calculez son acceleration (F = m x a)."),
        Map.entry("Electromagnetisme", "Calculez la force entre deux charges de 1 microC separees de 1 m (loi de Coulomb)."),
        Map.entry("Thermodynamique", "Un gaz parfait passe de 300 K a 600 K a volume constant. Comment varie sa pression ?"),
        Map.entry("Optique", "Un objet est a 30 cm d'une lentille convergente de focale 10 cm. L'image est-elle reelle ou virtuelle ?"),
        Map.entry("Chimie generale", "Equilibrez la reaction chimique : H2 + O2 -> H2O."),
        Map.entry("Chimie organique", "Donnez la formule et le nom d'un alcane a 3 atomes de carbone."),
        Map.entry("Chimie analytique", "On dose 20 mL d'un acide par une base de concentration connue : decrivez les etapes du titrage."),
        Map.entry("Biochimie", "Citez les trois grandes familles de biomolecules et donnez un role pour chacune."),
        Map.entry("Biologie cellulaire", "Faites un schema simple d'une cellule et nommez trois organites avec leur role."),
        Map.entry("Genetique", "Donnez la sequence complementaire du brin d'ADN : A-T-G-C."),
        Map.entry("Microbiologie", "Citez deux differences entre une bacterie et un virus."),
        Map.entry("Ecologie", "Construisez une chaine alimentaire simple : un producteur, un herbivore, un carnivore."),
        Map.entry("Comptabilite generale", "Enregistrez au journal un achat de marchandises de 1000 paye en especes."),
        Map.entry("Management", "Citez les quatre fonctions du manager et donnez un exemple pour l'une d'elles."),
        Map.entry("Marketing", "Pour un produit de votre choix, proposez les 4P (produit, prix, place, promotion)."),
        Map.entry("Gestion financiere", "Une entreprise gagne 5000 et depense 3000. Calculez son resultat."),
        Map.entry("Microeconomie", "L'offre est P = Q et la demande P = 10 - Q. Calculez le prix et la quantite d'equilibre."),
        Map.entry("Macroeconomie", "Le PIB passe de 100 a 105. Calculez le taux de croissance en pourcentage."),
        Map.entry("Economie internationale", "Expliquez en deux phrases l'effet d'une hausse du taux de change sur les exportations."),
        Map.entry("Droit civil", "Un voisin casse accidentellement votre fenetre : indiquez le type de responsabilite engagee."),
        Map.entry("Droit penal", "Donnez un exemple d'infraction et indiquez une peine possible."),
        Map.entry("Droit constitutionnel", "Citez les trois pouvoirs de l'Etat et le role de chacun."),
        Map.entry("Droit des affaires", "Citez deux formes de societes commerciales et une difference entre elles."),
        Map.entry("Grammaire anglaise", "Mettez la phrase au passe : 'She goes to school.'"),
        Map.entry("Comprehension ecrite", "Lisez un court paragraphe en anglais et resumez son idee principale en une phrase."),
        Map.entry("Expression orale", "Preparez une presentation orale d'une minute pour vous presenter en anglais."),
        Map.entry("Litterature anglaise", "Choisissez un auteur classique anglais : citez une oeuvre et son theme principal."),
        Map.entry("Grammaire arabe", "Faites l'analyse grammaticale (i'rab) de la phrase arabe : « ذهب الطالب »."),
        Map.entry("Litterature arabe", "Citez un poete arabe celebre et un theme de sa poesie."),
        Map.entry("Rhetorique", "Trouvez une metaphore dans une phrase et expliquez son sens."),
        Map.entry("Reseaux informatiques", "Indiquez la classe de l'adresse IP 192.168.1.10 et si elle est privee ou publique."),
        Map.entry("Securite reseau", "Citez deux mesures simples pour proteger un reseau contre les attaques."),
        Map.entry("Administration systeme", "Ecrivez les etapes pour creer un utilisateur et lui donner acces a un dossier."),
        Map.entry("Genie logiciel", "Citez, dans l'ordre, les grandes etapes du cycle de vie d'un logiciel."),
        Map.entry("UML et conception", "Dessinez un diagramme de classes simple pour 'Etudiant' et 'Cours' avec leur relation."),
        Map.entry("Tests logiciels", "Ecrivez un test simple qui verifie que la fonction somme(2, 3) retourne 5."),
        Map.entry("Gestion de projet", "Listez trois taches d'un projet et placez-les sur un mini diagramme de Gantt.")
    );
}
