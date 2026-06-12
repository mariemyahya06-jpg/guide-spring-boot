package com.guide.portail.repository;

import com.guide.portail.entity.Progression;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgressionRepository extends JpaRepository<Progression, Long> {
    List<Progression> findByUserId(Long userId);
    Optional<Progression> findByUserIdAndRessourceId(Long userId, Long ressourceId);
}
