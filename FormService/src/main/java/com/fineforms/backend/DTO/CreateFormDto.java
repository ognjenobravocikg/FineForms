package com.fineforms.backend.DTO;

import lombok.*;
import java.util.List;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateFormDto {
    private Long id; //form ID, null for new form
    private String title;
    private String description;
    private boolean requiresAuth; //If true user must be logged in
    private int maxResponses; //0 for unlimited
    private boolean locked; //If true, form is not accepting responses
    private Long createdBy; //user ID of creator
    private List<CreateQuestionDto> questions;
    private List<CollaboratorDto> collaborators;
}
