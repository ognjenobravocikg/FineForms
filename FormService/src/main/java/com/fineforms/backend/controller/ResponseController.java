package com.fineforms.backend.controller;

import com.fineforms.backend.DTO.FormResponseDto;
import com.fineforms.backend.client.ResponseServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/form/{formId}/responses")
@RequiredArgsConstructor
public class ResponseController {

    private final ResponseServiceClient responseServiceClient;

    @GetMapping
    public ResponseEntity<List<FormResponseDto>> getFormResponses(@PathVariable Long formId) {
        List<FormResponseDto> responses = responseServiceClient.getResponsesByFormId(formId);
        return ResponseEntity.ok(responses);
    }
}