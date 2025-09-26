package com.fineforms.backend.DTO;

import com.fineforms.backend.enums.QuestionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateQuestionDto {
    private Long id;
    public String text;
    public boolean required;
    private QuestionTypeEnum type;
    public String imageUrl;

    public int numberMin;
    public int numberMax;
    public int numberStep;
    private Integer minRequiredAnswers;
    private Integer maxAllowedAnswers;
    private Long formId;
    public List<OptionDto> options; // for multi_choice
}
