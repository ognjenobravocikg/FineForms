package com.fineforms.backend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.fineforms.backend.DTO.QuestionDTO;
import com.fineforms.backend.entity.Question;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    @Mapping(target = "formId", source = "form.id")
    QuestionDTO toDto(Question question);

    List<QuestionDTO> toDtoList(List<Question> questions);

    @Mapping(target = "form", ignore = true)
    Question toEntity(QuestionDTO questionDto);

    @Mapping(target = "form", ignore = true)
    void updateEntityFromDto(QuestionDTO dto, @MappingTarget Question question);
}
