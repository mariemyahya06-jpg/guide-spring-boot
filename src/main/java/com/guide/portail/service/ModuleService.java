package com.guide.portail.service;

import com.guide.portail.entity.Module;
import com.guide.portail.repository.ModuleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;

    public ModuleService(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    public List<Module> findAll() {
        return moduleRepository.findAll();
    }

    public Page<Module> findPage(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return moduleRepository.findByNomContainingIgnoreCase(keyword, pageable);
        }
        return moduleRepository.findAll(pageable);
    }

    public List<Module> findByFiliere(Long filiereId) {
        return moduleRepository.findByFiliereId(filiereId);
    }

    public Module findById(Long id) {
        return moduleRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Module introuvable : " + id));
    }

    public Module save(Module module) {
        return moduleRepository.save(module);
    }

    public void delete(Long id) {
        moduleRepository.deleteById(id);
    }
}
