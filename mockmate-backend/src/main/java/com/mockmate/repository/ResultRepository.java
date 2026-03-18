package com.mockmate.repository;

import com.mockmate.model.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByCandidateIdOrderByGeneratedAtDesc(Long candidateId);
    Optional<Result> findByCandidateIdAndSessionLabel(Long candidateId, String sessionLabel);
    long countByCandidateId(Long candidateId);
}
