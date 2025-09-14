package com.fineforms.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollaboratorDto {
    private Long id; //unique ID for this collaborator entry
    private Long userId; // ID of the user
    private Long formId; //id of form
    private String email; // or username
    private String role; //  viewer, editor
}
