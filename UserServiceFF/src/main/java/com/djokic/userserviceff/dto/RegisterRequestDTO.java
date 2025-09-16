package com.djokic.userserviceff.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class RegisterRequestDTO {

    @NotBlank(message = "E-Mail must be provided !")
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Email must be a valid email address with no spaces"
    )
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    public String getEmail() {
        if(this.email == null){
            return "";
        }

        return email;
    }

    public String getFirstName() {
        if(this.firstName == null){
            return "";
        }

        return firstName;
    }

    public String getLastName() {
        if(this.lastName == null){
            return "";
        }

        return lastName;
    }

    public String getPassword() {
        if(this.password == null){
            return "";
        }

        return password;
    }
}
