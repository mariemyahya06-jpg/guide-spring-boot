package com.guide.portail.service;

import com.guide.portail.entity.Role;
import com.guide.portail.entity.User;
import com.guide.portail.entity.Question;
import com.guide.portail.entity.Ressource;
import com.guide.portail.entity.Tutoriel;
import com.guide.portail.repository.FavoriRepository;
import com.guide.portail.repository.ProgressionRepository;
import com.guide.portail.repository.QuestionRepository;
import com.guide.portail.repository.RessourceRepository;
import com.guide.portail.repository.TutorielRepository;
import com.guide.portail.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FavoriRepository favoriRepository;
    private final ProgressionRepository progressionRepository;
    private final QuestionRepository questionRepository;
    private final RessourceRepository ressourceRepository;
    private final TutorielRepository tutorielRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       FavoriRepository favoriRepository, ProgressionRepository progressionRepository,
                       QuestionRepository questionRepository, RessourceRepository ressourceRepository,
                       TutorielRepository tutorielRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.favoriRepository = favoriRepository;
        this.progressionRepository = progressionRepository;
        this.questionRepository = questionRepository;
        this.ressourceRepository = ressourceRepository;
        this.tutorielRepository = tutorielRepository;
    }

    public Page<User> findPage(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return userRepository
                .findByFullNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(keyword, keyword, pageable);
        }
        return userRepository.findAll(pageable);
    }

    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + id));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + username));
    }

    /** Creation (encode le mot de passe). */
    public User create(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /** Mise a jour. Si rawPassword est vide, on garde l'ancien mot de passe. */
    public User update(Long id, User form, String rawPassword) {
        User existing = findById(id);
        existing.setUsername(form.getUsername());
        existing.setFullName(form.getFullName());
        existing.setEmail(form.getEmail());
        existing.setRole(form.getRole());
        existing.setEnabled(form.isEnabled());
        existing.setFiliere(form.getFiliere());
        if (rawPassword != null && !rawPassword.isBlank()) {
            existing.setPassword(passwordEncoder.encode(rawPassword));
        }
        return userRepository.save(existing);
    }

    /**
     * Suppression securisee d'un utilisateur :
     *  - le compte administrateur principal ("admin") ne peut pas etre supprime ;
     *  - les donnees liees sont d'abord traitees pour eviter toute erreur de cle etrangere :
     *      favoris et progressions  -> supprimes,
     *      questions posees (etudiant) -> supprimees,
     *      questions repondues (mentor) -> detachees (mentor mis a null),
     *      ressources et tutoriels (auteur) -> detaches (auteur mis a null).
     */
    @Transactional
    public void delete(Long id) {
        User user = findById(id);
        if ("admin".equalsIgnoreCase(user.getUsername())) {
            throw new IllegalStateException(
                    "Le compte administrateur principal ne peut pas etre supprime.");
        }

        // Detacher les references (mentor / auteur) pour conserver l'historique
        for (Question q : questionRepository.findByMentorId(id)) {
            q.setMentor(null);
            questionRepository.save(q);
        }
        for (Ressource r : ressourceRepository.findByAuteurId(id)) {
            r.setAuteur(null);
            ressourceRepository.save(r);
        }
        for (Tutoriel t : tutorielRepository.findByAuteurId(id)) {
            t.setAuteur(null);
            tutorielRepository.save(t);
        }

        // Supprimer les donnees possedees par l'utilisateur
        questionRepository.deleteAll(questionRepository.findByEtudiantIdOrderByDateCreationDesc(id));
        favoriRepository.deleteAll(favoriRepository.findByUserId(id));
        progressionRepository.deleteAll(progressionRepository.findByUserId(id));

        userRepository.delete(user);
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
