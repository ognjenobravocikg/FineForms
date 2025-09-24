package com.fineforms.backend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.fineforms.backend.DTO.FormDTO;
import com.fineforms.backend.entity.Form;


@Mapper(componentModel = "spring")
public interface FormMapper {
    FormDTO toDto(Form form);

    @Mapping(target = "questions", ignore = true)
    Form toEntity(FormDTO formDto);
}

