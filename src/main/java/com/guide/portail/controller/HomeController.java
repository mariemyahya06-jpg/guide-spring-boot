package com.guide.portail.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Authentication auth) {
        if (auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getName())) {
            return "redirect:/dashboard";
        }
        return "welcome";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /** Redirige vers le tableau de bord correspondant au role. */
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth) {
        if (auth == null) {
            return "redirect:/login";
        }
        boolean admin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean mentor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MENTOR"));
        if (admin) return "redirect:/admin";
        if (mentor) return "redirect:/mentor";
        return "redirect:/etudiant";
    }
}
