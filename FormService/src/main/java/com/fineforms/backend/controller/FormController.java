package com.fineforms.backend.controller;

import com.fineforms.backend.DTO.CreateFormDto;
import com.fineforms.backend.entity.Form;
import com.fineforms.backend.DTO.CreateQuestionDto;
import com.fineforms.backend.service.FormService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forms")
public class FormController {
    private final FormService formService;

    public FormController(FormService formService) {
        this.formService = formService;
    }


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
    @PostMapping("/{formId}/questions")
    public ResponseEntity<Form> addQuestion(@PathVariable Long formId, @RequestBody CreateQuestionDto questionDto) {
        return ResponseEntity.ok(formService.addQuestion(formId, questionDto));
    }

    @PutMapping("/{formId}/questions/{questionId}")
    public ResponseEntity<Form> updateQuestion(@PathVariable Long formId, @PathVariable Long questionId, @RequestBody CreateQuestionDto questionDto) {
        return ResponseEntity.ok(formService.updateQuestion(formId, questionId, questionDto));
    }

    @DeleteMapping("/{formId}/questions/{questionId}")
    public ResponseEntity<Form> deleteQuestion(@PathVariable Long formId, @PathVariable Long questionId) {
        return ResponseEntity.ok(formService.deleteQuestion(formId, questionId));
    }

    @PostMapping("/{formId}/questions/{questionId}/clone")
    public ResponseEntity<Form> cloneQuestion(@PathVariable Long formId, @PathVariable Long questionId) {
        return ResponseEntity.ok(formService.cloneQuestion(formId, questionId));
    }

    @PostMapping("/{formId}/questions/reorder")
    public ResponseEntity<Form> reorderQuestions(@PathVariable Long formId, @RequestBody List<Long> newOrder) {
        return ResponseEntity.ok(formService.reorderQuestions(formId, newOrder));
    }
}
