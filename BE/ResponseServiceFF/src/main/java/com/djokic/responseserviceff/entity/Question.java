package com.djokic.responseserviceff.entity;


import com.djokic.responseserviceff.entity.enums.QuestionTypeEnum;
import lombok.*;

import java.util.List;
import java.util.ArrayList;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    private Long id;

    private String text;
    private boolean requiredQuestion;

    private QuestionTypeEnum type;

    private int position;
    private String imageUrl;
    private int numberMin;
    private int numberMax;
    private int numberStep;


    private Form form;

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