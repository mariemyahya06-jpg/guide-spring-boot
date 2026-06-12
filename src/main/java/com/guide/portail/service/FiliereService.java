package com.guide.portail.service;

import com.guide.portail.entity.Filiere;
import com.guide.portail.repository.FiliereRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FiliereService {

    private final FiliereRepository filiereRepository;

    public FiliereService(FiliereRepository filiereRepository) {
        this.filiereRepository = filiereRepository;
    }

    public List<Filiere> findAll() {
        return filiereRepository.findAll();
    }

    public Page<Filiere> findPage(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return filiereRepository.findByNomContainingIgnoreCase(keyword, pageable);
        }
        return filiereRepository.findAll(pageable);
    }

    public Filiere findById(Long id) {
        return filiereRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Filiere introuvable : " + id));
    }

    public Filiere save(Filiere filiere) {
        return filiereRepository.save(filiere);
    }

    public void delete(Long id) {
        filiereRepository.deleteById(id);
    }
}
