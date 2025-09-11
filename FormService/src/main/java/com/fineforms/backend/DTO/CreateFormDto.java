package com.fineforms.backend.DTO;

import java.util.List;

public class CreateFormDto {
    public String title;
    public String description;
    public boolean requiresAuth;
    public List<CreateQuestionDto> questions;
}
