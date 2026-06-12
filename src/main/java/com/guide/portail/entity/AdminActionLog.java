package com.guide.portail.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** Trace simple d'une action effectuee par un administrateur (journal). */
@Entity
@Table(name = "admin_action_logs")
public class AdminActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateHeure = LocalDateTime.now();

    /** Nom d'utilisateur de l'administrateur. */
    @Column(nullable = false)
    private String administrateur;

    /** CREATE / UPDATE / DELETE */
    @Column(nullable = false)
    private String action;

    /** User / Filiere / Module / Ressource / Question */
    @Column(nullable = false)
    private String typeElement;

    private String nomElement;

    @Column(length = 300)
    private String detail;

    public AdminActionLog(String administrateur, String action, String typeElement,
                          String nomElement, String detail) {
        this.dateHeure = LocalDateTime.now();
        this.administrateur = administrateur;
        this.action = action;
        this.typeElement = typeElement;
        this.nomElement = nomElement;
        this.detail = detail;
    }

    public AdminActionLog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }

    public String getAdministrateur() {
        return administrateur;
    }

    public void setAdministrateur(String administrateur) {
        this.administrateur = administrateur;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTypeElement() {
        return typeElement;
    }

    public void setTypeElement(String typeElement) {
        this.typeElement = typeElement;
    }

    public String getNomElement() {
        return nomElement;
    }

    public void setNomElement(String nomElement) {
        this.nomElement = nomElement;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
