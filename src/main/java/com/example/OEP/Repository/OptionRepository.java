package com.example.OEP.Repository;

import com.example.OEP.Model.Options;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OptionRepository extends JpaRepository<Options, Long> {
    List<Options> findByQuestionId(Long questionId);
    @Query("SELECT o FROM Options o WHERE o.isCorrect = true AND o.examId = :examId")
    List<Options> findCorrectOptionsByExamId(@Param("examId") Long examId);
}
