package com.fineforms.backend.controller;

import com.fineforms.backend.model.Form;
import com.fineforms.backend.service.FormService;
import com.fineforms.backend.DTO.CreateFormDto;
import com.fineforms.backend.model.Collaborator;
import com.fineforms.backend.service.CollaboratorService;
import com.fineforms.backend.enums.CollaboratorRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forms")
@RequiredArgsConstructor
public class CollaboratorController {
    private final FormService formService;
    private final CollaboratorService collaboratorService;

    @PostMapping
    public ResponseEntity<Form> createForm(@RequestBody CreateFormDto dto, @RequestParam Long ownerId) {
        return ResponseEntity.ok(formService.createForm(dto, ownerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Form> getForm(@PathVariable Long id) {
        return ResponseEntity.ok(formService.getForm(id));
    }

    @GetMapping
    public ResponseEntity<List<Form>> getAllForms() {
        return ResponseEntity.ok(formService.getAllForms());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Form> updateForm(@PathVariable Long id, @RequestBody CreateFormDto dto) {
        return ResponseEntity.ok(formService.updateForm(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteForm(@PathVariable Long id) {
        formService.deleteForm(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{formId}/collaborators")
    public ResponseEntity<List<Collaborator>> getCollaborators(@PathVariable Long formId) {
        return ResponseEntity.ok(collaboratorService.getCollaboratorsByForm(formId));
    }

    @PostMapping("/{formId}/collaborators")
    public ResponseEntity<Collaborator> addCollaborator(
            @PathVariable Long formId,
            @RequestParam Long userId,
            @RequestParam CollaboratorRole role
    ) {
        return ResponseEntity.ok(collaboratorService.addCollaborator(formId, userId, role));
    }

    @DeleteMapping("/{formId}/collaborators/{collaboratorId}")
    public ResponseEntity<Void> removeCollaborator(@PathVariable Long formId, @PathVariable Long collaboratorId) {
        collaboratorService.removeCollaborator(collaboratorId);
        return ResponseEntity.noContent().build();
    }
}

