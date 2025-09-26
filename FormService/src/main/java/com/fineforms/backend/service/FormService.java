package com.fineforms.backend.service;

import com.fineforms.backend.DTO.*;
import com.fineforms.backend.entity.Collaborator;
import com.fineforms.backend.entity.Form;
import com.fineforms.backend.entity.Question;
import com.fineforms.backend.entity.Option;
import com.fineforms.backend.enums.CollaboratorRole;
import com.fineforms.backend.repo.CollaboratorRepository;
import com.fineforms.backend.repo.FormRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fineforms.backend.mappers.FormMapper;
import com.fineforms.backend.exceptions.*;
import com.fineforms.backend.client.UserServiceClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class FormService {

    private final FormRepository formRepository;
    private final FormMapper formMapper;
    private final UserServiceClient userServiceClient;
    private final CollaboratorService collaboratorService;
    private final CollaboratorRepository collaboratorRepository;

    private FormDTO mapToDto(Form form) {
        return FormDTO.builder()
                .id(form.getId())
                .title(form.getTitle())
                .description(form.getDescription())
                .requiresAuth(form.isRequiresAuth())
                .questions(form.getQuestions() != null ?
                        form.getQuestions().stream()
                                .map(this::mapQuestionToDto)
                                .collect(Collectors.toList())
                        : List.of())
                .collaborators(form.getCollaborators() != null ?
                        form.getCollaborators().stream()
                                .map(this::mapCollaboratorToDto)
                                .collect(Collectors.toList())
                        : List.of())
                .build();
    }

    private QuestionDTO mapQuestionToDto(Question q) {
        return new QuestionDTO(
                q.getId(),
                q.getText(),
                q.getType().name(),
                q.getOptions() != null ?
                        q.getOptions().stream()
                                .map(this::mapOptionToDto)
                                .collect(Collectors.toList())
                        : List.of(),
                q.getImageUrl(),
                q.isRequiredQuestion(),
                q.getNumberMin(),
                q.getNumberMax(),
                q.getNumberStep(),
                null, null, null,
                q.getForm().getId(),
                q.getPosition()
        );
    }

    private OptionDto mapOptionToDto(Option o) {
        return OptionDto.builder()
                .id(o.getId())
                .text(o.getText())
                .order(o.getOrder())
                .isCorrect(o.isCorrect())
                .imageUrl(o.getImageUrl())
                .questionId(o.getQuestion().getId())
                .build();
    }

    private CollaboratorDto mapCollaboratorToDto(Collaborator c) {
        return CollaboratorDto.builder()
                .userId(c.getUserId())
                .role(c.getRole().name())
                .build();
    }

    @Transactional
    public Form createForm(CreateFormDto dto, Long currentUserId) {
        if(dto.getOwnerId() == null) {
            dto.setOwnerId(currentUserId);
        }

        if(!dto.getOwnerId().equals(currentUserId)) {
            throw new NotAuthorizedException("Invalid ownerId");
        }

        if(dto.getTitle().isEmpty()) { dto.setTitle("Untitled Form"); }

        Form f = new Form();
        f.setOwnerId(dto.getOwnerId());
        f.setTitle(dto.getTitle());
        f.setDescription(dto.getDescription());
        f.setRequiresAuth(dto.isRequiresAuth());

        int pos = 0;
        if(dto.getQuestions() != null) {
            for (CreateQuestionDto qdto : dto.getQuestions()) {
                Question q = new Question();
                q.setText(qdto.getText());
                q.setRequiredQuestion(qdto.isRequired());
                if (qdto.getType() == null) {
                    throw new IllegalArgumentException("Invalid or missing question type");
                }
                q.setType(qdto.getType());
                q.setPosition(pos++);
                q.setNumberMin(qdto.getNumberMin());
                q.setNumberMax(qdto.getNumberMax());
                q.setNumberStep(qdto.getNumberStep());

                if (qdto.getOptions() != null) {
                    int ord = 0;
                    for (OptionDto optDto : qdto.getOptions()) {
                        Option opt = new Option();
                        opt.setText(optDto.getText());
                        opt.setOrder(ord++);
                        opt.setCorrect(optDto.isCorrect());
                        opt.setImageUrl(optDto.getImageUrl());
                        q.addOption(opt);
                    }
                }
                f.addQuestion(q);
            }
        }
        return formRepository.save(f);
    }

    public FormDTO getPublicForm(Long id) {
        Form form = formRepository.findById(id)
                .orElseThrow(() -> new FormNotFoundException(id));

        if (form.isRequiresAuth()) {
            throw new FormNotPublicException(id);
        }

        return formMapper.toDto(form);
    }

    public Form getForm(Long formId) {
        return formRepository.findById(formId)
                .orElseThrow(() -> new IllegalArgumentException("Form not found: " + formId));
    }

    public List<Form> getAllForms() {
        return formRepository.findAll();
    }

    @Transactional
    public Form updateForm(Long id, CreateFormDto dto, Long currentUserId) {
        Form f = getForm(id);
        Optional<Collaborator> collab = collaboratorRepository.findByFormIdAndUserId(id, currentUserId);

        if (!(f.getOwnerId().equals(currentUserId) ||
                (collab.isPresent() && collab.get().getRole() == CollaboratorRole.EDITOR))) {
            throw new NotAuthorizedException("Only the owner or editors can update the form.");
        }

        if(!dto.getTitle().isEmpty() && !dto.getTitle().equalsIgnoreCase(f.getTitle())) f.setTitle(dto.getTitle());
        f.setDescription(dto.getDescription());
        f.setRequiresAuth(dto.isRequiresAuth());

        List<Long> incomingQuestionIds = dto.getQuestions() != null
                ? dto.getQuestions().stream().map(CreateQuestionDto::getId).toList()
                : List.of();

        f.getQuestions().removeIf(q -> !incomingQuestionIds.contains(q.getId()));

        int pos = 0;
        if (dto.getQuestions() != null) {
            for (CreateQuestionDto qdto : dto.getQuestions()) {
                Question q;

                if (qdto.getId() != null) {
                    q = f.getQuestions().stream()
                            .filter(existing -> existing.getId().equals(qdto.getId()))
                            .findFirst()
                            .orElse(null);

                    if (q == null) {
                        throw new IllegalArgumentException("Question not found: " + qdto.getId());
                    }
                } else {
                    q = new Question();
                    f.addQuestion(q);
                }

                q.setText(qdto.getText());
                q.setRequiredQuestion(qdto.isRequired());

                if (qdto.getType() == null) {
                    throw new IllegalArgumentException("Invalid or missing question type");
                }
                q.setType(qdto.getType());
                q.setPosition(pos++);
                q.setNumberMin(qdto.getNumberMin());
                q.setNumberMax(qdto.getNumberMax());
                q.setNumberStep(qdto.getNumberStep());

                List<Long> incomingOptionIds = qdto.getOptions() != null
                        ? qdto.getOptions().stream().map(OptionDto::getId).toList()
                        : List.of();

                q.getOptions().removeIf(opt -> !incomingOptionIds.contains(opt.getId()));

                if (qdto.getOptions() != null) {
                    int ord = 0;
                    for (OptionDto optDto : qdto.getOptions()) {
                        Option opt;

                        if (optDto.getId() != null) {
                            opt = q.getOptions().stream()
                                    .filter(existing -> existing.getId().equals(optDto.getId()))
                                    .findFirst()
                                    .orElse(null);

                            if (opt == null) {
                                throw new IllegalArgumentException("Option not found: " + optDto.getId());
                            }
                        } else {
                            opt = new Option();
                            q.addOption(opt);
                        }

                        opt.setText(optDto.getText());
                        opt.setOrder(ord++);
                        opt.setCorrect(optDto.isCorrect());
                        opt.setImageUrl(optDto.getImageUrl());
                    }
                }
            }
        }

        return formRepository.save(f);
    }

    @Transactional
    public void deleteForm(Long id, Long currentUserId) {
        Form f = getForm(id);
        if(!f.getOwnerId().equals(currentUserId)) {
            throw new NotAuthorizedException("Only the owner can delete the form.");
        }

        formRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<CollaboratorDto> getCollaboratorsForForm(Long formId) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new FormNotFoundException(formId));

        List<Collaborator> collaborators = collaboratorRepository.findByFormId(formId);
        if (collaborators.isEmpty()) {
            throw new NoCollaboratorException(formId);
        }

        return collaborators.stream()
                .map(this::mapCollaboratorToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FormDTO> getFormsForUser(Long userId) {
        List<Form> ownedForms = formRepository.findAllByOwnerId(userId);

        List<Form> collaboratorForms = collaboratorRepository.findByUserId(userId)
                .stream()
                .map(Collaborator::getForm)
                .toList();

        return Stream.concat(ownedForms.stream(), collaboratorForms.stream())
                .distinct()
                .map(this::mapToDto)
                .toList();
    }

    public List<FormDTO> getAllFormsForCollaborator(Long collaboratorId, Long currentUserId) {
        List<Collaborator> collaboratorList = collaboratorService.getCollaboratorsByCollaboratorId(collaboratorId);

        List<Form> forms = collaboratorList.stream()
                .map(Collaborator::getForm)
                .distinct()
                .toList();
        
        List<FormDTO> formDTOs = forms.stream()
                .map(this::mapToDto)
                .toList();

        return formDTOs;
    }
}
