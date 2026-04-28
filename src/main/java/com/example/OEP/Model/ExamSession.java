package com.example.OEP.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_session")
public class ExamSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Column(nullable = false)
    private boolean completed = false;   // 🔥 FIXED

    private float score;

    private boolean is_attempted;

    private Long examId;

    private Integer violations = 0;
// Add Getters and Setters

    public ExamSession() {}

    // =========================
    // GETTERS & SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    // 🔥 SAFE BOOLEAN GETTER
    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
    public void setIs_attempted (boolean is_attempted){
        this.is_attempted = is_attempted;
    }
    public boolean getIs_attempted (){
        return is_attempted;
    }
    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public void setViolations(int violations){
        this.violations = violations;
    }
    public int getViolations (){
        return violations;
    }
}
