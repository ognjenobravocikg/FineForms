package com.djokic.responseserviceff.dto;

import lombok.Data;
import java.util.List;

@Data
public class FormDTO {
    private Long id;
    private Long ownerId;
    private String title;
    private String description;
    private boolean requiresAuth;
    private boolean locked; // da li je forma zaključana
    private List<QuestionDTO> questions;
    private List<CollaboratorDTO> collaborators;
}