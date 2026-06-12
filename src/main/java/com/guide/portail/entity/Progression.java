package com.guide.portail.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

@Entity
@Table(name = "progressions",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "ressource_id"}))
public class Progression {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ressource_id", nullable = false)
    private Ressource ressource;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutProgression statut = StatutProgression.NON_COMMENCE;

    @Min(0)
    @Max(100)
    @Column(nullable = false)
    private int pourcentage = 0;

    @Column(nullable = false)
    private LocalDate dateMaj = LocalDate.now();

    public Progression(User user, Ressource ressource) {
        this.user = user;
        this.ressource = ressource;
    }

    public Progression() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Ressource getRessource() {
        return ressource;
    }

    public void setRessource(Ressource ressource) {
        this.ressource = ressource;
    }

    public StatutProgression getStatut() {
        return statut;
    }

    public void setStatut(StatutProgression statut) {
        this.statut = statut;
    }

    public int getPourcentage() {
        return pourcentage;
    }

    public void setPourcentage(int pourcentage) {
        this.pourcentage = pourcentage;
    }

    public LocalDate getDateMaj() {
        return dateMaj;
    }

    public void setDateMaj(LocalDate dateMaj) {
        this.dateMaj = dateMaj;
    }
}
