package com.guide.portail.repository;

import com.guide.portail.entity.Favori;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriRepository extends JpaRepository<Favori, Long> {
    List<Favori> findByUserId(Long userId);
    Optional<Favori> findByUserIdAndRessourceId(Long userId, Long ressourceId);
    boolean existsByUserIdAndRessourceId(Long userId, Long ressourceId);
    void deleteByUserIdAndRessourceId(Long userId, Long ressourceId);
}
