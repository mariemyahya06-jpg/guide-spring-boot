package com.guide.portail.service;

import com.guide.portail.entity.*;
import com.guide.portail.repository.FavoriRepository;
import com.guide.portail.repository.ProgressionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EtudiantService {

    private final FavoriRepository favoriRepository;
    private final ProgressionRepository progressionRepository;
    private final RessourceService ressourceService;

    public EtudiantService(FavoriRepository favoriRepository,
                           ProgressionRepository progressionRepository,
                           RessourceService ressourceService) {
        this.favoriRepository = favoriRepository;
        this.progressionRepository = progressionRepository;
        this.ressourceService = ressourceService;
    }

    // ---------- Favoris ----------

    public List<Favori> getFavoris(Long userId) {
        return favoriRepository.findByUserId(userId);
    }

    public boolean estFavori(Long userId, Long ressourceId) {
        return favoriRepository.existsByUserIdAndRessourceId(userId, ressourceId);
    }

    @Transactional
    public boolean toggleFavori(User user, Long ressourceId) {
        var existing = favoriRepository.findByUserIdAndRessourceId(user.getId(), ressourceId);
        if (existing.isPresent()) {
            favoriRepository.delete(existing.get());
            return false; // ressource retiree des favoris
        }
        Ressource r = ressourceService.findById(ressourceId);
        favoriRepository.save(new Favori(user, r));
        return true; // ressource ajoutee aux favoris
    }

    // ---------- Progression ----------

    public List<Progression> getProgressions(Long userId) {
        return progressionRepository.findByUserId(userId);
    }

    public Progression majProgression(User user, Long ressourceId, StatutProgression statut, int pourcentage) {
        Progression p = progressionRepository.findByUserIdAndRessourceId(user.getId(), ressourceId)
            .orElseGet(() -> new Progression(user, ressourceService.findById(ressourceId)));
        p.setStatut(statut);
        p.setPourcentage(Math.max(0, Math.min(100, pourcentage)));
        p.setDateMaj(LocalDate.now());
        return progressionRepository.save(p);
    }
}
