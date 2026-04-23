package com.example.OEP.Repository;

import com.example.OEP.Model.Questions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Questions, Long> {
    List<Questions> findByExamId(Long examId);
}

