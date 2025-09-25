package com.fineforms.backend.mappers;

import com.fineforms.backend.DTO.CollaboratorDto;
import com.fineforms.backend.entity.Collaborator;
import com.fineforms.backend.entity.Form;
import com.fineforms.backend.enums.CollaboratorRole;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-25T18:59:21+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 23 (Oracle Corporation)"
)
@Component
public class CollaboratorMapperImpl implements CollaboratorMapper {

    @Override
    public CollaboratorDto toDto(Collaborator collaborator) {
        if ( collaborator == null ) {
            return null;
        }

        CollaboratorDto collaboratorDto = new CollaboratorDto();

        collaboratorDto.setFormId( collaboratorFormId( collaborator ) );
        collaboratorDto.setId( collaborator.getId() );
        collaboratorDto.setUserId( collaborator.getUserId() );
        if ( collaborator.getRole() != null ) {
            collaboratorDto.setRole( collaborator.getRole().name() );
        }

        return collaboratorDto;
    }

    @Override
    public Collaborator toEntity(CollaboratorDto collaboratorDto) {
        if ( collaboratorDto == null ) {
            return null;
        }

        Collaborator.CollaboratorBuilder collaborator = Collaborator.builder();

        collaborator.id( collaboratorDto.getId() );
        collaborator.userId( collaboratorDto.getUserId() );
        if ( collaboratorDto.getRole() != null ) {
            collaborator.role( Enum.valueOf( CollaboratorRole.class, collaboratorDto.getRole() ) );
        }

        return collaborator.build();
    }

    private Long collaboratorFormId(Collaborator collaborator) {
        Form form = collaborator.getForm();
        if ( form == null ) {
            return null;
        }
        return form.getId();
    }
}
