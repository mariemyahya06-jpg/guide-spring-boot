package com.guide.portail.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "favoris",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "ressource_id"}))
public class Favori {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ressource_id", nullable = false)
    private Ressource ressource;

    @Column(nullable = false)
    private LocalDate dateAjout = LocalDate.now();

    public Favori(User user, Ressource ressource) {
        this.user = user;
        this.ressource = ressource;
    }

    public Favori() {
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

    public LocalDate getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(LocalDate dateAjout) {
        this.dateAjout = dateAjout;
    }
}
