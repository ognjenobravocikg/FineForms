package com.fineforms.backend.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
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

    @ManyToOne
    @JoinColumn(name = "form_id")
    private Form form;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();

    public void addOption(Option o){
        o.setQuestion(this);
        options.add(o);
    }

    // Getters and Setters
    public Long getId() {return id;}
    public String getText() {return text;}
    public void setText(String text) {this.text = text;}
    public boolean isRequiredQuestion() {return requiredQuestion;}
    public void setRequiredQuestion(boolean requiredQuestion) {this.requiredQuestion = requiredQuestion;}
    public String getType() {return type;}
    public void setType(String type) {this.type = type;}
    public int getPostion() {return position;}
    public void setPosition(int position) {this.position = position;}
    public String getImageUrl() {return imageUrl;}
    public void setImageUrl(String imageUrl) {this.imageUrl = imageUrl;}
    public int getNumberMin() {return numberMin;}
    public void setNumberMin(int numberMin) {this.numberMin = numberMin;}
    public int getNumberMax() {return numberMax;}
    public void setNumberMax(int numberMax) {this.numberMax = numberMax;}
    public int getNumberStep() {return numberStep;}
    public void setNumberStep(int numberStep) {this.numberStep = numberStep;}
    public Form getForm() {return form;}
    public void setForm(Form form) {this.form = form;}
    public List<Option> getOptions() {return options;}
}
