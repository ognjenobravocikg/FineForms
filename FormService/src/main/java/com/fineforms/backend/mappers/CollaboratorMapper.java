package com.fineforms.backend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.fineforms.backend.DTO.CollaboratorDto;
import com.fineforms.backend.entity.Collaborator;

@Mapper(componentModel = "spring")
public interface CollaboratorMapper {
    @Mapping(target = "formId", source = "form.id")
    CollaboratorDto toDto(Collaborator collaborator);
    Collaborator toEntity(CollaboratorDto collaboratorDto);
}
