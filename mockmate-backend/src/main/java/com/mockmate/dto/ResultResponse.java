package com.mockmate.dto;

import com.mockmate.model.Result;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ResultResponse {

    private Long resultId;
    private Long candidateId;
    private String candidateName;
    private String sessionLabel;
    private Double overallScore;
    private Integer totalQuestions;
    private String overallFeedback;
    private String performanceLevel;
    private LocalDateTime generatedAt;
    private List<AttemptSummary> attempts;

    @Data
    @Builder
    public static class AttemptSummary {
        private Long attemptId;
        private Long questionId;
        private String questionContent;
        private String category;
        private String difficulty;
        private String answerText;
        private Integer score;
        private String aiFeedback;
        private String status;
        private LocalDateTime submittedAt;
    }

    public static ResultResponse fromEntity(Result result) {
        List<AttemptSummary> summaries = result.getAttempts() == null ? List.of() :
                result.getAttempts().stream().map(a -> AttemptSummary.builder()
                        .attemptId(a.getId())
                        .questionId(a.getQuestion().getId())
                        .questionContent(a.getQuestion().getContent())
                        .category(a.getQuestion().getCategory().name())
                        .difficulty(a.getQuestion().getDifficulty().name())
                        .answerText(a.getAnswerText())
                        .score(a.getScore())
                        .aiFeedback(a.getAiFeedback())
                        .status(a.getStatus().name())
                        .submittedAt(a.getSubmittedAt())
                        .build()
                ).toList();

        return ResultResponse.builder()
                .resultId(result.getId())
                .candidateId(result.getCandidate().getId())
                .candidateName(result.getCandidate().getFullName())
                .sessionLabel(result.getSessionLabel())
                .overallScore(result.getOverallScore())
                .totalQuestions(result.getTotalQuestions())
                .overallFeedback(result.getOverallFeedback())
                .performanceLevel(result.getPerformanceLevel() != null
                        ? result.getPerformanceLevel().name() : null)
                .generatedAt(result.getGeneratedAt())
                .attempts(summaries)
                .build();
    }
}
