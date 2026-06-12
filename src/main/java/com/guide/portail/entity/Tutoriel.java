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
@Table(name = "tutoriels")
@Getter
@Setter
@NoArgsConstructor
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
}
