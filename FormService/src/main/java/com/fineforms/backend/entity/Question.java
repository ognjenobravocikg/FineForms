package com.fineforms.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;
    private boolean requiredQuestion;
    private String type;
    private int position;
    private String imageUrl;
    private int numberMin;
    private int numberMax;
    private int numberStep;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id")
    private Form form;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();

    public void addOption(Option o){
        o.setQuestion(this);
        options.add(o);
    }
    public void removeOption(Option o) {
        options.remove(o);
        o.setQuestion(null);
    }

}
