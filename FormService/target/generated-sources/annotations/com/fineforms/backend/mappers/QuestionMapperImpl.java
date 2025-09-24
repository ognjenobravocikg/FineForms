package com.fineforms.backend.mappers;

import com.fineforms.backend.DTO.QuestionDTO;
import com.fineforms.backend.entity.Option;
import com.fineforms.backend.entity.Question;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-24T19:34:06+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 23-valhalla (Oracle Corporation)"
)
@Component
public class QuestionMapperImpl implements QuestionMapper {

    @Override
    public QuestionDTO toDto(Question question) {
        if ( question == null ) {
            return null;
        }

        QuestionDTO questionDTO = new QuestionDTO();

        questionDTO.setOptions( mapOptionsToStrings( question.getOptions() ) );
        questionDTO.setId( question.getId() );
        questionDTO.setText( question.getText() );
        questionDTO.setType( question.getType() );
        questionDTO.setImageUrl( question.getImageUrl() );
        questionDTO.setNumberMin( question.getNumberMin() );
        questionDTO.setNumberMax( question.getNumberMax() );
        questionDTO.setNumberStep( question.getNumberStep() );

        return questionDTO;
    }

    @Override
    public List<QuestionDTO> toDtoList(List<Question> questions) {
        if ( questions == null ) {
            return null;
        }

        List<QuestionDTO> list = new ArrayList<QuestionDTO>( questions.size() );
        for ( Question question : questions ) {
            list.add( toDto( question ) );
        }

        return list;
    }

    @Override
    public Question toEntity(QuestionDTO questionDto) {
        if ( questionDto == null ) {
            return null;
        }

        Question.QuestionBuilder question = Question.builder();

        question.options( mapStringsToOptions( questionDto.getOptions() ) );
        question.id( questionDto.getId() );
        question.text( questionDto.getText() );
        question.type( questionDto.getType() );
        question.imageUrl( questionDto.getImageUrl() );
        question.numberMin( questionDto.getNumberMin() );
        question.numberMax( questionDto.getNumberMax() );
        question.numberStep( questionDto.getNumberStep() );

        return question.build();
    }

    @Override
    public void updateEntityFromDto(QuestionDTO dto, Question question) {
        if ( dto == null ) {
            return;
        }

        question.setId( dto.getId() );
        question.setText( dto.getText() );
        question.setType( dto.getType() );
        question.setImageUrl( dto.getImageUrl() );
        question.setNumberMin( dto.getNumberMin() );
        question.setNumberMax( dto.getNumberMax() );
        question.setNumberStep( dto.getNumberStep() );
        if ( question.getOptions() != null ) {
            List<Option> list = mapStringsToOptions( dto.getOptions() );
            if ( list != null ) {
                question.getOptions().clear();
                question.getOptions().addAll( list );
            }
            else {
                question.setOptions( null );
            }
        }
        else {
            List<Option> list = mapStringsToOptions( dto.getOptions() );
            if ( list != null ) {
                question.setOptions( list );
            }
        }
    }
}
