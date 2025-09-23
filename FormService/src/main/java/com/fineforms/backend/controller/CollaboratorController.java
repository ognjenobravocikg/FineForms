package com.fineforms.backend.controller;

import com.fineforms.backend.entity.Form;
import com.fineforms.backend.service.FormService;
import com.fineforms.backend.DTO.CreateFormDto;
import com.fineforms.backend.entity.Collaborator;
import com.fineforms.backend.service.CollaboratorService;
import com.fineforms.backend.enums.CollaboratorRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forms/{formId}/collab")
@RequiredArgsConstructor
public class CollaboratorController {
    private final FormService formService;
    private final CollaboratorService collaboratorService;



    // Add collaborator to form
    @PostMapping
    public ResponseEntity<Collaborator> addCollaborator(
            @PathVariable Long formId,
            @RequestBody Long userId,
            @RequestParam CollaboratorRole role
    ) {
        return ResponseEntity.ok(collaboratorService.addCollaborator(formId, userId, role));
    }

    // Get all collaborators for a form
    @GetMapping
    public ResponseEntity<List<Collaborator>> getCollaborators(@PathVariable Long formId) {
        return ResponseEntity.ok(collaboratorService.getCollaboratorsByForm(formId));
    }

    // Update collaborator role (only owner can call)
    @PatchMapping("/{userId}")
    public ResponseEntity<Collaborator> updateRole(
            @PathVariable Long formId,
            @PathVariable Long userId,
            @RequestParam CollaboratorRole role,
            @RequestParam Long ownerId
    ) {
        return ResponseEntity.ok(collaboratorService.updateCollaboratorRole(formId, userId, role, ownerId));
    }

    // Remove collaborator (only owner can call)
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeCollaborator(
            @PathVariable Long formId,
            @PathVariable Long userId,
            @RequestParam Long ownerId
    ) {
        collaboratorService.removeCollaborator(formId, userId, ownerId);
        return ResponseEntity.noContent().build();
    }
}

