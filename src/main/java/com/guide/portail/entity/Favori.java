package com.guide.portail.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "favoris",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "ressource_id"}))
@Getter
@Setter
@NoArgsConstructor
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
}
