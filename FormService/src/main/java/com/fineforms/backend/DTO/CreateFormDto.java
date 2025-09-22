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
    private boolean locked; //If true, form is not accepting responses
    private Long ownerId; //user ID of creator
    private List<CreateQuestionDto> questions;
    private List<CollaboratorDto> collaborators;
}
