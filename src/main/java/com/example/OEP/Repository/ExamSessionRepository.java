package com.example.OEP.Repository;
import com.example.OEP.Model.ExamSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExamSessionRepository extends JpaRepository<ExamSession, Long> {
    Optional<ExamSession> findByUserIdAndExamId(String userId, Long examId);

}
