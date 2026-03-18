package com.mockmate.repository;

import com.mockmate.model.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttemptRepository extends JpaRepository<Attempt, Long> {

    List<Attempt> findByCandidateId(Long candidateId);

    List<Attempt> findByCandidateIdAndQuestionId(Long candidateId, Long questionId);

    @Query("SELECT AVG(a.score) FROM Attempt a WHERE a.candidate.id = :candidateId AND a.score IS NOT NULL")
    Double findAverageScoreByCandidateId(@Param("candidateId") Long candidateId);

    @Query("SELECT a FROM Attempt a WHERE a.candidate.id = :candidateId ORDER BY a.submittedAt DESC")
    List<Attempt> findRecentByCandidateId(@Param("candidateId") Long candidateId,
                                          org.springframework.data.domain.Pageable pageable);
}
