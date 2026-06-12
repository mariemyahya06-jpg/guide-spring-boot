package com.guide.portail.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "filieres")
public class Filiere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de la filiere est obligatoire")
    @Size(max = 100)
    @Column(nullable = false, unique = true)
    private String nom;

    @Size(max = 500)
    @Column(length = 500)
    private String description;

    @OneToMany(mappedBy = "filiere", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Module> modules = new ArrayList<>();

    public Filiere(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }

    public Filiere() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }
}
