package com.guide.portail.service;

import com.guide.portail.entity.Ressource;
import com.guide.portail.repository.RessourceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RessourceService {

    private final RessourceRepository ressourceRepository;

    public RessourceService(RessourceRepository ressourceRepository) {
        this.ressourceRepository = ressourceRepository;
    }

    public List<Ressource> findAll() {
        return ressourceRepository.findAll();
    }

    /** Recherche multicriteres + pagination + tri. Les chaines vides sont converties en null. */
    public Page<Ressource> search(Long filiereId, String niveau, String motCle, Pageable pageable) {
        String n = (niveau != null && !niveau.isBlank()) ? niveau : null;
        String m = (motCle != null && !motCle.isBlank()) ? motCle : null;
        return ressourceRepository.search(filiereId, n, m, pageable);
    }

    public Ressource findById(Long id) {
        return ressourceRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Ressource introuvable : " + id));
    }

    public Ressource save(Ressource ressource) {
        return ressourceRepository.save(ressource);
    }

    public void delete(Long id) {
        ressourceRepository.deleteById(id);
    }
}
