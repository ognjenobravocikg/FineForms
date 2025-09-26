package com.fineforms.backend.controller;

import com.fineforms.backend.DTO.CreateFormDto;
import com.fineforms.backend.entity.Form;
import com.fineforms.backend.DTO.CreateQuestionDto;
import com.fineforms.backend.service.FormService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/form")
public class FormController {
    private final FormService formService;

    public FormController(FormService formService) {
        this.formService = formService;
    }


    @PostMapping
    public ResponseEntity<Form> createForm(@RequestBody CreateFormDto dto, @RequestParam("userId") Long currentUserId) {
        return ResponseEntity.ok(formService.createForm(dto, currentUserId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Form> getForm(@PathVariable Long id) {
        return ResponseEntity.ok(formService.getForm(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Form>> getFormsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(formService.getFormsForUser(userId));
    }

    @GetMapping
    public ResponseEntity<List<Form>> getAllForms() {
        return ResponseEntity.ok(formService.getAllForms());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Form> updateForm(@PathVariable Long id, @RequestBody CreateFormDto dto, @RequestParam("userId") Long currentUserId) {

        return ResponseEntity.ok(formService.updateForm(id, dto, currentUserId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteForm(@PathVariable Long id, @RequestParam("userId") Long currentUserId) {
        formService.deleteForm(id, currentUserId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/public/{id}")
    public ResponseEntity<?> getPublicForm(@PathVariable Long id){
        return ResponseEntity.ok(formService.getPublicForm(id));
    }
}
