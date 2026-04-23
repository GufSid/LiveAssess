package com.example.OEP.Repository;

import com.example.OEP.Model.CorrectAnswers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CorrectAnswerRepository extends JpaRepository<CorrectAnswers, Long> {
    List<CorrectAnswers> findByQuestionId(Long questionId);
}