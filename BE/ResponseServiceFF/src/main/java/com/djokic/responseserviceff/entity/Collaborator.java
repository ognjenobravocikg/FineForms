package com.djokic.responseserviceff.entity;

import com.djokic.responseserviceff.entity.enums.CollaboratorRole;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Collaborator {
    private Long id;
    private Form form;
    private Long userId;
    private CollaboratorRole role;
}