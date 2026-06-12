package com.guide.portail.controller;

import com.guide.portail.service.QuestionService;
import com.guide.portail.service.AdminLogService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/questions")
public class AdminQuestionController {

    private final QuestionService questionService;
    private final AdminLogService adminLogService;

    public AdminQuestionController(QuestionService questionService, AdminLogService adminLogService) {
        this.questionService = questionService;
        this.adminLogService = adminLogService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("questions", questionService.findAll());
        model.addAttribute("readOnly", true);
        return "questions/list";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra, Authentication auth) {
        try {
            String extrait = questionService.findById(id).getContenu();
            if (extrait != null && extrait.length() > 60) extrait = extrait.substring(0, 60) + "...";
            questionService.delete(id);
            adminLogService.log(auth.getName(), "DELETE", "Question", extrait,
                    "Suppression d'une question");
            ra.addFlashAttribute("success", "Operation effectuee avec succes");
        } catch (Exception e) {
            ra.addFlashAttribute("error",
                    "Impossible de supprimer : cet element est lie a d'autres donnees.");
        }
        return "redirect:/admin/questions";
    }
}
