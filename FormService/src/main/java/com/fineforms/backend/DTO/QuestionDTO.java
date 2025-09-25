package com.fineforms.backend.DTO;

import java.util.List;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDTO {
    private Long id;
    private String text;
    private String type;
    private List<String> options;
    private String imageUrl;
    private boolean required;
    private int numberMin;
    private int numberMax;
    private int numberStep;
    private Integer minRequiredAnswers;
    private Integer maxAllowedAnswers;
    private List<Integer> numericValues;
    private Long formId;
}

