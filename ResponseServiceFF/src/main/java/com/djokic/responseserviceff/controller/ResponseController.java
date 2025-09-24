package com.djokic.responseserviceff.controller;

import com.djokic.responseserviceff.dto.CreateResponseRequestDTO;
import com.djokic.responseserviceff.dto.DeleteResponseRequestDTO;
import com.djokic.responseserviceff.service.ResponseService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/form/{formId}/response")
@AllArgsConstructor
public class ResponseController {
    @Autowired
    private final ResponseService responseService;

    @GetMapping
    public ResponseEntity<?> getAllResponsesForForm(@PathVariable Long formId){
        return ResponseEntity.status(HttpStatus.OK).body(responseService.getAllResponsesForForm(formId));
    }

    @PostMapping
    public ResponseEntity<?> createResponseForForm(@PathVariable Long formId, @RequestBody CreateResponseRequestDTO createResponseRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(responseService.createResponseForForm(formId, createResponseRequestDTO));
    }

    @DeleteMapping("/{responseId}")
    public ResponseEntity<?> deleteResponse(@PathVariable Long formId, @RequestBody DeleteResponseRequestDTO deleteResponseRequest){
        return ResponseEntity.ok(responseService.deleteResponse(formId, deleteResponseRequest.getResponseId(), deleteResponseRequest.getCurrentUserId()));
    }
}
