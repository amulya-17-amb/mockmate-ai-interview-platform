package com.mockmate.service;

import com.mockmate.dto.ResultResponse;
import com.mockmate.model.*;
import com.mockmate.repository.AttemptRepository;
import com.mockmate.repository.ResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResultService {

    private final ResultRepository resultRepository;
    private final AttemptRepository attemptRepository;

    @Transactional
    public Result generateSessionResult(Candidate candidate, String sessionLabel) {
        List<Attempt> evaluated = attemptRepository.findByCandidateId(candidate.getId())
                .stream()
                .filter(a -> a.getStatus() == Attempt.AttemptStatus.EVALUATED)
                .toList();

        if (evaluated.isEmpty()) {
            throw new IllegalStateException("No evaluated attempts found for this candidate.");
        }

        double avgScore = evaluated.stream()
                .filter(a -> a.getScore() != null)
                .mapToInt(Attempt::getScore)
                .average()
                .orElse(0.0);

        Result result = Result.builder()
                .candidate(candidate)
                .sessionLabel(sessionLabel != null ? sessionLabel
                        : "Session #" + (resultRepository.countByCandidateId(candidate.getId()) + 1))
                .overallScore(Math.round(avgScore * 10.0) / 10.0)
                .totalQuestions(evaluated.size())
                .overallFeedback(buildFeedback(avgScore, evaluated))
                .performanceLevel(toLevel(avgScore))
                .attempts(evaluated)
                .build();

        return resultRepository.save(result);
    }

    public List<ResultResponse> getCandidateResults(Long candidateId) {
        return resultRepository.findByCandidateIdOrderByGeneratedAtDesc(candidateId)
                .stream().map(ResultResponse::fromEntity).toList();
    }

    public ResultResponse getResultById(Long resultId, Long candidateId) {
        Result result = resultRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("Result not found: " + resultId));
        if (!result.getCandidate().getId().equals(candidateId)) {
            throw new IllegalArgumentException("Access denied to result: " + resultId);
        }
        return ResultResponse.fromEntity(result);
    }

    private Result.PerformanceLevel toLevel(double score) {
        if (score >= 85) return Result.PerformanceLevel.EXCELLENT;
        if (score >= 65) return Result.PerformanceLevel.GOOD;
        if (score >= 40) return Result.PerformanceLevel.AVERAGE;
        return Result.PerformanceLevel.POOR;
    }

    private String buildFeedback(double avg, List<Attempt> attempts) {
        long high = attempts.stream().filter(a -> a.getScore() != null && a.getScore() >= 70).count();
        return String.format("You completed %d question(s) with an average score of %.1f%%. " +
                "%d out of %d answers scored 70 or above. %sKeep practicing!",
                attempts.size(), avg, high, attempts.size(),
                avg >= 70 ? "Great performance! " : "There is room to improve. ");
    }
}
