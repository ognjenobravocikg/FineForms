package com.fineforms.backend.mappers;

import com.fineforms.backend.DTO.OptionDto;
import com.fineforms.backend.entity.Option;
import com.fineforms.backend.entity.Question;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-25T18:59:21+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 23 (Oracle Corporation)"
)
@Component
public class OptionMapperImpl implements OptionMapper {

    @Override
    public OptionDto toDto(Option option) {
        if ( option == null ) {
            return null;
        }

        OptionDto.OptionDtoBuilder optionDto = OptionDto.builder();

        optionDto.questionId( optionQuestionId( option ) );
        optionDto.id( option.getId() );

        return optionDto.build();
    }

    @Override
    public Option toEntity(OptionDto optionDto) {
        if ( optionDto == null ) {
            return null;
        }

        Option.OptionBuilder option = Option.builder();

        option.id( optionDto.getId() );

        return option.build();
    }

    private Long optionQuestionId(Option option) {
        Question question = option.getQuestion();
        if ( question == null ) {
            return null;
        }
        return question.getId();
    }
}
