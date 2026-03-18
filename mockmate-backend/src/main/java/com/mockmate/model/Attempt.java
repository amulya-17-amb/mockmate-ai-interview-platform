package com.mockmate.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "attempt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    /** The raw answer text submitted by the candidate */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String answerText;

    /** Score 0-100 assigned by AI evaluation */
    private Integer score;

    /** Detailed AI-generated feedback */
    @Column(columnDefinition = "TEXT")
    private String aiFeedback;

    @Enumerated(EnumType.STRING)
    private AttemptStatus status;

    @Column(updatable = false)
    private LocalDateTime submittedAt;

    private LocalDateTime evaluatedAt;

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
        status = AttemptStatus.PENDING;
    }

    public enum AttemptStatus { PENDING, EVALUATED, FAILED }
}
