package com.guide.portail.service;

import com.guide.portail.entity.Question;
import com.guide.portail.entity.User;
import com.guide.portail.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<Question> findAll() {
        return questionRepository.findAllByOrderByDateCreationDesc();
    }

    public List<Question> findEnAttente() {
        return questionRepository.findByReponseIsNullOrderByDateCreationDesc();
    }

    public List<Question> findByEtudiant(Long etudiantId) {
        return questionRepository.findByEtudiantIdOrderByDateCreationDesc(etudiantId);
    }

    public Question findById(Long id) {
        return questionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Question introuvable : " + id));
    }

    public Question poser(Question question, User etudiant) {
        question.setEtudiant(etudiant);
        question.setDateCreation(LocalDateTime.now());
        return questionRepository.save(question);
    }

    public Question repondre(Long questionId, String reponse, User mentor) {
        Question q = findById(questionId);
        q.setReponse(reponse);
        q.setMentor(mentor);
        q.setDateReponse(LocalDateTime.now());
        return questionRepository.save(q);
    }

    public void delete(Long id) {
        questionRepository.deleteById(id);
    }
}
