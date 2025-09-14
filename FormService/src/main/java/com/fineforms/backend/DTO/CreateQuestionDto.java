package com.fineforms.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateQuestionDto {
    public String text;
    public boolean required;
    public String type; // example: short_text, long_text, multi_choice etc.
    public String imageUrl;

    public int numberMin;
    public int numberMax;
    public int numberStep;

    public List<OptionDto> options; // for multi_choice
}
