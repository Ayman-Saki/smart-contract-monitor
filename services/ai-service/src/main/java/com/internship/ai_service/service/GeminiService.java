package com.internship.ai_service.service;

import com.internship.ai_service.dto.SummaryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final RestClient geminiRestClient;

    public String generateSummary(SummaryRequest event) {

        String prompt = buildPrompt(event);

        Map<String, Object> requestBody = Map.of(
                "contents",
                List.of(
                        Map.of(
                                "parts",
                                List.of(
                                        Map.of(
                                                "text",
                                                prompt
                                        )
                                )
                        )
                )
        );

        Map<String, Object> response =
                geminiRestClient
                        .post()
                        .uri(
                                "/models/gemini-3.5-flash-lite:generateContent"
                        )
                        .body(requestBody)
                        .retrieve()
                        .body(Map.class);

        return extractText(response);
    }

    private String buildPrompt(SummaryRequest event) {

        return """
                You are an assistant for a smart-contract monitoring system.

                Generate a concise professional summary of the blockchain event below.

                Rules:
                - Use only the supplied information.
                - Do not invent facts.
                - Do not claim that an attack, hack, or exploit occurred unless explicitly stated.
                - If the event is unusual, explain the supplied anomaly reasons.
                - Keep the summary to 2 or 3 short sentences.
                - Write for someone monitoring blockchain smart-contract activity.

                Event name: %s
                Transaction hash: %s
                Block number: %s
                Contract address: %s
                From: %s
                To: %s
                Amount: %s
                Status: %s
                Severity: %s
                Risk score: %s
                Reasons: %s
                """.formatted(
                event.getEventName(),
                event.getTransactionHash(),
                event.getBlockNumber(),
                event.getContractAddress(),
                event.getFrom(),
                event.getTo(),
                event.getAmount(),
                event.getStatus(),
                event.getSeverity(),
                event.getRiskScore(),
                event.getReasons()
        );
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> response) {

        if (response == null) {
            throw new RuntimeException(
                    "Gemini returned an empty response"
            );
        }

        List<Map<String, Object>> candidates =
                (List<Map<String, Object>>)
                        response.get("candidates");

        if (candidates == null || candidates.isEmpty()) {
            throw new RuntimeException(
                    "Gemini returned no candidates"
            );
        }

        Map<String, Object> content =
                (Map<String, Object>)
                        candidates.get(0).get("content");

        if (content == null) {
            throw new RuntimeException(
                    "Gemini returned no content"
            );
        }

        List<Map<String, Object>> parts =
                (List<Map<String, Object>>)
                        content.get("parts");

        if (parts == null || parts.isEmpty()) {
            throw new RuntimeException(
                    "Gemini returned no text"
            );
        }

        Object text = parts.get(0).get("text");

        if (text == null) {
            throw new RuntimeException(
                    "Gemini response does not contain text"
            );
        }

        return text.toString();
    }
}