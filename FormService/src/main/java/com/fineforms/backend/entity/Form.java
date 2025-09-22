package com.fineforms.backend.entity;

import jakarta.persistence.*;
import lombok.*;


import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "forms")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
    public void removeQuestion(Question q) {
        questions.remove(q);
        q.setForm(null);
    }


}
