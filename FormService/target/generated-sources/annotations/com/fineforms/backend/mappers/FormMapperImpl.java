package com.fineforms.backend.mappers;

import com.fineforms.backend.DTO.CollaboratorDto;
import com.fineforms.backend.DTO.FormDTO;
import com.fineforms.backend.DTO.QuestionDTO;
import com.fineforms.backend.entity.Collaborator;
import com.fineforms.backend.entity.Form;
import com.fineforms.backend.entity.Question;
import com.fineforms.backend.enums.CollaboratorRole;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-25T18:59:21+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 23 (Oracle Corporation)"
)
@Component
public class FormMapperImpl implements FormMapper {

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public FormDTO toDto(Form form) {
        if ( form == null ) {
            return null;
        }

        FormDTO.FormDTOBuilder formDTO = FormDTO.builder();

        formDTO.id( form.getId() );
        formDTO.title( form.getTitle() );
        formDTO.description( form.getDescription() );
        formDTO.questions( questionMapper.toDtoList( form.getQuestions() ) );
        formDTO.collaborators( collaboratorListToCollaboratorDtoList( form.getCollaborators() ) );
        formDTO.requiresAuth( form.isRequiresAuth() );

        return formDTO.build();
    }

    @Override
    public Form toEntity(FormDTO formDto) {
        if ( formDto == null ) {
            return null;
        }

        Form.FormBuilder form = Form.builder();

        form.id( formDto.getId() );
        form.title( formDto.getTitle() );
        form.description( formDto.getDescription() );
        form.requiresAuth( formDto.isRequiresAuth() );
        form.questions( questionDTOListToQuestionList( formDto.getQuestions() ) );
        form.collaborators( collaboratorDtoListToCollaboratorList( formDto.getCollaborators() ) );

        return form.build();
    }

    protected CollaboratorDto collaboratorToCollaboratorDto(Collaborator collaborator) {
        if ( collaborator == null ) {
            return null;
        }

        CollaboratorDto collaboratorDto = new CollaboratorDto();

        collaboratorDto.setId( collaborator.getId() );
        collaboratorDto.setUserId( collaborator.getUserId() );
        if ( collaborator.getRole() != null ) {
            collaboratorDto.setRole( collaborator.getRole().name() );
        }

        return collaboratorDto;
    }

    protected List<CollaboratorDto> collaboratorListToCollaboratorDtoList(List<Collaborator> list) {
        if ( list == null ) {
            return null;
        }

        List<CollaboratorDto> list1 = new ArrayList<CollaboratorDto>( list.size() );
        for ( Collaborator collaborator : list ) {
            list1.add( collaboratorToCollaboratorDto( collaborator ) );
        }

        return list1;
    }

    protected List<Question> questionDTOListToQuestionList(List<QuestionDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<Question> list1 = new ArrayList<Question>( list.size() );
        for ( QuestionDTO questionDTO : list ) {
            list1.add( questionMapper.toEntity( questionDTO ) );
        }

        return list1;
    }

    protected Collaborator collaboratorDtoToCollaborator(CollaboratorDto collaboratorDto) {
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

    protected List<Collaborator> collaboratorDtoListToCollaboratorList(List<CollaboratorDto> list) {
        if ( list == null ) {
            return null;
        }

        List<Collaborator> list1 = new ArrayList<Collaborator>( list.size() );
        for ( CollaboratorDto collaboratorDto : list ) {
            list1.add( collaboratorDtoToCollaborator( collaboratorDto ) );
        }

        return list1;
    }
}
