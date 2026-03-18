package com.mockmate.repository;

import com.mockmate.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByCategory(Question.Category category);

    List<Question> findByDifficulty(Question.Difficulty difficulty);

    List<Question> findByCategoryAndDifficulty(Question.Category category,
                                               Question.Difficulty difficulty);

    @Query("SELECT q FROM Question q ORDER BY RAND()")
    List<Question> findRandomQuestions(org.springframework.data.domain.Pageable pageable);
}
