package com.guide.portail.controller;

import com.guide.portail.entity.Module;
import com.guide.portail.service.FiliereService;
import com.guide.portail.service.ModuleService;
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
@RequestMapping("/admin/modules")
public class ModuleController {

    private final ModuleService moduleService;
    private final FiliereService filiereService;
    private final AdminLogService adminLogService;

    public ModuleController(ModuleService moduleService, FiliereService filiereService,
                            AdminLogService adminLogService) {
        this.moduleService = moduleService;
        this.filiereService = filiereService;
        this.adminLogService = adminLogService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "nom") String sort,
                       Model model) {
        Page<Module> modules = moduleService.findPage(keyword,
                PageRequest.of(page, 5, Sort.by(sort)));
        model.addAttribute("modules", modules);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        return "modules/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("module", new Module());
        model.addAttribute("filieres", filiereService.findAll());
        return "modules/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("module", moduleService.findById(id));
        model.addAttribute("filieres", filiereService.findAll());
        return "modules/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("module") Module module,
                       BindingResult result, Model model, RedirectAttributes ra, Authentication auth) {
        if (result.hasErrors()) {
            model.addAttribute("filieres", filiereService.findAll());
            return "modules/form";
        }
        boolean isNew = module.getId() == null;
        moduleService.save(module);
        adminLogService.log(auth.getName(), isNew ? "CREATE" : "UPDATE", "Module",
                module.getNom(), (isNew ? "Creation" : "Modification") + " du module " + module.getNom());
        ra.addFlashAttribute("success", "Operation effectuee avec succes");
        return "redirect:/admin/modules";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra, Authentication auth) {
        try {
            String nom = moduleService.findById(id).getNom();
            moduleService.delete(id);
            adminLogService.log(auth.getName(), "DELETE", "Module", nom,
                    "Suppression du module " + nom);
            ra.addFlashAttribute("success", "Operation effectuee avec succes");
        } catch (Exception e) {
            ra.addFlashAttribute("error",
                    "Impossible de supprimer : cet element est lie a d'autres donnees.");
        }
        return "redirect:/admin/modules";
    }
}
