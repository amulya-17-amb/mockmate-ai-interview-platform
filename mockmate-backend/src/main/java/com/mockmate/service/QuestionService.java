package com.mockmate.service;

import com.mockmate.model.Question;
import com.mockmate.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public Question getQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + id));
    }

    public List<Question> getByCategory(String category) {
        return questionRepository.findByCategory(Question.Category.valueOf(category.toUpperCase()));
    }

    public List<Question> getByDifficulty(String difficulty) {
        return questionRepository.findByDifficulty(Question.Difficulty.valueOf(difficulty.toUpperCase()));
    }

    public List<Question> getByCategoryAndDifficulty(String category, String difficulty) {
        return questionRepository.findByCategoryAndDifficulty(
                Question.Category.valueOf(category.toUpperCase()),
                Question.Difficulty.valueOf(difficulty.toUpperCase()));
    }

    public List<Question> getRandomQuestions(int count) {
        return questionRepository.findRandomQuestions(PageRequest.of(0, count));
    }

    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }

    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }
}
