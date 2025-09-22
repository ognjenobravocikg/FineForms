package com.djokic.apigatewayauthserviceff.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EditRequestDTO {

    private String email;
    private String password;

    private String firstName;
    private String lastName;
}
