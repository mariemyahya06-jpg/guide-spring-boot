package com.guide.portail.repository;

import com.guide.portail.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByEtudiantIdOrderByDateCreationDesc(Long etudiantId);
    List<Question> findByReponseIsNullOrderByDateCreationDesc();
    List<Question> findAllByOrderByDateCreationDesc();

    List<Question> findByMentorId(Long mentorId);
}
