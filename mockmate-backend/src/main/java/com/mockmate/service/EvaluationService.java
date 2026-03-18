package com.mockmate.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mockmate.config.OpenAIConfig;
import com.mockmate.model.Question;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EvaluationService {

    private final OkHttpClient okHttpClient;
    private final OpenAIConfig openAIConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> evaluate(Question question, String answerText) {
        String prompt = buildPrompt(question, answerText);
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", openAIConfig.getModel());
            requestBody.put("max_tokens", openAIConfig.getMaxTokens());
            requestBody.put("messages", List.of(
                Map.of("role", "system", "content",
                    "You are an expert technical interviewer. Evaluate interview answers objectively. " +
                    "Always respond with valid JSON only, no markdown, no explanation outside JSON."),
                Map.of("role", "user", "content", prompt)
            ));

            RequestBody body = RequestBody.create(
                    objectMapper.writeValueAsString(requestBody),
                    MediaType.get("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url(openAIConfig.getApiUrl())
                    .addHeader("Authorization", "Bearer " + openAIConfig.getApiKey())
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    log.error("OpenAI API error: {}", response.code());
                    return fallbackEvaluation();
                }
                return parseOpenAIResponse(response.body().string());
            }
        } catch (Exception e) {
            log.error("Error calling OpenAI API", e);
            return fallbackEvaluation();
        }
    }

    private String buildPrompt(Question question, String answerText) {
        return "Evaluate this interview answer.\n\n" +
               "Question: " + question.getContent() + "\n" +
               "Category: " + question.getCategory() + " | Difficulty: " + question.getDifficulty() + "\n" +
               "Expected keywords/concepts: " + question.getExpectedKeywords() + "\n\n" +
               "Candidate Answer: " + answerText + "\n\n" +
               "Respond ONLY with JSON in this exact format:\n" +
               "{\n" +
               "  \"score\": <integer 0-100>,\n" +
               "  \"feedback\": \"<detailed constructive feedback, 3-5 sentences>\",\n" +
               "  \"strengths\": \"<what the candidate did well>\",\n" +
               "  \"improvements\": \"<what could be improved>\",\n" +
               "  \"verdict\": \"<POOR|AVERAGE|GOOD|EXCELLENT>\"\n" +
               "}";
    }

    private Map<String, Object> parseOpenAIResponse(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        String content = root.path("choices").get(0).path("message").path("content").asText();
        content = content.replaceAll("```json|```", "").trim();

        JsonNode result = objectMapper.readTree(content);
        Map<String, Object> out = new HashMap<>();
        out.put("score", result.path("score").asInt(50));
        out.put("feedback", buildFullFeedback(result));
        return out;
    }

    private String buildFullFeedback(JsonNode node) {
        StringBuilder sb = new StringBuilder();
        if (!node.path("feedback").isMissingNode())
            sb.append(node.path("feedback").asText()).append("\n\n");
        if (!node.path("strengths").isMissingNode())
            sb.append("Strengths: ").append(node.path("strengths").asText()).append("\n");
        if (!node.path("improvements").isMissingNode())
            sb.append("Areas to improve: ").append(node.path("improvements").asText()).append("\n");
        if (!node.path("verdict").isMissingNode())
            sb.append("Verdict: ").append(node.path("verdict").asText());
        return sb.toString();
    }

    private Map<String, Object> fallbackEvaluation() {
        Map<String, Object> m = new HashMap<>();
        m.put("score", 50);
        m.put("feedback", "Evaluation service temporarily unavailable. A default score has been assigned.");
        return m;
    }
}
