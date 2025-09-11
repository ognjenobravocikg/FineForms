package com.fineforms.backend.DTO;

import java.util.List;

public class CreatteQuestionDto {
    public String text;
    public boolean required;
    public String type; // example: short_text, long_text, multi_choice etc.
    public String imageUrl;

    public int numberMin;
    public int numberMax;
    public int numberStep;

    public List<String> options; // for multi_choice
}
