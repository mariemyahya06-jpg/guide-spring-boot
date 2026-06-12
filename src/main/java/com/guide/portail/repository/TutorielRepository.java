package com.guide.portail.repository;

import com.guide.portail.entity.Tutoriel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorielRepository extends JpaRepository<Tutoriel, Long> {

    java.util.List<Tutoriel> findByAuteurId(Long auteurId);
    Page<Tutoriel> findByTitreContainingIgnoreCase(String titre, Pageable pageable);
}
