package com.fineforms.backend.service;

import org.springframework.stereotype.Service;
import com.fineforms.backend.entity.Collaborator;
import com.fineforms.backend.repo.CollaboratorRepository;
import com.fineforms.backend.enums.CollaboratorRole;
import com.fineforms.backend.repo.FormRepository;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.EntityNotFoundException;
import com.fineforms.backend.DTO.CollaboratorDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollaboratorService {

private final CollaboratorRepository collaboratorRepository;
private final FormRepository formRepository;

public List<Collaborator> getCollaboratorsByForm(Long formId) {
    return collaboratorRepository.findByFormId(formId);
}

    public CollaboratorDto addCollaborator(Long formId, Long userId, CollaboratorRole role, Long currentUserId) {
        var form = formRepository.findById(formId)
                .orElseThrow(() -> new EntityNotFoundException("Form not found with id: " + formId));
        if (!form.getOwnerId().equals(currentUserId)) {
            throw new SecurityException("Only the owner can add collaborators.");
        }
        Collaborator collaborator = Collaborator.builder()
                .form(form)
                .userId(userId)
                .role(role)
                .build();


        Collaborator entity = collaboratorRepository.save(collaborator);

        CollaboratorDto response = CollaboratorDto.builder()
                .id(entity.getId())
                .formId(entity.getForm().getId())
                .userId(entity.getUserId())
                .role(entity.getRole().name())
                .build();

        return response;
    }

    public void removeCollaborator(Long formId, Long userId, Long currentUserId) {
        var form = formRepository.findById(formId)
                .orElseThrow(() -> new EntityNotFoundException("Form not found with id: " + formId));

        if (!form.getOwnerId().equals(currentUserId)) {
            throw new SecurityException("Only the owner can remove collaborators.");
        }

        var collaborator = collaboratorRepository.findByFormIdAndUserId(formId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Collaborator not found for user: " + userId));

        collaboratorRepository.delete(collaborator);
    }
    public Collaborator updateCollaboratorRole(Long formId, Long userId, CollaboratorRole role, Long currentUserId) {
        var form = formRepository.findById(formId)
                .orElseThrow(() -> new EntityNotFoundException("Form not found with id: " + formId));

        // Check if ownerId matches the form owner
        if (!form.getOwnerId().equals(currentUserId)) {
            throw new SecurityException("Only the owner can update collaborator roles.");
        }

        var collaborator = collaboratorRepository.findByFormIdAndUserId(formId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Collaborator not found for user: " + userId));

        collaborator.setRole(role);
        return collaboratorRepository.save(collaborator);
    }
}