package com.guide.portail.service;

import com.guide.portail.entity.AdminActionLog;
import com.guide.portail.repository.AdminActionLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Journal d'administration : enregistre les actions des administrateurs
 * (CREATE / UPDATE / DELETE) et fournit l'historique pour la page Journal.
 */
@Service
public class AdminLogService {

    private final AdminActionLogRepository repo;

    public AdminLogService(AdminActionLogRepository repo) {
        this.repo = repo;
    }

    /** Enregistre une action d'administration. */
    public void log(String administrateur, String action, String typeElement,
                    String nomElement, String detail) {
        repo.save(new AdminActionLog(administrateur, action, typeElement, nomElement, detail));
    }

    public List<AdminActionLog> findAll() {
        return repo.findAllByOrderByDateHeureDesc();
    }
}
