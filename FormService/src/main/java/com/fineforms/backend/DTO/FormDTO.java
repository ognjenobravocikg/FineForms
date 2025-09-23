package com.fineforms.backend.DTO;

import lombok.*;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormDTO {
    private Long id;
    private String title;
    private String description;
    private List<QuestionDTO> questions;
    private List<CollaboratorDto> collaborators;
}
