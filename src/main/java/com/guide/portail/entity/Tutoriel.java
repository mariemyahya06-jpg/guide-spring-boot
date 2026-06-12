package com.guide.portail.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "tutoriels")
public class Tutoriel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 150)
    @Column(nullable = false)
    private String titre;

    @NotBlank(message = "Le contenu est obligatoire")
    @Size(max = 5000)
    @Column(length = 5000)
    private String contenu;

    @Size(max = 300)
    private String videoUrl;

    @Column(nullable = false)
    private LocalDate dateCreation = LocalDate.now();

    @NotNull(message = "Le module est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auteur_id")
    private User auteur;

    public Tutoriel(String titre, String contenu, String videoUrl, Module module, User auteur) {
        this.titre = titre;
        this.contenu = contenu;
        this.videoUrl = videoUrl;
        this.module = module;
        this.auteur = auteur;
    }

    public Tutoriel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public User getAuteur() {
        return auteur;
    }

    public void setAuteur(User auteur) {
        this.auteur = auteur;
    }
}
