package com.djokic.apigatewayauthserviceff.client;

import com.djokic.apigatewayauthserviceff.dto.CreateFormDto;
import com.djokic.apigatewayauthserviceff.enumeration.CollaboratorRole;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "form-service", url = "${form.service.url}")
public interface FormServiceClient {

    @PostMapping("/form")
    ResponseEntity<?> createForm(@RequestBody CreateFormDto createFormDto);

    @GetMapping("/form/{id}")
    ResponseEntity<?> getFormById(@PathVariable("id") Long id);

    @GetMapping("/form")
    ResponseEntity<?> getAllForms();

    @PutMapping("/form/{id}")
    ResponseEntity<?> updateForm(@PathVariable Long id, @RequestBody CreateFormDto createFormDto, @RequestParam("userId") Long userIdFromToken);

    @DeleteMapping("/form/{id}")
    ResponseEntity<?> deleteForm(@PathVariable Long id, @RequestParam("userId") Long currentUserId);

    @GetMapping("/form/public/{id}")
    ResponseEntity<?> getPublicFormById(@PathVariable Long id);

    @PostMapping("/form/{formId}/collab")
    ResponseEntity<?> addCollaborator(@PathVariable("formId") Long formId,
                                      @RequestParam Long userId,
                                      @RequestParam CollaboratorRole collaboratorRole,
                                      @RequestParam("userId") Long currentUserId);

    @GetMapping("/form/{formId}/collab")
    ResponseEntity<?> getCollaborators(@PathVariable("formId") Long formId);

    @PatchMapping("/form/{formId}/collab/{userId}")
    ResponseEntity<?> updateRole(@PathVariable("formId") Long formId,
                                 @PathVariable("userId") Long userId,
                                 @RequestParam CollaboratorRole collaboratorRole,
                                 @RequestParam("currentUserId") Long currentUserId);

    @DeleteMapping("/form/{formId}/collab/{userId}")
    ResponseEntity<?> removeCollaborator(@PathVariable("formId") Long formId,
                                         @PathVariable("userId") Long userId,
                                         @RequestParam("currentUserId") Long currentUserId);
}