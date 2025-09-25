package com.djokic.responseserviceff.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO {
    private Long id;
    private Long formId;
    private Long userId;
    private String userEmail;
    private Map<String, Object> answers;
    private LocalDateTime submittedAt;
    private Boolean isAuthenticated;
}
