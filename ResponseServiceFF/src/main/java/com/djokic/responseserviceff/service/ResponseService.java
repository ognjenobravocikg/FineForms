package com.djokic.responseserviceff.service;

import com.djokic.responseserviceff.dto.CreateResponseRequestDTO;
import com.djokic.responseserviceff.dto.ResponseDTO;
import com.djokic.responseserviceff.repository.ResponseRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ResponseService {
    @Autowired
    private final ResponseRepository responseRepository;

    public List<ResponseDTO> getAllResponsesForForm(Long formId) {

    }

    public ResponseDTO createResponseForForm(Long formId, CreateResponseRequestDTO createResponseRequestDTO) {

    }

    public boolean deleteResponse(Long formId, Long responseId, Long currentUserId) {
        responseRepository.findById(responseId).ifPresent(response -> {if(!response.getAuthorId().equals(currentUserId)) throw new RuntimeException("You are not allowed to delete this response!");});
        responseRepository.deleteById(responseId);

        return true;
    }
}
