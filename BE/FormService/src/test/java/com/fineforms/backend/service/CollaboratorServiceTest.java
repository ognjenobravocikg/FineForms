package com.fineforms.backend.service;

import com.fineforms.backend.entity.Collaborator;
import com.fineforms.backend.entity.Form;
import com.fineforms.backend.enums.CollaboratorRole;
import com.fineforms.backend.repo.CollaboratorRepository;
import com.fineforms.backend.repo.FormRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollaboratorServiceTest {

    @Mock
    private CollaboratorRepository collaboratorRepository;

    @Mock
    private FormRepository formRepository;

    @InjectMocks
    private CollaboratorService collaboratorService;

    @Test
    void addCollaborator_WhenOwner_ShouldAddCollaborator() {
        // Given
        Long formId = 1L;
        Long ownerId = 1L;
        Long collaboratorUserId = 2L;

        Form form = Form.builder().id(formId).ownerId(ownerId).build();
        when(formRepository.findById(formId)).thenReturn(Optional.of(form));
        when(collaboratorRepository.save(any(Collaborator.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Collaborator result = collaboratorService.addCollaborator(formId, collaboratorUserId, CollaboratorRole.EDITOR, ownerId);

        // Then
        assertEquals(collaboratorUserId, result.getUserId());
        assertEquals(CollaboratorRole.EDITOR, result.getRole());
        assertEquals(form, result.getForm());
    }

    @Test
    void addCollaborator_WhenNotOwner_ShouldThrowException() {
        // Given
        Long formId = 1L;
        Long ownerId = 1L;
        Long nonOwnerId = 999L;

        Form form = Form.builder().id(formId).ownerId(ownerId).build();
        when(formRepository.findById(formId)).thenReturn(Optional.of(form));

        // When & Then
        assertThrows(SecurityException.class,
                () -> collaboratorService.addCollaborator(formId, 2L, CollaboratorRole.EDITOR, nonOwnerId));
    }

    @Test
    void getCollaboratorsByForm_ShouldReturnCollaborators() {
        // Given
        Long formId = 1L;
        List<Collaborator> expectedCollaborators = List.of(
                Collaborator.builder().id(1L).userId(2L).role(CollaboratorRole.EDITOR).build()
        );
        when(collaboratorRepository.findByFormId(formId)).thenReturn(expectedCollaborators);

        // When
        List<Collaborator> result = collaboratorService.getCollaboratorsByForm(formId);

        // Then
        assertEquals(1, result.size());
        verify(collaboratorRepository, times(1)).findByFormId(formId);
    }

    @Test
    void removeCollaborator_WhenOwner_ShouldRemoveCollaborator() {
        // Given
        Long formId = 1L;
        Long ownerId = 1L;
        Long collaboratorUserId = 2L;

        Form form = Form.builder().id(formId).ownerId(ownerId).build();
        Collaborator collaborator = Collaborator.builder().id(1L).userId(collaboratorUserId).build();

        when(formRepository.findById(formId)).thenReturn(Optional.of(form));
        when(collaboratorRepository.findByFormIdAndUserId(formId, collaboratorUserId))
                .thenReturn(Optional.of(collaborator));

        // When
        collaboratorService.removeCollaborator(formId, collaboratorUserId, ownerId);

        // Then
        verify(collaboratorRepository, times(1)).delete(collaborator);
    }

    @Test
    void removeCollaborator_WhenCollaboratorNotFound_ShouldThrowException() {
        // Given
        Long formId = 1L;
        Long ownerId = 1L;
        Long collaboratorUserId = 2L;

        Form form = Form.builder().id(formId).ownerId(ownerId).build();
        when(formRepository.findById(formId)).thenReturn(Optional.of(form));
        when(collaboratorRepository.findByFormIdAndUserId(formId, collaboratorUserId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class,
                () -> collaboratorService.removeCollaborator(formId, collaboratorUserId, ownerId));
    }
}