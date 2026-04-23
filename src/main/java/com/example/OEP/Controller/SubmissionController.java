package com.example.OEP.Controller;

import com.example.OEP.Model.Answers;
import com.example.OEP.Model.ExamSession;
import com.example.OEP.Model.Options;
import com.example.OEP.Repository.AnswersRepository;
import com.example.OEP.Repository.ExamSessionRepository;
import com.example.OEP.Repository.OptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/api/submission")
public class SubmissionController {

    @Autowired
    private AnswersRepository answerRepo;

    @Autowired
    private OptionRepository optionRepo;

    @Autowired
    private ExamSessionRepository examSessionRepo;

    /**
     * ✅ FEATURE: SAVE PROGRESS
     * Called every time a user clicks an option. Saves or clears progress in real-time.
     */
    @PostMapping("/save-progress")
    public void saveProgress(@RequestBody Map<String, Object> body) {
        String userId = (String) body.get("userId");
        Long examId = Long.parseLong(body.get("examId").toString());
        Long questionId = Long.parseLong(body.get("questionId").toString());
        String selected = (String) body.get("selectedOption");

        // 1. Remove existing answer for this specific question to avoid duplicates
        answerRepo.deleteByUserIdAndExamIdAndQuestionId(userId, examId, questionId);

        // 2. If 'selected' is not empty (user didn't just clear it), save the new choice
        if (selected != null && !selected.trim().isEmpty()) {
            Answers ans = new Answers();
            ans.setUserId(userId);
            ans.setExamId(examId);
            ans.setQuestionId(questionId);
            ans.setSelectedOption(selected);
            answerRepo.save(ans);
        }
    }

    /**
     * ✅ FEATURE: FINAL SUBMIT
     * Calculates the final score based on saved progress and closes the session.
     */
    @PostMapping("/submit")
    public Map<String, Object> submit(@RequestBody Map<String, Object> body) {
        String userId = (String) body.get("userId");
        Object examIdObj = body.get("examId");

        if (examIdObj == null) return Map.of("error", "Exam ID is missing");
        Long examId = Long.parseLong(examIdObj.toString());

        float negativeMark = 0.25f;
        float score = 0;

        // 1. Validate Session
        Optional<ExamSession> sessionOpt = examSessionRepo.findByUserIdAndExamId(userId, examId);
        if (sessionOpt.isEmpty()) return Map.of("error", "No session found for this user.");

        ExamSession session = sessionOpt.get();
        if (session.isCompleted()) return Map.of("error", "Exam already submitted.");

        // 2. Check for Expiry
        if (LocalDateTime.now().isAfter(session.getEndTime())) {
            session.setCompleted(true);
            session.setIs_attempted(false);
            session.setScore(0.0f);
            examSessionRepo.save(session);
            return Map.of("error", "Submission rejected: Time expired.");
        }

        // 3. Fetch all saved answers from DB (saved via /save-progress)
        List<Answers> savedAnswers = answerRepo.findByUserIdAndExamId(userId, examId);
        HashMap<Long, String> userAnsMap = new HashMap<>();
        for (Answers a : savedAnswers) {
            userAnsMap.put(a.getQuestionId(), a.getSelectedOption());
        }

        // 4. Fetch Correct Options for THIS Exam only
        List<Options> correctOptions = optionRepo.findCorrectOptionsByExamId(examId);
        List<Map<String, Object>> details = new ArrayList<>();
        int correctCount = 0;
        int wrongCount = 0;

        for (Options opt : correctOptions) {
            Long qId = opt.getQuestionId();
            String correctKey = opt.getOptionKey();
            String selectedOpt = userAnsMap.getOrDefault(qId, "");

            boolean isCorrect = false;
            if (!selectedOpt.isEmpty()) {
                if (correctKey.equalsIgnoreCase(selectedOpt)) {
                    isCorrect = true;
                    correctCount++;
                    score += 1.0f; // Standard 1 point for correct
                } else {
                    wrongCount++;
                    score -= negativeMark; // Subtract 0.25 for wrong
                }
            }

            // Map data for Frontend review
            Map<String, Object> map = new HashMap<>();
            map.put("questionId", qId);
            map.put("questionText", opt.getText());
            map.put("questionImage", opt.getImage());
            map.put("selected", selectedOpt);
            map.put("correctOption", correctKey);
            map.put("correct", isCorrect);
            details.add(map);
        }

        // 5. Finalise the Session
        session.setScore(score);
        session.setCompleted(true);
        session.setIs_attempted(true);
        examSessionRepo.save(session);

        // 6. Response for Frontend Result Dashboard
        Map<String, Object> response = new HashMap<>();
        response.put("score", score);
        response.put("attempted", userAnsMap.size());
        response.put("correctCount", correctCount);
        response.put("wrongCount", wrongCount);
        response.put("details", details);
        response.put("total", correctOptions.size());

        return response;
    }
}
