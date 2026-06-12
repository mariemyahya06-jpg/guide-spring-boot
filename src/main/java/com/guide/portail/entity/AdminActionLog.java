package com.guide.portail.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Trace simple d'une action effectuee par un administrateur (journal). */
@Entity
@Table(name = "admin_action_logs")
@Getter
@Setter
@NoArgsConstructor
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
}
