package com.djokic.apigatewayauthserviceff.dto.formservicedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDTO {
    private Long id;
    private String text;
    private String type;
    private List<OptionDto> options;
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