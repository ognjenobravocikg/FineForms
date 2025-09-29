package com.djokic.responseserviceff.entity;

import jakarta.persistence.*;
import lombok.*;


import java.util.List;
import java.util.ArrayList;


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

    private List<Question> questions = new ArrayList<>();

    private List<Collaborator> collaborators;

    public void addQuestion(Question q) {
        q.setForm(this);
        questions.add(q);

    }
    public void removeQuestion(Question q) {
        questions.remove(q);
        q.setForm(null);
    }


}
