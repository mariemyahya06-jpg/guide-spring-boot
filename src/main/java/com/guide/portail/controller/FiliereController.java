package com.guide.portail.controller;

import com.guide.portail.entity.Filiere;
import com.guide.portail.service.FiliereService;
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
@RequestMapping("/admin/filieres")
public class FiliereController {

    private final FiliereService filiereService;
    private final AdminLogService adminLogService;

    public FiliereController(FiliereService filiereService, AdminLogService adminLogService) {
        this.filiereService = filiereService;
        this.adminLogService = adminLogService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "nom") String sort,
                       Model model) {
        Page<Filiere> filieres = filiereService.findPage(keyword,
                PageRequest.of(page, 5, Sort.by(sort)));
        model.addAttribute("filieres", filieres);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        return "filieres/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("filiere", new Filiere());
        return "filieres/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("filiere", filiereService.findById(id));
        return "filieres/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("filiere") Filiere filiere,
                       BindingResult result, RedirectAttributes ra, Authentication auth) {
        if (result.hasErrors()) {
            return "filieres/form";
        }
        boolean isNew = filiere.getId() == null;
        filiereService.save(filiere);
        adminLogService.log(auth.getName(), isNew ? "CREATE" : "UPDATE", "Filiere",
                filiere.getNom(), (isNew ? "Creation" : "Modification") + " de la filiere " + filiere.getNom());
        ra.addFlashAttribute("success", "Operation effectuee avec succes");
        return "redirect:/admin/filieres";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra, Authentication auth) {
        try {
            String nom = filiereService.findById(id).getNom();
            filiereService.delete(id);
            adminLogService.log(auth.getName(), "DELETE", "Filiere", nom,
                    "Suppression de la filiere " + nom);
            ra.addFlashAttribute("success", "Operation effectuee avec succes");
        } catch (Exception e) {
            ra.addFlashAttribute("error",
                    "Impossible de supprimer : cet element est lie a d'autres donnees.");
        }
        return "redirect:/admin/filieres";
    }
}
