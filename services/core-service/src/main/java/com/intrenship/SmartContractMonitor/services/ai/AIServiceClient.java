package com.intrenship.SmartContractMonitor.services.ai;

import com.intrenship.SmartContractMonitor.dtos.ai.SummaryRequest;
import com.intrenship.SmartContractMonitor.dtos.ai.SummaryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AIServiceClient {

    private final RestClient restClient;

    public AIServiceClient(
            @Value("${ai.service.url}") String aiServiceUrl
    ) {

        this.restClient = RestClient.builder()
                .baseUrl(aiServiceUrl)
                .build();
    }

    public String generateSummary(
            SummaryRequest request
    ) {

        SummaryResponse response =
                restClient
                        .post()
                        .uri("/api/summary")
                        .body(request)
                        .retrieve()
                        .body(SummaryResponse.class);

        if (response == null
                || response.getSummary() == null) {

            throw new RuntimeException(
                    "AI Service returned no summary"
            );
        }

        return response.getSummary();
    }
}