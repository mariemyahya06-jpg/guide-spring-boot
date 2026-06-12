package com.guide.portail.controller;

import com.guide.portail.entity.Question;
import com.guide.portail.entity.StatutProgression;
import com.guide.portail.entity.User;
import com.guide.portail.service.EtudiantService;
import com.guide.portail.service.QuestionService;
import com.guide.portail.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/etudiant")
public class EtudiantController {

    private final EtudiantService etudiantService;
    private final QuestionService questionService;
    private final UserService userService;

    public EtudiantController(EtudiantService etudiantService, QuestionService questionService,
                              UserService userService) {
        this.etudiantService = etudiantService;
        this.questionService = questionService;
        this.userService = userService;
    }

    private User current(Authentication auth) {
        return userService.findByUsername(auth.getName());
    }

    @GetMapping
    public String dashboard(Authentication auth, Model model) {
        User u = current(auth);
        model.addAttribute("nbFavoris", etudiantService.getFavoris(u.getId()).size());
        model.addAttribute("nbProgressions", etudiantService.getProgressions(u.getId()).size());
        model.addAttribute("nbQuestions", questionService.findByEtudiant(u.getId()).size());
        return "etudiant/dashboard";
    }

    // ----- Favoris -----
    @GetMapping("/favoris")
    public String favoris(Authentication auth, Model model) {
        model.addAttribute("favoris", etudiantService.getFavoris(current(auth).getId()));
        return "etudiant/favoris";
    }

    @PostMapping("/favoris/{ressourceId}/toggle")
    public String toggleFavori(@PathVariable Long ressourceId, Authentication auth,
                               @RequestParam(required = false) String redirect,
                               RedirectAttributes ra) {
        boolean added = etudiantService.toggleFavori(current(auth), ressourceId);
        ra.addFlashAttribute("success", added ? "Ressource ajoutee aux favoris avec succes"
                                              : "Ressource retiree des favoris");
        return "redirect:" + (redirect != null ? redirect : "/etudiant/favoris");
    }

    // ----- Suivi de progression (mise a jour) -----
    @PostMapping("/progression/{ressourceId}/maj")
    public String majProgression(@PathVariable Long ressourceId,
                                 @RequestParam StatutProgression statut,
                                 @RequestParam int pourcentage,
                                 Authentication auth, RedirectAttributes ra) {
        etudiantService.majProgression(current(auth), ressourceId, statut, pourcentage);
        ra.addFlashAttribute("success", "Progression enregistree avec succes");
        return "redirect:/etudiant/suivies";
    }

    /** Ancien chemin conserve : redirige vers la page unique "Mes ressources suivies". */
    @GetMapping("/progression")
    public String progressionRedirect() {
        return "redirect:/etudiant/suivies";
    }

    // ----- Mes ressources suivies (page unique : suivi + progression) -----
    @GetMapping("/suivies")
    public String suivies(Authentication auth, Model model) {
        model.addAttribute("progressions", etudiantService.getProgressions(current(auth).getId()));
        model.addAttribute("statuts", StatutProgression.values());
        return "etudiant/suivies";
    }

    // ----- Questions -----
    @GetMapping("/questions")
    public String questions(Authentication auth, Model model) {
        model.addAttribute("questions", questionService.findByEtudiant(current(auth).getId()));
        model.addAttribute("question", new Question());
        return "etudiant/questions";
    }

    @PostMapping("/questions/poser")
    public String poser(@Valid @ModelAttribute("question") Question question,
                        BindingResult result, Authentication auth, Model model,
                        RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("questions", questionService.findByEtudiant(current(auth).getId()));
            return "etudiant/questions";
        }
        questionService.poser(question, current(auth));
        ra.addFlashAttribute("success", "Question envoyee avec succes");
        return "redirect:/etudiant/questions";
    }
}
