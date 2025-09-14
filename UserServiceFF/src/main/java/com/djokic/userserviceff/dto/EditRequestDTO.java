package com.djokic.userserviceff.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EditRequestDTO {

    @Email(message = "Invalid E-Mail address !")
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Email must be a valid email address with no spaces"
    )
    private String email;

    private String firstName;
    private String lastName;
    private String password;
}
