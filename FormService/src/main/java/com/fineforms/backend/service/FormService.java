package com.fineforms.backend.service;

import com.fineforms.backend.DTO.CreateFormDto;
import com.fineforms.backend.DTO.CreatteQuestionDto;
import com.fineforms.backend.model.Form;
import com.fineforms.backend.model.Question;
import com.fineforms.backend.model.Option;
import com.fineforms.backend.repo.FormRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FormService {

    private final FormRepository formRepository;

    public FormService(FormRepossitory formRepository){
        this.formRepository = formRepository;
    }

    @Transactional
    public Form createForm(CreateFormDto dto, Long ownerId) {
        Form f = new Form();
        f.setOwnerId(ownerId);
        f.setTitle(dto.title);
        f.setDescription(dto.description);
        f.setRequiresAuth(dto.requiresAuth);

        int pos = 0;
        if(dto.questions != null) {
            for (CreateQuestionDto qdto : dto.questions) {
                Question q = new Question();
                q.setText(qdto.text);
                q.setRequiredQuestion(qdto.required);
                q.setType(qdto.type);
                q.setPosition(pos++);
                q.setNumberMin(qdto.numberMin);
                q.setNumberMax(qdto.numberMax);
                q.setNumberStep(qdto.numberStep);

                if (qdto.options !=null) {
                    int ord = 0;
                    for (String optLaber : qdto.options) {
                        Option opt = new Option();
                        opt.setLabel(optLaber);
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
}
