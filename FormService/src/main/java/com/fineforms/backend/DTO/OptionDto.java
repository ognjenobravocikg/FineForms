package com.fineforms.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionDto {
    private String text; //Regular text option
    private int order; //Order of showing
    private boolean isCorrect; //option for quiz
    private String imageUrl; //option for image
    private Long id; //option ID
    private Long questionId; //ID of the question this option belongs to
}
