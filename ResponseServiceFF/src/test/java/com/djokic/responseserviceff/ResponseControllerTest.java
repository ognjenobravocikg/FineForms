package com.djokic.responseserviceff;

import com.djokic.responseserviceff.controller.ResponseController;
import com.djokic.responseserviceff.dto.CreateResponseDTO;
import com.djokic.responseserviceff.dto.ResponseDTO;
import com.djokic.responseserviceff.service.ResponseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ResponseController.class)
class ResponseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResponseService responseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateResponse() throws Exception {
        CreateResponseDTO requestDTO = CreateResponseDTO.builder()
                .formId(1L)
                .userId(123L)
                .userEmail("pera@example.com")
                .answers(Map.of("q1", "Da"))
                .build();

        ResponseDTO responseDTO = ResponseDTO.builder()
                .id(1L)
                .formId(1L)
                .userId(123L)
                .userEmail("pera@example.com")
                .answers(Map.of("q1", "Da"))
                .isAuthenticated(true)
                .build();

        when(responseService.createResponse(any(CreateResponseDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/response")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userEmail").value("pera@example.com"))
                .andExpect(jsonPath("$.answers.q1").value("Da"));
    }

    @Test
    void testCreateAnonymousResponse() throws Exception {
        CreateResponseDTO requestDTO = CreateResponseDTO.builder()
                .formId(1L)
                .answers(Map.of("q1", "Ne"))
                .build();

        ResponseDTO responseDTO = ResponseDTO.builder()
                .id(2L)
                .formId(1L)
                .userId(0L)
                .answers(Map.of("q1", "Ne"))
                .isAuthenticated(false)
                .build();

        when(responseService.createAnonymousResponse(any(CreateResponseDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/response/anonymous")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(0))
                .andExpect(jsonPath("$.answers.q1").value("Ne"));
    }
}
