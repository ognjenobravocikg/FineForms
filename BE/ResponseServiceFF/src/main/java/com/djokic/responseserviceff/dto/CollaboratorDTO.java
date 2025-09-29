package com.djokic.responseserviceff.dto;

import lombok.Data;

@Data
public class CollaboratorDTO {
    private Long id;
    private Long userId;
    private String role; // VIEWER, EDITOR
}