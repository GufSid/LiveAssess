package com.example.OEP.Controller;
import com.example.OEP.Model.*;
import com.example.OEP.Repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionRepository questionRepo;

    @Autowired
    private OptionRepository optionRepo;

    @Autowired
    private CorrectAnswerRepository correctRepo;

    @Autowired
    private AnswersRepository answerRepo;

    // ✅ 1. ADD QUESTION
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String addQuestion(
            @RequestParam("question") String questionText,
            @RequestParam("examId") Long examId,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam("options") String optionsJson, // Receive options as a JSON string
            @RequestParam("correctAnswers") List<String> correctAnswers) throws IOException {

        // 1. Save Question Image (if exists)
        String questionImagePath = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            questionImagePath = saveImageLocally(imageFile);
        }

        Questions q = new Questions();
        q.setQuestion(questionText);
        q.setImage(questionImagePath); // Store the path/URL
        Questions saved = questionRepo.save(q);

        // 2. Parse Options (using ObjectMapper since it's sent as a String in Multipart)
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, String>> options = mapper.readValue(optionsJson, new TypeReference<List<Map<String, String>>>(){});

        for (Map<String, String> opt : options) {
            Options o = new Options();
            o.setQuestionId(saved.getId());
            o.setOptionKey(opt.get("key"));
            o.setText(opt.get("text"));
            // If options also have images, you'd handle them similarly
            o.setImage(opt.get("image"));
            optionRepo.save(o);
        }

        // 3. Save Correct Answers
        for (String ans : correctAnswers) {
            CorrectAnswers ca = new CorrectAnswers();
            ca.setQuestionId(saved.getId());
            ca.setOptionKey(ans);
            correctRepo.save(ca);
        }

        return "Question Saved with Image";
    }

    // Helper method to save file to a folder
    private String saveImageLocally(MultipartFile file) throws IOException {
        String folder = "uploads/";
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(folder + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());
        return "/images/" + fileName; // Return the URL path
    }

    // ✅ 2. GET QUESTIONS
    @GetMapping("/exam")
    public List<Map<String, Object>> getQuestionsByExam(@RequestParam Long examId) {

        List<Questions> questions = questionRepo.findByExamId(examId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Questions q : questions) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", q.getId());
            map.put("question", q.getQuestion());
            map.put("image", q.getImage());

            List<Options> options = optionRepo.findByQuestionId(q.getId());
            map.put("options", options);
            result.add(map);
        }

        return result;
    }

    // ✅ 3. SUBMIT ANSWERS
    @PostMapping("/submit")
    public String submitAnswers(@RequestBody Map<String, Object> data) {

        String userId = (String) data.get("userId");
        Long examId = (Long) data.get("examId");
        List<Map<String, Object>> answers =
                (List<Map<String, Object>>) data.get("answers");

        for (Map<String, Object> a : answers) {

            Answers ans = new Answers();
            ans.setUserId(userId);
            ans.setQuestionId(Long.valueOf(a.get("questionId").toString()));
            ans.setSelectedOption((String) a.get("selectedOption"));
            ans.setExamId(examId);

            answerRepo.save(ans);
        }

        return "Submitted Successfully";
    }

    // ✅ 4. GET RESULT (COMPARE)
    @GetMapping("/result/{userId}")
    public List<Map<String, Object>> getResult(@PathVariable String userId, @RequestParam Long examId) {

        List<Answers> userAnswers = answerRepo.findByUserIdAndExamId(userId,examId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Answers ans : userAnswers) {

            List<CorrectAnswers> correctList =
                    correctRepo.findByQuestionId(ans.getQuestionId());

            boolean isCorrect = false;

            for (CorrectAnswers ca : correctList) {
                if (ca.getOptionKey().equals(ans.getSelectedOption())) {
                    isCorrect = true;
                    break;
                }
            }

            Map<String, Object> map = new HashMap<>();
            map.put("questionId", ans.getQuestionId());
            map.put("selected", ans.getSelectedOption());
            map.put("correct", isCorrect);
            map.put("examId", examId);
            result.add(map);
        }

        return result;
    }
}
