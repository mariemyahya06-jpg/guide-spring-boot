package com.guide.portail.service;

import com.guide.portail.entity.Tutoriel;
import com.guide.portail.repository.TutorielRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TutorielService {

    private final TutorielRepository tutorielRepository;

    public TutorielService(TutorielRepository tutorielRepository) {
        this.tutorielRepository = tutorielRepository;
    }

    public List<Tutoriel> findAll() {
        return tutorielRepository.findAll();
    }

    public Page<Tutoriel> findPage(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return tutorielRepository.findByTitreContainingIgnoreCase(keyword, pageable);
        }
        return tutorielRepository.findAll(pageable);
    }

    public Tutoriel findById(Long id) {
        return tutorielRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Tutoriel introuvable : " + id));
    }

    public Tutoriel save(Tutoriel tutoriel) {
        return tutorielRepository.save(tutoriel);
    }

    public void delete(Long id) {
        tutorielRepository.deleteById(id);
    }
}
