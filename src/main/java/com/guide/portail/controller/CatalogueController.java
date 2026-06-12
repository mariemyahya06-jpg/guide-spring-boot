package com.guide.portail.controller;

import com.guide.portail.entity.Ressource;
import com.guide.portail.entity.User;
import com.guide.portail.service.EtudiantService;
import com.guide.portail.service.FiliereService;
import com.guide.portail.service.RessourceService;
import com.guide.portail.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CatalogueController {

    private final RessourceService ressourceService;
    private final FiliereService filiereService;
    private final EtudiantService etudiantService;
    private final UserService userService;

    public CatalogueController(RessourceService ressourceService, FiliereService filiereService,
                               EtudiantService etudiantService, UserService userService) {
        this.ressourceService = ressourceService;
        this.filiereService = filiereService;
        this.etudiantService = etudiantService;
        this.userService = userService;
    }

    /** Recherche et filtrage : filiere, niveau, mot-cle, avec pagination et tri. */
    @GetMapping("/catalogue")
    public String catalogue(@RequestParam(required = false) Long filiereId,
                            @RequestParam(required = false) String niveau,
                            @RequestParam(required = false) String motCle,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "titre") String sort,
                            Model model) {
        Page<Ressource> ressources = ressourceService.search(filiereId, niveau, motCle,
                PageRequest.of(page, 6, Sort.by(sort)));
        model.addAttribute("ressources", ressources);
        model.addAttribute("filieres", filiereService.findAll());
        model.addAttribute("niveaux", java.util.List.of("L1", "L2", "L3", "M1", "M2"));
        model.addAttribute("filiereId", filiereId);
        model.addAttribute("niveau", niveau);
        model.addAttribute("motCle", motCle);
        model.addAttribute("sort", sort);
        return "ressources/search";
    }

    /** Detail d'une ressource. */
    @GetMapping("/ressource/{id}")
    public String detail(@PathVariable Long id, Model model, Authentication auth) {
        Ressource ressource = ressourceService.findById(id);
        model.addAttribute("ressource", ressource);
        model.addAttribute("statuts", com.guide.portail.entity.StatutProgression.values());
        if (auth != null) {
            try {
                User u = userService.findByUsername(auth.getName());
                model.addAttribute("estFavori", etudiantService.estFavori(u.getId(), id));
            } catch (Exception ignored) {
            }
        }
        return "ressources/detail";
    }
}
