package com.fineforms.backend.repo;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.fineforms.backend.entity.Collaborator;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {
    List<Collaborator> findByFormId(Long formId);
    Optional<Collaborator> findByFormIdAndUserId(Long formId, Long userId);
    List<Long> findFormsByUserId(Long collaboratorId);
}

