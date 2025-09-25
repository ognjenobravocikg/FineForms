package com.djokic.responseserviceff.service;

import com.djokic.responseserviceff.dto.*;
import com.djokic.responseserviceff.entity.Response;
import com.djokic.responseserviceff.repository.ResponseRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ResponseService {

    @Autowired
    private ResponseRepository responseRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResponseDTO createResponse(CreateResponseDTO requestDTO) {
        log.info("Creating response for form: {}, user: {}",
                requestDTO.getFormId(), requestDTO.getUserId());

        Long userId = requestDTO.getUserId() != null ? requestDTO.getUserId() : 0L;

        validateDuplicateResponse(requestDTO.getFormId(), userId, requestDTO.getUserEmail());

        Response response = mapToEntity(requestDTO, userId);
        Response saved = responseRepository.save(response);

        log.info("Response created successfully with ID: {}", saved.getId());
        return mapToDTO(saved);
    }

    public List<ResponseDTO> getResponsesByFormId(Long formId, Long currentUserId) {
        log.info("Fetching responses for form: {}, requested by user: {}", formId, currentUserId);

        return responseRepository.findByFormId(formId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ResponseDTO getResponseById(Long responseId, Long currentUserId) {
        Response response = responseRepository.findById(responseId)
                .orElseThrow(() -> new RuntimeException("Response not found"));

        if (response.getUserId() > 0 && !response.getUserId().equals(currentUserId)) {
            throw new RuntimeException("Not allowed to view this response!");
        }

        return mapToDTO(response);
    }

    public void deleteResponse(Long responseId, Long currentUserId) {
        Response response = responseRepository.findById(responseId)
                .orElseThrow(() -> new RuntimeException("Response not found"));

        if (response.getUserId() > 0 && !response.getUserId().equals(currentUserId)) {
            throw new RuntimeException("Not allowed to delete this response!");
        }

        responseRepository.deleteById(responseId);
    }

    public ResponseDTO createAnonymousResponse(CreateResponseDTO requestDTO) {
        log.info("Creating anonymous response for form: {}", requestDTO.getFormId());

        Long userId = 0L;

        validateDuplicateResponse(requestDTO.getFormId(), userId, requestDTO.getUserEmail());

        Response response = mapToEntity(requestDTO, userId);
        Response saved = responseRepository.save(response);

        log.info("Anonymous response created successfully with ID: {}", saved.getId());
        return mapToDTO(saved);
    }

    private Response mapToEntity(CreateResponseDTO dto, Long userId) {
        String answersJson = null;
        try {
            if (dto.getAnswers() != null) {
                answersJson = objectMapper.writeValueAsString(dto.getAnswers());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert answers to JSON", e);
        }

        return Response.builder()
                .formId(dto.getFormId())
                .userId(userId)
                .userEmail(dto.getUserEmail())
                .answers(answersJson)
                .isAuthenticated(userId > 0)
                .build();
    }

    private Response mapToEntity(CreateResponseDTO dto) {
        Long userId = dto.getUserId() != null ? dto.getUserId() : 0L;
        return mapToEntity(dto, userId);
    }

    private void validateDuplicateResponse(Long formId, Long userId, String userEmail) {
        if (userId > 0) {
            if (responseRepository.existsByFormIdAndUserId(formId, userId)) {
                throw new IllegalArgumentException("User has already submitted a response");
            }
        } else {
            if (userEmail != null && responseRepository.existsByFormIdAndUserEmail(formId, userEmail)) {
                throw new IllegalArgumentException("User with this email has already responded");
            }
        }
    }

    private ResponseDTO mapToDTO(Response response) {
        ResponseDTO dto = new ResponseDTO();
        dto.setId(response.getId());
        dto.setFormId(response.getFormId());
        dto.setUserId(response.getUserId());
        dto.setUserEmail(response.getUserEmail());
        dto.setSubmittedAt(response.getSubmittedAt());
        dto.setIsAuthenticated(response.getIsAuthenticated());

        try {
            if (response.getAnswers() != null) {
                dto.setAnswers(objectMapper.readValue(response.getAnswers(), Map.class));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse answers JSON", e);
        }
        return dto;
    }
}