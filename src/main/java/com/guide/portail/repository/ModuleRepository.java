package com.guide.portail.repository;

import com.guide.portail.entity.Module;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModuleRepository extends JpaRepository<Module, Long> {
    List<Module> findByFiliereId(Long filiereId);
    Page<Module> findByNomContainingIgnoreCase(String nom, Pageable pageable);
    List<Module> findAllByOrderByNiveauAsc();
}
