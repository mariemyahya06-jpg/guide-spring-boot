package com.guide.portail.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "ressources")
public class Ressource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 150)
    @Column(nullable = false)
    private String titre;

    @Size(max = 1000)
    @Column(length = 1000)
    private String description;

    /** Type : PDF, VIDEO, LIEN, ARTICLE ... */
    @NotBlank(message = "Le type est obligatoire")
    @Size(max = 30)
    private String type;

    @Size(max = 300)
    private String url;

    /** Mots-cles separes par des virgules (utilise pour la recherche). */
    @Size(max = 200)
    private String motsCles;

    /** Contenu interne lisible dans l'application (sections texte). */
    @Size(max = 4000)
    @Column(length = 4000)
    private String contenu;

    @Column(nullable = false)
    private LocalDate dateCreation = LocalDate.now();

    @NotNull(message = "Le module est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    /** Mentor (User) qui a publie la ressource. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auteur_id")
    private User auteur;

    public Ressource(String titre, String description, String type, String url,
                     String motsCles, Module module, User auteur) {
        this.titre = titre;
        this.description = description;
        this.type = type;
        this.url = url;
        this.motsCles = motsCles;
        this.module = module;
        this.auteur = auteur;
    }

    public Ressource() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMotsCles() {
        return motsCles;
    }

    public void setMotsCles(String motsCles) {
        this.motsCles = motsCles;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
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
