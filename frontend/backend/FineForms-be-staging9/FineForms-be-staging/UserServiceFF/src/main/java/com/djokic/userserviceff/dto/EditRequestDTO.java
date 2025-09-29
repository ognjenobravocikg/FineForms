package com.djokic.userserviceff.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditRequestDTO {
    private String email;
    private String password;

    private String firstName;
    private String lastName;
}
