package com.fineforms.backend.DTO;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class FormResponseDto {
    private Long id;
    private Long formId;
    private Long userId;
    private Map<String, Object> answers;
    private LocalDateTime submittedAt;
}