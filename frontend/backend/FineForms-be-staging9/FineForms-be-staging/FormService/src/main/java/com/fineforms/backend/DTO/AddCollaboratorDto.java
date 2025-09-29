package com.fineforms.backend.DTO;

import com.fineforms.backend.enums.CollaboratorRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AddCollaboratorDto {
    private Long userId;
    private CollaboratorRole role;
    private Long currentUserId;
}
