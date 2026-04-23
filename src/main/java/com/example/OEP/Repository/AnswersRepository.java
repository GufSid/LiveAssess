package com.example.OEP.Repository;

import com.example.OEP.Model.Answers;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AnswersRepository extends JpaRepository<Answers, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Answers a WHERE a.userId = :userId AND a.examId = :examId AND a.questionId = :questionId")
    void deleteByUserIdAndExamIdAndQuestionId(String userId, Long examId, Long questionId);

    List<Answers> findByUserIdAndExamId(String userId, Long examId);
}

