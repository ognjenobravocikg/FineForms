package com.fineforms.backend.DTO;

import com.fineforms.backend.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDetailsDTO {
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Role role;
}
