package com.djokic.responseserviceff.mapper;

import com.djokic.responseserviceff.dto.CreateResponseDTO;
import com.djokic.responseserviceff.dto.ResponseDTO;
import com.djokic.responseserviceff.entity.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResponseMapper {

    private final ObjectMapper objectMapper;

    public Response toEntity(CreateResponseDTO dto, Long userId) {
        if (dto == null) return null;

        String answersJson = null;
        try {
            if (dto.getAnswers() != null) {
                answersJson = objectMapper.writeValueAsString(dto.getAnswers());
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to convert answers to JSON", e);
            throw new IllegalArgumentException("Invalid answers format");
        }

        return Response.builder()
                .formId(dto.getFormId())
                .userId(userId != null ? userId : 0L)
                .userEmail(dto.getUserEmail())
                .answers(answersJson)
                .authenticated(userId != null && userId > 0)
                .build();
    }

    public ResponseDTO toDTO(Response response) {
        if (response == null) return null;

        Map<String, Object> answers = null;
        try {
            if (response.getAnswers() != null) {
                answers = objectMapper.readValue(response.getAnswers(), Map.class);
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to parse answers JSON", e);
        }

        return ResponseDTO.builder()
                .id(response.getId())
                .formId(response.getFormId())
                .userId(response.getUserId())
                .userEmail(response.getUserEmail())
                .answers(answers)
                .submittedAt(response.getSubmittedAt())
                .isAuthenticated(response.getAuthenticated())
                .build();
    }
}