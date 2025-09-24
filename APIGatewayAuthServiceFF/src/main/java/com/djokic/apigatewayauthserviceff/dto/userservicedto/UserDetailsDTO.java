package com.djokic.apigatewayauthserviceff.dto.userservicedto;

import com.djokic.apigatewayauthserviceff.enumeration.Role;
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
