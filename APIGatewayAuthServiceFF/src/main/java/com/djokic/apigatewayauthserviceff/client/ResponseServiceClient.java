package com.djokic.apigatewayauthserviceff.client;

import com.djokic.apigatewayauthserviceff.dto.responseservicedto.CreateResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "response-service", url = "${response.service.url}")
public interface ResponseServiceClient {

    @GetMapping("/response/{formId}")
    ResponseEntity<?> getAllResponsesForForm(@PathVariable Long formId, @RequestParam("currentUserId") Long currentUserId);

    @GetMapping("/response/{formId}/{responseId}")
    ResponseEntity<?> getResponseForFormById(@PathVariable Long formId, @PathVariable Long responseId, @RequestParam("currentUserId") Long currentUserId);

    @PostMapping("/response")
    ResponseEntity<?> createResponse(@RequestBody CreateResponseDTO createResponseDTO);

    @PostMapping("/response/anonymous")
    public ResponseEntity<?> createAnonymousResponse(@RequestBody CreateResponseDTO createResponseDTO);

    @DeleteMapping("/response/{id}")
    ResponseEntity<?> deleteResponse(@PathVariable Long id, @RequestParam("currentUserId") Long currentUserId);

    @GetMapping("/response/export")
    ResponseEntity<?> exportResponses(@RequestParam("formId") Long formId,
                                      @RequestParam("formId") Long currentUserId);
}