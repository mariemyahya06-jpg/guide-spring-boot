package com.guide.portail.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "ressources")
@Getter
@Setter
@NoArgsConstructor
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
}
