package com.djokic.responseserviceff.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ResponseSummaryDTO {
    private Long formId;
    private String formTitle;
    private Long totalResponses;
    private Map<String, QuestionSummary> questionSummaries;

    @Data
    public static class QuestionSummary {
        private String questionText;
        private String questionType;
        private Map<Object, Long> answerDistribution;
        private List<String> individualAnswers;
    }
}