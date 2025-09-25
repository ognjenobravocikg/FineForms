package com.djokic.responseserviceff.controller;

import com.djokic.responseserviceff.dto.CreateResponseDTO;
import com.djokic.responseserviceff.dto.ResponseDTO;
import com.djokic.responseserviceff.service.ResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/response")
public class ResponseController {

    @Autowired
    private ResponseService responseService;

    @PostMapping
    public ResponseEntity<ResponseDTO> createResponse(@RequestBody CreateResponseDTO requestDTO) {
        ResponseDTO response = responseService.createResponse(requestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/anonymous")
    public ResponseEntity<ResponseDTO> createAnonymousResponse(@RequestBody CreateResponseDTO requestDTO) {
        ResponseDTO response = responseService.createAnonymousResponse(requestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{formId}")
    public ResponseEntity<List<ResponseDTO>> getResponsesByFormId(
            @PathVariable Long formId,
            @RequestParam("currentUserId") Long currentUserId) {
        List<ResponseDTO> responses = responseService.getResponsesByFormId(formId, currentUserId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{formId}/{responseId}")
    public ResponseEntity<ResponseDTO> getResponseForFormById(
            @PathVariable Long formId,
            @PathVariable Long responseId,
            @RequestParam("currentUserId") Long currentUserId) {
        ResponseDTO response = responseService.getResponseById(responseId, currentUserId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResponse(
            @PathVariable Long id,
            @RequestParam("currentUserId") Long currentUserId) {
        responseService.deleteResponse(id, currentUserId);
        return ResponseEntity.noContent().build();
    }
}