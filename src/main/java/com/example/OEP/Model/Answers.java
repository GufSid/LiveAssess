package com.example.OEP.Model;

import jakarta.persistence.*;

@Entity
public class Answers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private Long questionId;
    private String selectedOption;
    private Long examId;

    public Answers() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {   // ✅ NOW EXISTS
        this.userId = userId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {  // ✅ NOW EXISTS
        this.selectedOption = selectedOption;
    }
    public Long getExamId(){return this.examId;}
    public void setExamId(Long examId){this.examId = examId;}


}

