package com.internship.ai_service.controller;

import com.internship.ai_service.dto.SummaryRequest;
import com.internship.ai_service.dto.SummaryResponse;
import com.internship.ai_service.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/summary")
@RequiredArgsConstructor
public class SummaryController {

    private final GeminiService geminiService;

    @PostMapping
    public SummaryResponse generateSummary(
            @RequestBody SummaryRequest request
    ) {

        String summary =
                geminiService.generateSummary(request);

        return new SummaryResponse(summary);
    }
}