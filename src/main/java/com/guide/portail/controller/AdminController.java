package com.guide.portail.controller;

import com.guide.portail.entity.Role;
import com.guide.portail.repository.*;
import com.guide.portail.service.AdminLogService;
import com.guide.portail.service.QuestionService;
import com.guide.portail.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final FiliereRepository filiereRepo;
    private final ModuleRepository moduleRepo;
    private final RessourceRepository ressourceRepo;
    private final UserService userService;
    private final QuestionService questionService;
    private final AdminLogService adminLogService;

    public AdminController(FiliereRepository filiereRepo, ModuleRepository moduleRepo,
                           RessourceRepository ressourceRepo, UserService userService,
                           QuestionService questionService, AdminLogService adminLogService) {
        this.filiereRepo = filiereRepo;
        this.moduleRepo = moduleRepo;
        this.ressourceRepo = ressourceRepo;
        this.userService = userService;
        this.questionService = questionService;
        this.adminLogService = adminLogService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("nbFilieres", filiereRepo.count());
        model.addAttribute("nbModules", moduleRepo.count());
        model.addAttribute("nbRessources", ressourceRepo.count());
        model.addAttribute("nbMentors", userService.findByRole(Role.MENTOR).size());
        model.addAttribute("nbEtudiants", userService.findByRole(Role.ETUDIANT).size());
        model.addAttribute("nbQuestions", questionService.findAll().size());
        return "admin-dashboard";
    }

    /** Centre d'administration : hub avec cartes vers chaque section. */
    @GetMapping("/centre")
    public String centre() {
        return "admin-centre";
    }

    /** Journal d'administration : historique des actions. */
    @GetMapping("/journal")
    public String journal(Model model) {
        model.addAttribute("logs", adminLogService.findAll());
        return "admin-journal";
    }
}
