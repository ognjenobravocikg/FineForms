package com.djokic.responseserviceff.dto;

import lombok.Data;

@Data
public class QuestionDTO {
    private Long id;
    private String text;
    private String type; // text, number, multiple_choice, etc.
    private boolean required;
    private int position;
}
