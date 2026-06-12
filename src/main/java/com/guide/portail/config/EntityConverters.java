package com.guide.portail.config;

import com.guide.portail.entity.Filiere;
import com.guide.portail.entity.Module;
import com.guide.portail.repository.FiliereRepository;
import com.guide.portail.repository.ModuleRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Convertit les identifiants (String) envoyes par les formulaires
 * en entites JPA, pour les listes deroulantes (select).
 */
public class EntityConverters {

    @Component
    public static class StringToFiliereConverter implements Converter<String, Filiere> {
        private final FiliereRepository repo;
        public StringToFiliereConverter(FiliereRepository repo) { this.repo = repo; }
        @Override
        public Filiere convert(String source) {
            if (source == null || source.isBlank()) return null;
            return repo.findById(Long.valueOf(source)).orElse(null);
        }
    }

    @Component
    public static class StringToModuleConverter implements Converter<String, Module> {
        private final ModuleRepository repo;
        public StringToModuleConverter(ModuleRepository repo) { this.repo = repo; }
        @Override
        public Module convert(String source) {
            if (source == null || source.isBlank()) return null;
            return repo.findById(Long.valueOf(source)).orElse(null);
        }
    }
}
