package com.djokic.responseserviceff.client;

import com.djokic.responseserviceff.dto.FormDTO;
import com.djokic.responseserviceff.dto.CollaboratorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "form-service", url = "${form.service.url:http://localhost:8060}")
public interface FormServiceClient {

    @GetMapping("/form/{formId}")
    FormDTO getForm(@PathVariable Long formId);

    @GetMapping("/form/{formId}/collab")
    List<CollaboratorDTO> getCollaborators(@PathVariable Long formId);

    @GetMapping("/form/public/{formId}")
    FormDTO getPublicForm(@PathVariable Long formId);
}