package com.mockmate.controller;

import com.mockmate.dto.ResultResponse;
import com.mockmate.model.Candidate;
import com.mockmate.model.Result;
import com.mockmate.repository.CandidateRepository;
import com.mockmate.service.ResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class ResultController {

    private final ResultService resultService;
    private final CandidateRepository candidateRepository;

    @PostMapping("/generate")
    public ResponseEntity<ResultResponse> generateResult(
            @RequestParam(required = false) String sessionLabel,
            @AuthenticationPrincipal UserDetails userDetails) {
        Candidate candidate = candidateRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        Result result = resultService.generateSessionResult(candidate, sessionLabel);
        return ResponseEntity.ok(ResultResponse.fromEntity(result));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ResultResponse>> getMyResults(@AuthenticationPrincipal UserDetails userDetails) {
        Candidate candidate = candidateRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return ResponseEntity.ok(resultService.getCandidateResults(candidate.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultResponse> getResult(@PathVariable Long id,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        Candidate candidate = candidateRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return ResponseEntity.ok(resultService.getResultById(id, candidate.getId()));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(@AuthenticationPrincipal UserDetails userDetails) {
        Candidate candidate = candidateRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        List<ResultResponse> results = resultService.getCandidateResults(candidate.getId());
        double avg = results.stream()
                .filter(res -> res.getOverallScore() != null)
                .mapToDouble(ResultResponse::getOverallScore)
                .average().orElse(0.0);
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSessions", results.size());
        stats.put("averageScore", Math.round(avg * 10.0) / 10.0);
        stats.put("latestSession", results.isEmpty() ? null : results.get(0));
        return ResponseEntity.ok(stats);
    }
}
