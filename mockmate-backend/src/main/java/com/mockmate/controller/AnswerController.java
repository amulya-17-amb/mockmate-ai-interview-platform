package com.mockmate.controller;

import com.mockmate.dto.AnswerRequest;
import com.mockmate.model.*;
import com.mockmate.repository.AttemptRepository;
import com.mockmate.repository.CandidateRepository;
import com.mockmate.service.EvaluationService;
import com.mockmate.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
@Slf4j
public class AnswerController {

    private final AttemptRepository attemptRepository;
    private final QuestionService questionService;
    private final EvaluationService evaluationService;
    private final CandidateRepository candidateRepository;

    @PostMapping("/submit")
    public ResponseEntity<?> submit(@Valid @RequestBody AnswerRequest request,
                                    @AuthenticationPrincipal UserDetails userDetails) {

        Candidate candidate = candidateRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));
        Question question = questionService.getQuestionById(request.getQuestionId());

        Attempt attempt = Attempt.builder()
                .candidate(candidate)
                .question(question)
                .answerText(request.getAnswerText())
                .status(Attempt.AttemptStatus.PENDING)
                .build();
        attempt = attemptRepository.save(attempt);

        try {
            Map<String, Object> eval = evaluationService.evaluate(question, request.getAnswerText());
            attempt.setScore((int) eval.get("score"));
            attempt.setAiFeedback((String) eval.get("feedback"));
            attempt.setStatus(Attempt.AttemptStatus.EVALUATED);
            attempt.setEvaluatedAt(LocalDateTime.now());
            attempt = attemptRepository.save(attempt);
            log.info("Attempt {} evaluated: score={}", attempt.getId(), attempt.getScore());
        } catch (Exception e) {
            log.error("Evaluation failed for attempt {}", attempt.getId(), e);
            attempt.setStatus(Attempt.AttemptStatus.FAILED);
            attemptRepository.save(attempt);
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("attemptId", attempt.getId());
        resp.put("score", attempt.getScore() != null ? attempt.getScore() : 0);
        resp.put("feedback", attempt.getAiFeedback() != null ? attempt.getAiFeedback() : "Evaluation pending");
        resp.put("status", attempt.getStatus().name());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Attempt>> getMyAttempts(@AuthenticationPrincipal UserDetails userDetails) {
        Candidate candidate = candidateRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return ResponseEntity.ok(attemptRepository.findByCandidateId(candidate.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Attempt> getById(@PathVariable Long id,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        Attempt attempt = attemptRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attempt not found: " + id));
        if (!attempt.getCandidate().getEmail().equals(userDetails.getUsername())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(attempt);
    }
}
