package com.fineforms.backend.service;

import com.fineforms.backend.DTO.CreateFormDto;
import com.fineforms.backend.DTO.CreateQuestionDto;
import com.fineforms.backend.DTO.FormDTO;
import com.fineforms.backend.DTO.OptionDto;
import com.fineforms.backend.entity.Collaborator;
import com.fineforms.backend.entity.Form;
import com.fineforms.backend.entity.Question;
import com.fineforms.backend.entity.Option;
import com.fineforms.backend.enums.CollaboratorRole;
import com.fineforms.backend.repo.CollaboratorRepository;
import com.fineforms.backend.repo.FormRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fineforms.backend.mappers.FormMapper;
import com.fineforms.backend.exceptions.*;
import com.fineforms.backend.client.UserServiceClient;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class FormService {

    private final FormRepository formRepository;
    private final FormMapper formMapper;
    private final UserServiceClient userServiceClient;
    private final CollaboratorService collaboratorService;
    private final CollaboratorRepository collaboratorRepository;

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
        return formRepository.findById(formId).orElseThrow(() -> new IllegalArgumentException("Form not found: " + formId) );
    }
    public List<Form> getAllForms() {
        return formRepository.findAll();
    }

    @Transactional
    public Form updateForm(Long id, CreateFormDto dto, Long currentUserId) {
        Form f = getForm(id);
        Optional<Collaborator> collab = collaboratorRepository.findByFormIdAndUserId(id, currentUserId);
        if(!f.getOwnerId().equals(currentUserId) || (
                collab.isEmpty() || collab.get().getRole()!=CollaboratorRole.EDITOR
        )) {
            throw new NotAuthorizedException("Only the owner can update the form.");
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
    @Transactional
    public Form addQuestion(Long formId, CreateQuestionDto dto) {

        Form form = getForm(formId);

        Question q = new Question();
        q.setText(dto.getText());
        q.setRequiredQuestion(dto.isRequired());
        q.setType(dto.getType());
        q.setNumberMin(dto.getNumberMin());
        q.setNumberMax(dto.getNumberMax());
        q.setNumberStep(dto.getNumberStep());
        q.setPosition(form.getQuestions().size());

        if (dto.getOptions() != null) {
            int ord = 0;
            for (OptionDto optDto : dto.getOptions()) {
                Option opt = new Option();
                opt.setText(optDto.getText());
                opt.setOrder(ord++);
                q.addOption(opt);
            }
        }

        form.addQuestion(q);

        return formRepository.save(form);
    }

    @Transactional
    public Form updateQuestion(Long formId, Long questionId, CreateQuestionDto dto) {
        Form form = getForm(formId);

        Question q = form.getQuestions().stream()
                .filter(question -> question.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));

        q.setText(dto.getText());
        q.setRequiredQuestion(dto.isRequired());
        q.setType(dto.getType());
        q.setNumberMin(dto.getNumberMin());
        q.setNumberMax(dto.getNumberMax());
        q.setNumberStep(dto.getNumberStep());

        q.getOptions().clear();
        if (dto.getOptions() != null) {
            int ord = 0;
            for (OptionDto optDto : dto.getOptions()) {
                Option opt = new Option();
                opt.setText(optDto.getText());
                opt.setOrder(ord++);
                q.addOption(opt);
            }
        }

        return formRepository.save(form);
    }

    @Transactional
    public Form deleteQuestion(Long formId, Long questionId) {
        Form form = getForm(formId);
        form.getQuestions().removeIf(q -> q.getId().equals(questionId));
        return formRepository.save(form);
    }

    @Transactional
    public Form cloneQuestion(Long formId, Long questionId) {
        Form form = getForm(formId);
        Question original = form.getQuestions().stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));

        Question copy = new Question();
        copy.setText(original.getText());
        copy.setRequiredQuestion(original.isRequiredQuestion());
        copy.setType(original.getType());
        copy.setNumberMin(original.getNumberMin());
        copy.setNumberMax(original.getNumberMax());
        copy.setNumberStep(original.getNumberStep());
        copy.setPosition(form.getQuestions().size());

        for (Option origOpt : original.getOptions()) {
            Option newOpt = new Option();
            newOpt.setText(origOpt.getText());
            newOpt.setOrder(origOpt.getOrder());
            newOpt.setCorrect(origOpt.isCorrect());
            newOpt.setImageUrl(origOpt.getImageUrl());
            copy.addOption(newOpt);
        }

        form.addQuestion(copy);
        return formRepository.save(form);
    }

    @Transactional
    public Form reorderQuestions(Long formId, List<Long> newOrder) {
        Form form = getForm(formId);

        int pos = 0;
        for (Long qid : newOrder) {
            for (Question q : form.getQuestions()) {
                if (q.getId().equals(qid)) {
                    q.setPosition(pos++);
                    break;
                }
            }
        }

        return formRepository.save(form);
    }

    @Transactional(readOnly = true)
    public List<Form> getFormsForUser(Long userId) {
        List<Form> ownedForms = formRepository.findByOwnerId(userId);

        List<Form> collaboratorForms = collaboratorRepository.findByUserId(userId)
                .stream()
                .map(Collaborator::getForm)
                .toList();

        return Stream.concat(ownedForms.stream(), collaboratorForms.stream())
                .distinct()
                .toList();
    }
}
