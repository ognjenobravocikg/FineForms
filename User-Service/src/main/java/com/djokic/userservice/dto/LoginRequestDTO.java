package com.djokic.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @NotBlank(message = "E-Mail must be provided !")
    @Email(message = "Invalid E-Mail address !")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
