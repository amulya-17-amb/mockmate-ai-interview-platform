package com.mockmate.controller;

import com.mockmate.model.Question;
import com.mockmate.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<List<Question>> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty) {

        if (category != null && difficulty != null)
            return ResponseEntity.ok(questionService.getByCategoryAndDifficulty(category, difficulty));
        if (category != null)
            return ResponseEntity.ok(questionService.getByCategory(category));
        if (difficulty != null)
            return ResponseEntity.ok(questionService.getByDifficulty(difficulty));
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    @GetMapping("/random")
    public ResponseEntity<List<Question>> getRandom(@RequestParam(defaultValue = "5") int count) {
        return ResponseEntity.ok(questionService.getRandomQuestions(count));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Question> create(@RequestBody Question question) {
        return ResponseEntity.ok(questionService.createQuestion(question));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
