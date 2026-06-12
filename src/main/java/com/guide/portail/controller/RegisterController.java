package com.guide.portail.controller;

import com.guide.portail.dto.RegisterForm;
import com.guide.portail.entity.Role;
import com.guide.portail.entity.User;
import com.guide.portail.service.FiliereService;
import com.guide.portail.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegisterController {

    private final UserService userService;
    private final FiliereService filiereService;

    public RegisterController(UserService userService, FiliereService filiereService) {
        this.userService = userService;
        this.filiereService = filiereService;
    }

    @GetMapping("/register")
    public String showForm(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        prepare(model);
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterForm form,
                           BindingResult result, Model model, RedirectAttributes ra) {

        // 1) Confirmation du mot de passe
        if (form.getPassword() != null && !form.getPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "Match",
                    "Les deux mots de passe ne correspondent pas");
        }
        // 2) Unicite du nom d'utilisateur et de l'email
        if (form.getUsername() != null && userService.usernameExists(form.getUsername())) {
            result.rejectValue("username", "Exists",
                    "Ce nom d'utilisateur est deja utilise");
        }
        if (form.getEmail() != null && userService.emailExists(form.getEmail())) {
            result.rejectValue("email", "Exists", "Cet email est deja utilise");
        }

        if (result.hasErrors()) {
            prepare(model);
            return "register";
        }

        // 3) Creation du compte ETUDIANT (mot de passe encode dans le service)
        User user = new User(form.getUsername(), form.getFullName(), form.getEmail(),
                form.getPassword(), Role.ETUDIANT);
        user.setNiveau(form.getNiveau());
        if (form.getFiliereId() != null) {
            user.setFiliere(filiereService.findById(form.getFiliereId()));
        }
        userService.create(user);

        ra.addFlashAttribute("registered",
                "Compte cree avec succes. Vous pouvez vous connecter.");
        return "redirect:/login";
    }

    private void prepare(Model model) {
        model.addAttribute("filieres", filiereService.findAll());
        model.addAttribute("niveaux", java.util.List.of("L1", "L2", "L3", "M1", "M2"));
    }
}
