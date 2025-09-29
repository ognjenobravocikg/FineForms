package com.fineforms.backend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.fineforms.backend.DTO.OptionDto;
import com.fineforms.backend.entity.Option;


@Mapper(componentModel = "spring")
public interface OptionMapper {
    @Mapping(target = "questionId", source = "question.id")
    OptionDto toDto(Option option);

    @Mapping(target = "question", ignore = true)
    Option toEntity(OptionDto optionDto);
}
