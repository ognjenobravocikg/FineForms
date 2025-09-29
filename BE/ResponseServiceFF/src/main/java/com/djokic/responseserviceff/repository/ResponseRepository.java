package com.djokic.responseserviceff.repository;

import com.djokic.responseserviceff.entity.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ResponseRepository extends JpaRepository<Response, Long> {

    List<Response> findByFormId(Long formId);

    List<Response> findByUserId(Long userId);

    List<Response> findByFormIdAndUserId(Long formId, Long userId);

    @Query("SELECT COUNT(r) FROM Response r WHERE r.formId = :formId")
    Long countByFormId(@Param("formId") Long formId);

    boolean existsByFormIdAndUserId(Long formId, Long userId);

    //anonymous user check
    boolean existsByFormIdAndUserEmail(Long formId, String userEmail);

    // For analytics - fetch only answers for a specific form
    @Query("SELECT r.answers FROM Response r WHERE r.formId = :formId")
    List<String> findAnswersByFormId(@Param("formId") Long formId);
}