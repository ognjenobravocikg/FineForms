package com.fineforms.backend.client;

import com.fineforms.backend.DTO.FormResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "response-service", url = "${response.service.url:http://localhost:8070}")
public interface ResponseServiceClient {

    @GetMapping("/api/responses/form/{formId}")
    List<FormResponseDto> getResponsesByFormId(@PathVariable Long formId);
}
