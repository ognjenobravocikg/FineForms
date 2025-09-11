package com.fineforms.backend.repo;

import com.fineforms.backend.model.Form;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormRepository extends JpaRepository<Form, Long> {

}
