package com.guide.portail.repository;

import com.guide.portail.entity.Ressource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RessourceRepository extends JpaRepository<Ressource, Long> {

    /**
     * Recherche multicriteres : filiere, niveau et mot-cle.
     * Chaque parametre est optionnel (null = ignore). Avec pagination et tri.
     */
    @Query("SELECT r FROM Ressource r WHERE " +
           "(:filiereId IS NULL OR r.module.filiere.id = :filiereId) AND " +
           "(:niveau IS NULL OR r.module.niveau = :niveau) AND " +
           "(:motCle IS NULL OR LOWER(r.titre) LIKE LOWER(CONCAT('%', :motCle, '%')) " +
           "   OR LOWER(r.motsCles) LIKE LOWER(CONCAT('%', :motCle, '%')) " +
           "   OR LOWER(r.description) LIKE LOWER(CONCAT('%', :motCle, '%')))")
    Page<Ressource> search(@Param("filiereId") Long filiereId,
                           @Param("niveau") String niveau,
                           @Param("motCle") String motCle,
                           Pageable pageable);

    List<Ressource> findByModuleFiliereId(Long filiereId);
    List<Ressource> findByModuleId(Long moduleId);

    List<Ressource> findByAuteurId(Long auteurId);

    /** Charge les ressources avec leur module et filiere (evite LazyInitializationException). */
    @Query("SELECT r FROM Ressource r JOIN FETCH r.module m JOIN FETCH m.filiere")
    List<Ressource> findAllWithModule();
}
