package com.djokic.responseserviceff.service;

import com.djokic.responseserviceff.client.FormServiceClient;
import com.djokic.responseserviceff.dto.CreateResponseDTO;
import com.djokic.responseserviceff.dto.FormDTO;
import com.djokic.responseserviceff.dto.ResponseDTO;
import com.djokic.responseserviceff.entity.Response;
import com.djokic.responseserviceff.mapper.ResponseMapper;
import com.djokic.responseserviceff.repository.ResponseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResponseService {

    private final ResponseRepository responseRepository;
    private final ResponseMapper responseMapper;
    private final FormServiceClient formServiceClient;

    public ResponseDTO createResponse(CreateResponseDTO requestDTO) {
        log.info("Creating response for form: {}, user: {}", requestDTO.getFormId(), requestDTO.getUserId());

        Long userId = requestDTO.getUserId() != null ? requestDTO.getUserId() : 0L;

        Response response = responseMapper.toEntity(requestDTO, userId);
        Response saved = responseRepository.save(response);

        log.info("Response created successfully with ID: {}", saved.getId());
        return responseMapper.toDTO(saved);
    }

    public ResponseDTO createAnonymousResponse(CreateResponseDTO requestDTO) {
        log.info("Creating anonymous response for form: {}", requestDTO.getFormId());

        Response response = responseMapper.toEntity(requestDTO, 0L);
        Response saved = responseRepository.save(response);

        log.info("Anonymous response created successfully with ID: {}", saved.getId());
        return responseMapper.toDTO(saved);
    }

    public List<ResponseDTO> getResponsesByFormId(Long formId, Long currentUserId) {
        log.info("Fetching responses for form: {}, requested by user: {}", formId, currentUserId);
        FormDTO f = formServiceClient.getForm(formId);

        return responseRepository.findByFormId(formId)
                .stream()
                .map(responseMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ResponseDTO getResponseById(Long responseId, Long currentUserId) {
        Response response = responseRepository.findById(responseId)
                .orElseThrow(() -> new RuntimeException("Response not found"));

        if (response.getUserId() > 0 && !response.getUserId().equals(currentUserId)) {
            throw new RuntimeException("Not allowed to view this response!");
        }

        return responseMapper.toDTO(response);
    }

    public void deleteResponse(Long responseId, Long currentUserId) {
        Response response = responseRepository.findById(responseId)
                .orElseThrow(() -> new RuntimeException("Response not found"));

        if (response.getUserId() > 0 && !response.getUserId().equals(currentUserId)) {
            throw new RuntimeException("Not allowed to delete this response!");
        }

        responseRepository.deleteById(responseId);
    }

    public byte[] exportResponsesToCsv(Long formId) {
        List<Response> responses = responseRepository.findByFormId(formId);

        if (responses.isEmpty()) {
            throw new IllegalArgumentException("No responses found for formId: " + formId);
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8), true)) {

            ObjectMapper mapper = new ObjectMapper();

            Set<String> allKeys = responses.stream()
                    .flatMap(r -> {
                        try {
                            Map<String, Object> map = r.getAnswers() != null ?
                                    mapper.readValue(r.getAnswers(), Map.class) :
                                    Map.of();
                            return map.keySet().stream();
                        } catch (Exception e) {
                            return Stream.empty();
                        }
                    })
                    .collect(Collectors.toSet());

            List<String> headers = new ArrayList<>();
            headers.add("id");
            headers.add("userId");
            headers.add("userEmail");
            headers.addAll(allKeys);
            headers.add("isAuthenticated");

            writer.println(headers.stream()
                    .map(h -> "\"" + h + "\"")
                    .collect(Collectors.joining(",")));

            for (Response r : responses) {
                List<String> row = new ArrayList<>();
                row.add(String.valueOf(r.getId()));
                row.add(String.valueOf(r.getUserId() != null ? r.getUserId() : 0));
                row.add("\"" + (r.getUserEmail() != null ? r.getUserEmail().replace("\"","\"\"") : "") + "\"");

                Map<String, Object> answersMap;
                try {
                    answersMap = r.getAnswers() != null ? mapper.readValue(r.getAnswers(), Map.class) : Map.of();
                } catch (Exception e) {
                    answersMap = Map.of();
                }

                for (String key : allKeys) {
                    Object value = answersMap.getOrDefault(key, "");
                    String safeValue = value != null ? value.toString().replace("\"","\"\"") : "";
                    row.add("\"" + safeValue + "\"");
                }

                row.add(String.valueOf(r.isAuthenticated()));

                writer.println(String.join(",", row));
            }

            writer.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error exporting responses to CSV", e);
        }
    }
}
