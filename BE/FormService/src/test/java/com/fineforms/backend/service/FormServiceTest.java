package com.fineforms.backend.service;

import com.fineforms.backend.repo.CollaboratorRepository;
import com.fineforms.backend.DTO.CreateFormDto;
import com.fineforms.backend.DTO.CreateQuestionDto;
import com.fineforms.backend.entity.Form;
import com.fineforms.backend.exceptions.NotAuthorizedException;
import com.fineforms.backend.repo.FormRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FormServiceTest {

    @Mock
    private FormRepository formRepository;

    @Mock
    private CollaboratorService collaboratorService;

    @InjectMocks
    private FormService formService;

    @Mock
    private CollaboratorRepository collaboratorRepository;


    @Test
    void createForm_ShouldCreateFormWithQuestions() {
        // Given
        CreateFormDto dto = CreateFormDto.builder()
                .ownerId(1L)
                .title("Test Form")
                .description("Test Description")
                .requiresAuth(false)
                .questions(List.of(
                        CreateQuestionDto.builder()
                                .text("Question 1")
                                .type("text")
                                .required(true)
                                .build()
                ))
                .build();

        Form savedForm = Form.builder().id(1L).build();
        when(formRepository.save(any(Form.class))).thenReturn(savedForm);

        // When
        Form result = formService.createForm(dto);

        // Then
        assertNotNull(result);
        verify(formRepository, times(1)).save(any(Form.class));
    }

    @Test
    void createForm_WithEmptyQuestions_ShouldCreateForm() {
        // Given
        CreateFormDto dto = CreateFormDto.builder()
                .ownerId(1L)
                .title("Test Form")
                .description("Test Description")
                .requiresAuth(false)
                .questions(Collections.emptyList())
                .build();

        when(formRepository.save(any(Form.class))).thenReturn(Form.builder().id(1L).build());

        // When & Then
        assertDoesNotThrow(() -> formService.createForm(dto));
    }

    @Test
    void getForm_WhenFormExists_ShouldReturnForm() {
        // Given
        Long formId = 1L;
        Form expectedForm = Form.builder().id(formId).title("Test").build();
        when(formRepository.findById(formId)).thenReturn(Optional.of(expectedForm));

        // When
        Form result = formService.getForm(formId);

        // Then
        assertEquals(expectedForm, result);
    }

    @Test
    void getForm_WhenFormNotExists_ShouldThrowException() {
        // Given
        Long formId = 999L;
        when(formRepository.findById(formId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> formService.getForm(formId));
    }

    @Test
    void getAllForms_ShouldReturnAllForms() {
        // Given
        List<Form> expectedForms = List.of(
                Form.builder().id(1L).title("Form 1").build(),
                Form.builder().id(2L).title("Form 2").build()
        );
        when(formRepository.findAll()).thenReturn(expectedForms);

        // When
        List<Form> result = formService.getAllForms();

        // Then
        assertEquals(2, result.size());
        verify(formRepository, times(1)).findAll();
    }

    @Test
    void updateForm_WhenOwner_ShouldUpdateForm() {
        // Given
        Long formId = 1L;
        Long ownerId = 1L;
        Form existingForm = Form.builder().id(formId).ownerId(ownerId).title("Old Title").build();
        CreateFormDto dto = CreateFormDto.builder()
                .title("New Title")
                .description("New Desc")
                .requiresAuth(true)
                .build();

        when(formRepository.findById(formId)).thenReturn(Optional.of(existingForm));
        when(formRepository.save(any(Form.class))).thenReturn(existingForm);

        // When
        Form result = formService.updateForm(formId, dto, ownerId);

        // Then
        verify(formRepository, times(1)).save(existingForm);
    }

    @Test
    void updateForm_WhenNotOwner_ShouldThrowException() {
        // Given
        Long formId = 1L;
        Long ownerId = 1L;
        Long nonOwnerId = 2L;
        Form form = Form.builder().id(formId).ownerId(ownerId).build();

        when(formRepository.findById(formId)).thenReturn(Optional.of(form));

        // When & Then
        assertThrows(NotAuthorizedException.class,
                () -> formService.updateForm(formId, CreateFormDto.builder().build(), nonOwnerId));
    }

    @Test
    void deleteForm_WhenOwner_ShouldDeleteForm() {
        // Given
        Long formId = 1L;
        Long ownerId = 1L;
        Form form = Form.builder().id(formId).ownerId(ownerId).build();

        when(formRepository.findById(formId)).thenReturn(Optional.of(form));

        // When
        formService.deleteForm(formId, ownerId);

        // Then
        verify(formRepository, times(1)).deleteById(formId);
    }
}