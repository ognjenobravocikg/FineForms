package com.djokic.responseserviceff.dto;

import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreateResponseDTO {
    private Long formId;
    private Long userId;//null ako je anonimni korisnik
    private String userEmail;// null ako je anonimni korisnik
    private Map<String, Object> answers;
    private Boolean isAuthenticated;
}