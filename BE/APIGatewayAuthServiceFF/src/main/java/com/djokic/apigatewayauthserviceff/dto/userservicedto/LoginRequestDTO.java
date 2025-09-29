package com.djokic.apigatewayauthserviceff.dto.userservicedto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LoginRequestDTO {
    private String email;
    private String password;
}
