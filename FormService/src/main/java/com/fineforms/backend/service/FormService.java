package com.fineforms.backend.service;

import com.fineforms.backend.DTO.CreateFormDto;
import com.fineforms.backend.DTO.CreateQuestionDto;
import com.fineforms.backend.DTO.OptionDto;
import com.fineforms.backend.model.Form;
import com.fineforms.backend.model.Question;
import com.fineforms.backend.model.Option;
import com.fineforms.backend.repo.FormRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FormService {

    private final FormRepository formRepository;

    public FormService(FormRepository formRepository){
        this.formRepository = formRepository;
    }

    @Transactional
    public Form createForm(CreateFormDto dto, Long ownerId) {
        Form f = new Form();
        f.setOwnerId(ownerId);
        f.setTitle(dto.getTitle());
        f.setDescription(dto.getDescription());
        f.setRequiresAuth(dto.isRequiresAuth());

        int pos = 0;
        if(dto.getQuestions() != null) {
            for (CreateQuestionDto qdto : dto.getQuestions()) {
                Question q = new Question();
                q.setText(qdto.getText());
                q.setRequiredQuestion(qdto.isRequired());
                q.setType(qdto.getType());
                q.setPosition(pos++);
                q.setNumberMin(qdto.getNumberMin());
                q.setNumberMax(qdto.getNumberMax());
                q.setNumberStep(qdto.getNumberStep());

                if (qdto.getOptions() != null) {
                    int ord = 0;
                    for (OptionDto optDto : qdto.getOptions()) {
                        Option opt = new Option();
                        opt.setLabel(optDto.getText());
                        opt.setOrdinal(ord++);
                        q.addOption(opt);
                    }
                }
                f.addQuestion(q);
            }
        }
        return formRepository.save(f);
    }
    public Form getForm(Long formId) {
        return formRepository.findById(formId).orElseThrow(() -> new IllegalArgumentException("Form not found: " + formId) );
    }
    public List<Form> getAllForms() {
        return formRepository.findAll();
    }

    @Transactional
    public Form updateForm(Long id, CreateFormDto dto) {
        Form f = getForm(id);
        f.setTitle(dto.getTitle());
        f.setDescription(dto.getDescription());
        f.setRequiresAuth(dto.isRequiresAuth());
        return formRepository.save(f);
    }

    @Transactional
    public void deleteForm(Long id) {
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
        q.setPosition(form.getQuestions().size()); // add at end

        if (dto.getOptions() != null) {
            int ord = 0;
            for (OptionDto optDto : dto.getOptions()) {
                Option opt = new Option();
                opt.setLabel(optDto.getText());
                opt.setOrdinal(ord++);
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
                opt.setLabel(optDto.getText());
                opt.setOrdinal(ord++);
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
            newOpt.setLabel(origOpt.getLabel());
            newOpt.setOrdinal(origOpt.getOrdinal());
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

}
