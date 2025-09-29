package com.djokic.apigatewayauthserviceff.dto.formservicedto;

import com.djokic.apigatewayauthserviceff.enumeration.CollaboratorRole;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddCollaboratorDTO {
    private Long userId;
    private CollaboratorRole role;
}
