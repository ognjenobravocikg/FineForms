package com.fineforms.backend.model;

import jakarta.persistence.*;
import org.graalvm.nativeimage.c.struct.UniqueLocationIdentity;

import java.util.List;
import java.util.ArrayList;

@Entity
public class Form {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ownerId;
    private String title;
    private String description;
    private boolean requiresAuth;

    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    public void addQuestion(Question q) {
        q.setForm(this);
        questions.add(q);

    }

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public Long getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
    public String getTitle(){ return title;}
    public void setTitle(String title){ this.title = title; }
    public String getDescription(){ return description; }
    public void setDescription(String description){ this.description = description; }
    public boolean isRequiresAuth(){ return requiresAuth; }
    public void setRequiresAuth(boolean requiresAuth){ this.requiresAuth = requiresAuth; }
    public List<Question> getQuestions() { return questions;}
}
