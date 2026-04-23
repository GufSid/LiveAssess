package com.example.OEP.Model;

import jakarta.persistence.*;

@Entity
public class Questions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String question;
    private String image;
    private Long examId;

    public Long getId() { return id; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Long getExamId(){return this.examId;}
    public void setExamId(Long examId){this.examId = examId;}

}
