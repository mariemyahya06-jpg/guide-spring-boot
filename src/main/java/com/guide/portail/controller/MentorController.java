package com.guide.portail.controller;

import com.guide.portail.entity.Ressource;
import com.guide.portail.entity.Tutoriel;
import com.guide.portail.entity.User;
import com.guide.portail.service.*;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mentor")
public class MentorController {

    private final RessourceService ressourceService;
    private final TutorielService tutorielService;
    private final ModuleService moduleService;
    private final QuestionService questionService;
    private final UserService userService;

    public MentorController(RessourceService ressourceService, TutorielService tutorielService,
                            ModuleService moduleService, QuestionService questionService,
                            UserService userService) {
        this.ressourceService = ressourceService;
        this.tutorielService = tutorielService;
        this.moduleService = moduleService;
        this.questionService = questionService;
        this.userService = userService;
    }

    private User current(Authentication auth) {
        return userService.findByUsername(auth.getName());
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("nbRessources", ressourceService.findAll().size());
        model.addAttribute("nbTutoriels", tutorielService.findAll().size());
        model.addAttribute("nbQuestionsEnAttente", questionService.findEnAttente().size());
        return "mentor-dashboard";
    }

    // ----- Ressources -----
    @GetMapping("/ressources")
    public String ressources(Model model) {
        model.addAttribute("ressources", ressourceService.findAll());
        return "mentor-ressources";
    }

    @GetMapping("/ressources/new")
    public String newRessource(Model model) {
        model.addAttribute("ressource", new Ressource());
        model.addAttribute("modules", moduleService.findAll());
        return "ressources/form";
    }

    @GetMapping("/ressources/{id}/edit")
    public String editRessource(@PathVariable Long id, Model model) {
        model.addAttribute("ressource", ressourceService.findById(id));
        model.addAttribute("modules", moduleService.findAll());
        return "ressources/form";
    }

    @PostMapping("/ressources/save")
    public String saveRessource(@Valid @ModelAttribute("ressource") Ressource ressource,
                                BindingResult result, Authentication auth, Model model,
                                RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("modules", moduleService.findAll());
            return "ressources/form";
        }
        if (ressource.getAuteur() == null) {
            ressource.setAuteur(current(auth));
        }
        ressourceService.save(ressource);
        ra.addFlashAttribute("success", "Operation effectuee avec succes");
        return "redirect:/mentor/ressources";
    }

    // ----- Tutoriels -----
    @GetMapping("/tutoriels")
    public String tutoriels(Model model) {
        model.addAttribute("tutoriels", tutorielService.findAll());
        return "tutoriels/list";
    }

    @GetMapping("/tutoriels/new")
    public String newTutoriel(Model model) {
        model.addAttribute("tutoriel", new Tutoriel());
        model.addAttribute("modules", moduleService.findAll());
        return "tutoriels/form";
    }

    @GetMapping("/tutoriels/{id}/edit")
    public String editTutoriel(@PathVariable Long id, Model model) {
        model.addAttribute("tutoriel", tutorielService.findById(id));
        model.addAttribute("modules", moduleService.findAll());
        return "tutoriels/form";
    }

    @PostMapping("/tutoriels/save")
    public String saveTutoriel(@Valid @ModelAttribute("tutoriel") Tutoriel tutoriel,
                               BindingResult result, Authentication auth, Model model,
                               RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("modules", moduleService.findAll());
            return "tutoriels/form";
        }
        if (tutoriel.getAuteur() == null) {
            tutoriel.setAuteur(current(auth));
        }
        tutorielService.save(tutoriel);
        ra.addFlashAttribute("success", "Operation effectuee avec succes");
        return "redirect:/mentor/tutoriels";
    }

    @PostMapping("/tutoriels/{id}/delete")
    public String deleteTutoriel(@PathVariable Long id, RedirectAttributes ra) {
        try {
            tutorielService.delete(id);
            ra.addFlashAttribute("success", "Operation effectuee avec succes");
        } catch (Exception e) {
            ra.addFlashAttribute("error",
                    "Impossible de supprimer : cet element est lie a d'autres donnees.");
        }
        return "redirect:/mentor/tutoriels";
    }

    // ----- Questions a repondre -----
    @GetMapping("/questions")
    public String questions(Model model) {
        model.addAttribute("questions", questionService.findAll());
        model.addAttribute("readOnly", false);
        return "questions/list";
    }

    @PostMapping("/questions/{id}/repondre")
    public String repondre(@PathVariable Long id, @RequestParam String reponse,
                           Authentication auth, RedirectAttributes ra) {
        questionService.repondre(id, reponse, current(auth));
        ra.addFlashAttribute("success", "Reponse enregistree avec succes");
        return "redirect:/mentor/questions";
    }
}
