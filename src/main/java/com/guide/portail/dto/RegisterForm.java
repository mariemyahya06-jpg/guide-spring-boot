package com.guide.portail.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Formulaire d'inscription d'un nouvel etudiant. */
@Getter
@Setter
@NoArgsConstructor
public class RegisterForm {

    @NotBlank(message = "Le nom complet est obligatoire")
    @Size(max = 100)
    private String fullName;

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caracteres")
    private String username;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email n'est pas valide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caracteres")
    private String password;

    @NotBlank(message = "La confirmation est obligatoire")
    private String confirmPassword;

    private Long filiereId;

    private String niveau;
}
