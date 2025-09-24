package com.djokic.responseserviceff.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class DeleteResponseRequestDTO {
    private Long responseId;
    private Long currentUserId;
}
