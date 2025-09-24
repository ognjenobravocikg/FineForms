package com.fineforms.backend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.fineforms.backend.DTO.QuestionDTO;
import com.fineforms.backend.entity.Question;
import com.fineforms.backend.entity.Option;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionMapper {



    @Mapping(target = "options", source = "options")
    QuestionDTO toDto(Question question);

    List<QuestionDTO> toDtoList(List<Question> questions);

    @Mapping(target = "form", ignore = true)
    @Mapping(target = "options", source = "options")
    Question toEntity(QuestionDTO questionDto);

    @Mapping(target = "form", ignore = true)
    void updateEntityFromDto(QuestionDTO dto, @MappingTarget Question question);
    default List<String> mapOptionsToStrings(List<Option> options) {
        if (options == null) return null;
        return options.stream()
                .map(Option::getLabel)
                .toList();
    }

    default List<Option> mapStringsToOptions(List<String> optionLabels) {
        if (optionLabels == null) return null;
        return optionLabels.stream()
                .map(label -> {
                    Option opt = new Option();
                    opt.setLabel(label);
                    return opt;
                })
                .toList();
    }
}
