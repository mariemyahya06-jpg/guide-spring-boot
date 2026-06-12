package com.guide.portail.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "modules")
@Getter
@Setter
@NoArgsConstructor
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du module est obligatoire")
    @Size(max = 100)
    @Column(nullable = false)
    private String nom;

    /** Niveau : L1, L2, L3, M1, M2 ... (utilise pour la recherche). */
    @NotBlank(message = "Le niveau est obligatoire")
    @Size(max = 20)
    @Column(nullable = false)
    private String niveau;

    @Size(max = 500)
    @Column(length = 500)
    private String description;

    @NotNull(message = "La filiere est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filiere_id", nullable = false)
    private Filiere filiere;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ressource> ressources = new ArrayList<>();

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tutoriel> tutoriels = new ArrayList<>();

    public Module(String nom, String niveau, String description, Filiere filiere) {
        this.nom = nom;
        this.niveau = niveau;
        this.description = description;
        this.filiere = filiere;
    }
}
