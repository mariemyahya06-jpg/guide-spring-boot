package com.guide.portail.repository;

import com.guide.portail.entity.Filiere;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FiliereRepository extends JpaRepository<Filiere, Long> {
    Page<Filiere> findByNomContainingIgnoreCase(String nom, Pageable pageable);

    Optional<Filiere> findByNom(String nom);
}
