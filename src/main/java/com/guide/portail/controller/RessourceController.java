package com.guide.portail.controller;

import com.guide.portail.entity.Ressource;
import com.guide.portail.service.ModuleService;
import com.guide.portail.service.RessourceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.guide.portail.service.AdminLogService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/ressources")
public class RessourceController {

    private final RessourceService ressourceService;
    private final ModuleService moduleService;
    private final AdminLogService adminLogService;

    public RessourceController(RessourceService ressourceService, ModuleService moduleService,
                               AdminLogService adminLogService) {
        this.ressourceService = ressourceService;
        this.moduleService = moduleService;
        this.adminLogService = adminLogService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "titre") String sort,
                       Model model) {
        Page<Ressource> ressources = ressourceService.search(null, null, keyword,
                PageRequest.of(page, 5, Sort.by(sort)));
        model.addAttribute("ressources", ressources);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        return "ressources/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("ressource", new Ressource());
        model.addAttribute("modules", moduleService.findAll());
        return "ressources/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("ressource", ressourceService.findById(id));
        model.addAttribute("modules", moduleService.findAll());
        return "ressources/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("ressource") Ressource ressource,
                       BindingResult result, Model model, RedirectAttributes ra, Authentication auth) {
        if (result.hasErrors()) {
            model.addAttribute("modules", moduleService.findAll());
            return "ressources/form";
        }
        boolean isNew = ressource.getId() == null;
        ressourceService.save(ressource);
        adminLogService.log(auth.getName(), isNew ? "CREATE" : "UPDATE", "Ressource",
                ressource.getTitre(), (isNew ? "Creation" : "Modification") + " de la ressource " + ressource.getTitre());
        ra.addFlashAttribute("success", "Operation effectuee avec succes");
        return "redirect:/admin/ressources";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra, Authentication auth) {
        try {
            String nom = ressourceService.findById(id).getTitre();
            ressourceService.delete(id);
            adminLogService.log(auth.getName(), "DELETE", "Ressource", nom,
                    "Suppression de la ressource " + nom);
            ra.addFlashAttribute("success", "Operation effectuee avec succes");
        } catch (Exception e) {
            ra.addFlashAttribute("error",
                    "Impossible de supprimer : cet element est lie a d'autres donnees.");
        }
        return "redirect:/admin/ressources";
    }
}
