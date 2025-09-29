package com.djokic.apigatewayauthserviceff.dto.responseservicedto;

import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreateResponseDTO {
    private Long formId;
    private Long userId;
    private String userEmail;
    private Map<String, Object> answers;
    private Boolean isAuthenticated;
}