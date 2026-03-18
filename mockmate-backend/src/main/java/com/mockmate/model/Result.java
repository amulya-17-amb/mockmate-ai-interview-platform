package com.mockmate.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    /** Session label, e.g. "Session #3 – Backend Dev" */
    private String sessionLabel;

    /** Overall score across all attempts in this session (0-100) */
    private Double overallScore;

    /** Total number of questions attempted in this session */
    private Integer totalQuestions;

    /** AI-generated overall session summary / recommendation */
    @Column(columnDefinition = "TEXT")
    private String overallFeedback;

    @Enumerated(EnumType.STRING)
    private PerformanceLevel performanceLevel;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id")
    private List<Attempt> attempts;

    @Column(updatable = false)
    private LocalDateTime generatedAt;

    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
    }

    public enum PerformanceLevel { POOR, AVERAGE, GOOD, EXCELLENT }
}
