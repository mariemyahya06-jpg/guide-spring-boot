package com.guide.portail.controller;

import com.guide.portail.entity.Role;
import com.guide.portail.entity.User;
import com.guide.portail.service.FiliereService;
import com.guide.portail.service.UserService;
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
@RequestMapping("/admin/users")
public class UserController {

    private final UserService userService;
    private final FiliereService filiereService;
    private final AdminLogService adminLogService;

    public UserController(UserService userService, FiliereService filiereService,
                          AdminLogService adminLogService) {
        this.userService = userService;
        this.filiereService = filiereService;
        this.adminLogService = adminLogService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "username") String sort,
                       Model model) {
        Page<User> users = userService.findPage(keyword,
                PageRequest.of(page, 5, Sort.by(sort)));
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        return "users/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("user", new User());
        prepare(model);
        return "users/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        prepare(model);
        return "users/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("user") User user,
                       BindingResult result,
                       Model model, RedirectAttributes ra, Authentication auth) {
        // Le champ "password" du formulaire contient le mot de passe en clair.
        // En creation il est obligatoire ; en modification, vide = conserver l'ancien.
        if (user.getId() == null && (user.getPassword() == null || user.getPassword().isBlank())) {
            result.rejectValue("password", "NotBlank", "Le mot de passe est obligatoire");
        }
        if (result.hasErrors()) {
            prepare(model);
            return "users/form";
        }
        boolean isNew = user.getId() == null;
        if (isNew) {
            userService.create(user);
        } else {
            userService.update(user.getId(), user, user.getPassword());
        }
        adminLogService.log(auth.getName(), isNew ? "CREATE" : "UPDATE", "User",
                user.getUsername(), (isNew ? "Creation" : "Modification") + " de l'utilisateur "
                        + user.getFullName() + " (" + user.getRole() + ")");
        ra.addFlashAttribute("success", "Operation effectuee avec succes");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra, Authentication auth) {
        try {
            com.guide.portail.entity.User u = userService.findById(id);
            String username = u.getUsername();
            String fullName = u.getFullName();
            userService.delete(id);
            adminLogService.log(auth.getName(), "DELETE", "User", username,
                    "Suppression de l'utilisateur " + fullName);
            ra.addFlashAttribute("success", "Utilisateur supprime avec succes");
        } catch (Exception e) {
            ra.addFlashAttribute("error",
                    "Impossible de supprimer cet utilisateur : " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    private void prepare(Model model) {
        model.addAttribute("roles", Role.values());
        model.addAttribute("filieres", filiereService.findAll());
    }
}
