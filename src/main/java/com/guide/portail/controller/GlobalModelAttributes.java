package com.guide.portail.controller;

import com.guide.portail.entity.User;
import com.guide.portail.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Expose l'utilisateur connecte a toutes les vues (utile pour la navbar). */
@ControllerAdvice
public class GlobalModelAttributes {

    private final UserService userService;

    public GlobalModelAttributes(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("currentUser")
    public User currentUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getName())) {
            return null;
        }
        try {
            return userService.findByUsername(auth.getName());
        } catch (Exception e) {
            return null;
        }
    }
}
