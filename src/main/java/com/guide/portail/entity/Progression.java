package com.guide.portail.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "progressions",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "ressource_id"}))
@Getter
@Setter
@NoArgsConstructor
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
}
