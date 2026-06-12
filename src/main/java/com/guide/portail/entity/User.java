package com.guide.portail.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caracteres")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Le nom complet est obligatoire")
    @Size(max = 100)
    @Column(nullable = false)
    private String fullName;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email n'est pas valide")
    @Column(nullable = false, unique = true)
    private String email;

    /** Stocke le hash BCrypt. Validation geree dans le controleur (obligatoire a la creation). */
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ETUDIANT;

    @Column(nullable = false)
    private boolean enabled = true;

    /** Niveau de l'etudiant (L1, L2, ...). Renseigne a l'inscription. */
    @Size(max = 20)
    private String niveau;

    /** Filiere de l'etudiant (facultative, utile pour filtrer son catalogue). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filiere_id")
    private Filiere filiere;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favori> favoris = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Progression> progressions = new ArrayList<>();

    @OneToMany(mappedBy = "etudiant")
    private List<Question> questions = new ArrayList<>();

    public User(String username, String fullName, String email, String password, Role role) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
