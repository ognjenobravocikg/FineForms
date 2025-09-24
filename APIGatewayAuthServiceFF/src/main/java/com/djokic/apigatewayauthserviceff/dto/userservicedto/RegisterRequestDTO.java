package com.djokic.apigatewayauthserviceff.dto.userservicedto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@Builder
public class RegisterRequestDTO {
    private String email;
    private String password;
    private String firstName;
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
