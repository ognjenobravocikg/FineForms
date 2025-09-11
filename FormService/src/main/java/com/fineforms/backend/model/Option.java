package com.fineforms.backend.model;

import jakarta.persistence.*;

@Entity
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;
    private int ordinal;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    // Getters and Setters
    public Long getId() { return id; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public int getOrdinal() { return ordinal; }
    public void setOrdinal(int ordinal) { this.ordinal = ordinal; }
    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
}
