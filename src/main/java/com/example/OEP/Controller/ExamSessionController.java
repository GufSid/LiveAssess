package com.example.OEP.Controller;

import com.example.OEP.Model.Answers;
import com.example.OEP.Model.ExamSession;
import com.example.OEP.Model.Options;
import com.example.OEP.Repository.AnswersRepository;
import com.example.OEP.Repository.ExamSessionRepository;
import com.example.OEP.Repository.OptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/exam")
@CrossOrigin
public class ExamSessionController {

    @Autowired
    private ExamSessionRepository Examrepo;

    @Autowired
    private AnswersRepository answerRepo;

    @Autowired
    private OptionRepository optionRepo;

    @GetMapping("/start")
    public Map<String, Object> startExam(@RequestParam(required = false) String userId, @RequestParam(required = false) Long examId) {
        Map<String, Object> response = new HashMap<>();

        // 1. Basic Validation
        if (userId == null || userId.trim().isEmpty() || examId == null) {
            return Map.of("allowed", false, "message", "Invalid Request: User ID or Exam ID is missing");
        }

        // 2. Fetch Session
        Optional<ExamSession> optional = Examrepo.findByUserIdAndExamId(userId, examId);
        if (optional.isEmpty()) {
            return Map.of("allowed", false, "message", "User '" + userId + "' is not registered for this exam");
        }

        ExamSession session = optional.get();
        LocalDateTime now = LocalDateTime.now();

        // 3. Check Early Start
        if (session.getStartTime() != null && now.isBefore(session.getStartTime())) {
            response.put("allowed", false);
            response.put("early", true);
            response.put("startTime", session.getStartTime().toString());
            response.put("message", "The exam has not started yet.");
            return response;
        }

        // 4. Check Expiration
        if (!session.isCompleted() && session.getEndTime() != null && now.isAfter(session.getEndTime())) {
            session.setCompleted(true);
            session.setIs_attempted(false);
            session.setScore(0.0f);
            Examrepo.save(session);

            return Map.of(
                    "allowed", false,
                    "completed", true,
                    "message", "The exam session has expired."
            );
        }

        // 5. Handle Completed Session (Result View)
        if (session.isCompleted()) {
            response.put("allowed", true);
            response.put("completed", true);
            response.put("score", session.getScore());

            List<Answers> userAnswers = answerRepo.findByUserIdAndExamId(userId, examId);
            List<Options> options = optionRepo.findCorrectOptionsByExamId(examId);

            HashMap<Long, Answers> attemptMap = new HashMap<>();
            for (Answers ans : userAnswers) {
                attemptMap.put(ans.getQuestionId(), ans);
            }

            List<Map<String, Object>> details = new ArrayList<>();
            int correctCount = 0;
            for (Options opt : options) {
                Long qId = opt.getQuestionId();
                String correctKey = opt.getOptionKey();
                String selectedOpt = attemptMap.containsKey(qId) ? attemptMap.get(qId).getSelectedOption() : "";
                boolean isCorrect = correctKey.equalsIgnoreCase(selectedOpt);

                if (isCorrect) correctCount++;

                Map<String, Object> map = new HashMap<>();
                map.put("questionId", qId);
                map.put("selected", selectedOpt);
                map.put("correctOption", correctKey);
                map.put("correct", isCorrect);
                details.add(map);
            }
            response.put("attempted", userAnswers.size());
            response.put("correctCount", correctCount);
            response.put("wrongCount", userAnswers.size() - correctCount);
            response.put("details", details);
            return response;
        }

        // 6. Success Case (Ongoing Exam)
        // ✅ FETCH PROGRESS: Map saved answers for the frontend to restore state
        List<Answers> existingAnswers = answerRepo.findByUserIdAndExamId(userId, examId);
        Map<Long, String> progressMap = new HashMap<>();
        for (Answers a : existingAnswers) {
            progressMap.put(a.getQuestionId(), a.getSelectedOption());
        }

        response.put("allowed", true);
        response.put("completed", false);
        response.put("previousAnswers", progressMap); // 🔥 This sends the data to React
        response.put("score", session.getScore());
        return response;
    }

    @GetMapping("/time")
    public Map<String, Object> getTime(@RequestParam(required = false) String userId, @RequestParam(required = false) Long examId) {
        if (userId == null || examId == null) return Map.of("secondsLeft", 0);

        Optional<ExamSession> optional = Examrepo.findByUserIdAndExamId(userId, examId);
        if (optional.isEmpty()) return Map.of("secondsLeft", 0);

        ExamSession session = optional.get();
        if (session.getEndTime() == null) return Map.of("secondsLeft", 0);

        long secondsLeft = Duration.between(LocalDateTime.now(), session.getEndTime()).getSeconds();
        if (secondsLeft <= 0) {
            secondsLeft = 0;
            if (!session.isCompleted()) {
                session.setCompleted(true);
                Examrepo.save(session);
            }
        }

        Map<String, Object> res = new HashMap<>();
        res.put("secondsLeft", secondsLeft);
        res.put("completed", session.isCompleted());
        res.put("score", session.getScore());
        return res;
    }
    @PostMapping("/log-violation")
    public Map<String, Object> logViolation(@RequestBody Map<String, Object> body) {
        String userId = (String) body.get("userId");
        Long examId = Long.parseLong(body.get("examId").toString());

        Optional<ExamSession> sessionOpt = Examrepo.findByUserIdAndExamId(userId, examId);
        if (sessionOpt.isPresent()) {
            ExamSession session = sessionOpt.get();
            session.setViolations(session.getViolations() + 1);
            Examrepo.save(session);
            return Map.of("count", session.getViolations());
        }
        return Map.of("error", "Session not found");
    }
}
