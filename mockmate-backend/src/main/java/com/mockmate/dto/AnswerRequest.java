package com.mockmate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnswerRequest {

    @NotNull(message = "Question ID is required")
    private Long questionId;

    @NotBlank(message = "Answer text cannot be blank")
    private String answerText;

    /** Optional session label to group this answer into a result session */
    private String sessionLabel;
}
