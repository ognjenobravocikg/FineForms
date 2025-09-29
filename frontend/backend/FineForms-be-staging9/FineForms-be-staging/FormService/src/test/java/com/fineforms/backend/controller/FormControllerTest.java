package com.fineforms.backend.controller;

import com.fineforms.backend.DTO.CreateFormDto;
import com.fineforms.backend.DTO.FormDTO;
import com.fineforms.backend.entity.Form;
import com.fineforms.backend.service.FormService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FormController.class)
@Import(FormControllerTest.TestConfig.class)
class FormControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FormService formService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        FormService formService() {
            return mock(FormService.class);
        }
    }

    @Test
    void createForm_ShouldReturnCreatedForm() throws Exception {
        CreateFormDto dto = CreateFormDto.builder().title("Test Form").ownerId(1L).build();
        Form form = Form.builder().id(1L).title("Test Form").build();

        when(formService.createForm(any(CreateFormDto.class))).thenReturn(form);

        mockMvc.perform(post("/form")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Form"));
    }

    @Test
    void getForm_ShouldReturnForm() throws Exception {
        Form form = Form.builder().id(1L).title("Test Form").build();
        when(formService.getForm(1L)).thenReturn(form);

        mockMvc.perform(get("/form/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Form"));
    }

    @Test
    void getAllForms_ShouldReturnFormList() throws Exception {
        List<Form> forms = List.of(
                Form.builder().id(1L).title("Form 1").build(),
                Form.builder().id(2L).title("Form 2").build()
        );
        when(formService.getAllForms()).thenReturn(forms);

        mockMvc.perform(get("/form"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Form 1"))
                .andExpect(jsonPath("$[1].title").value("Form 2"));
    }

    @Test
    void updateForm_ShouldReturnUpdatedForm() throws Exception {
        CreateFormDto dto = CreateFormDto.builder().title("Updated Form").build();
        Form updatedForm = Form.builder().id(1L).title("Updated Form").build();

        when(formService.updateForm(eq(1L), any(CreateFormDto.class), eq(1L))).thenReturn(updatedForm);

        mockMvc.perform(put("/form/1")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Form"));
    }

    @Test
    void deleteForm_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/form/1")
                        .param("userId", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getPublicForm_ShouldReturnForm() throws Exception {
        FormDTO formDTO = FormDTO.builder()
                .id(1L)
                .title("Public Form")
                .requiresAuth(false)
                .build();

        when(formService.getPublicForm(1L)).thenReturn(formDTO);

        mockMvc.perform(get("/form/public/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Public Form"));
    }
}
