package com.example.OEP.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "correct_answers")
public class CorrectAnswers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long questionId;
    private String optionKey; // A, B, C, D

    // 🔹 Constructor
    public CorrectAnswers() {}

    // 🔹 Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getOptionKey() {
        return optionKey;
    }

    public void setOptionKey(String optionKey) {   // ✅ IMPORTANT
        this.optionKey = optionKey;
    }
}
