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
@RequestMapping("/api/forms/collab") // TODO: Zameniti endpoint -> /api/forms/{formId}/collab/
@RequiredArgsConstructor
public class CollaboratorController {
    private final FormService formService;
    private final CollaboratorService collaboratorService;

    /*
    TODO: Prepraviti kontroler od nule. Ovaj kontroler ce imati ulogu handle-ovanja zahteva vezanih za kolaboratore.
          Na primer POST zahtev na /api/forms/{formId}/collab endpoint sa request body-em u kom je spakovan userDTO ili userId
          ce dodati kolaboratora sa tacno definisanom ulogom (viewer, editor) na formu sa odgovarajucim ID-em.
          GET zahtev na /api/forms/{formId}/collab endpoint ce u response body vratiti JSON objekat koji ce u sebi imati listu svih kolaboratora sa date forme.
          PATCH zahtev na /api/forms/{formId}/collab/{userId} endpoint ima za cilj promenu uloge kolaboratora *****OVAJ ZAHTEV MOZE BITI OBRADJEN SAMO KADA GA OWNER POZIVA*****
          DELETE zahtev na /api/forms/{formId}/collab{userId} endpoint ima za cilj brisanje kolaboratora sa zadate forme *****OVAJ ZAHTEV MOZE BITI OBRADJEN SAMO KADA GA OWNER POZIVA*****
    */

    // TODO: Ukloniti createForm metodu iz ovog kontrolera
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

