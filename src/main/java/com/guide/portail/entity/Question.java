package com.guide.portail.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La question est obligatoire")
    @Size(min = 5, max = 1000, message = "La question doit contenir entre 5 et 1000 caracteres")
    @Column(nullable = false, length = 1000)
    private String contenu;

    @Size(max = 2000)
    @Column(length = 2000)
    private String reponse;

    @Column(nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    private LocalDateTime dateReponse;

    /** Etudiant qui a pose la question. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private User etudiant;

    /** Mentor qui a repondu (null tant que pas de reponse). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id")
    private User mentor;

    /** Ressource concernee (facultative). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ressource_id")
    private Ressource ressource;

    public boolean isRepondue() {
        return reponse != null && !reponse.isBlank();
    }

    public Question() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateReponse() {
        return dateReponse;
    }

    public void setDateReponse(LocalDateTime dateReponse) {
        this.dateReponse = dateReponse;
    }

    public User getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(User etudiant) {
        this.etudiant = etudiant;
    }

    public User getMentor() {
        return mentor;
    }

    public void setMentor(User mentor) {
        this.mentor = mentor;
    }

    public Ressource getRessource() {
        return ressource;
    }

    public void setRessource(Ressource ressource) {
        this.ressource = ressource;
    }
}
