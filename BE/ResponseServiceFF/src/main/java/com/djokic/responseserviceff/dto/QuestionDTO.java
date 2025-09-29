package com.djokic.responseserviceff.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class QuestionDTO {
    private Long id;
    private String text;
    private String type; // text, number, multiple_choice, etc.
    private boolean required;
    private int position;
    private Boolean requiredQuestion;
}
