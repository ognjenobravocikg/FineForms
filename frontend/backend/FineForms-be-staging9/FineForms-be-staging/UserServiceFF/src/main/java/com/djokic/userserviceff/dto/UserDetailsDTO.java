package com.djokic.userserviceff.dto;

import com.djokic.userserviceff.enumeration.Role;
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
