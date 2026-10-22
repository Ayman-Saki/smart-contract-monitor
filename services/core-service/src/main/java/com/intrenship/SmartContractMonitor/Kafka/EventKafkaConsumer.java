package com.intrenship.SmartContractMonitor.Kafka;

import com.intrenship.SmartContractMonitor.dtos.KafkaMessage.BlockchainEventDTO;
import com.intrenship.SmartContractMonitor.dtos.ai.SummaryRequest;
import com.intrenship.SmartContractMonitor.dtos.analysis.AnalysisResult;
import com.intrenship.SmartContractMonitor.entities.Event;
import com.intrenship.SmartContractMonitor.entities.SmartContract;
import com.intrenship.SmartContractMonitor.repositories.EventRepository;
import com.intrenship.SmartContractMonitor.repositories.SmartContractRepository;
import com.intrenship.SmartContractMonitor.services.ai.AIServiceClient;
import com.intrenship.SmartContractMonitor.services.analysis.EventAnalyzer;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import com.intrenship.SmartContractMonitor.entities.Summary;
import com.intrenship.SmartContractMonitor.repositories.SummaryRepository;

@Service
@RequiredArgsConstructor
public class EventKafkaConsumer {

    private final ObjectMapper objectMapper;

    private final SmartContractRepository smartContractRepository;

    private final EventRepository eventRepository;

    private final EventAnalyzer eventAnalyzer;

    private final AIServiceClient aiServiceClient;

    private final SummaryRepository summaryRepository;

    @KafkaListener(
            topics = "smart-contract-events",
            groupId = "smart-contract-monitor"
    )
    public void consumeEvent(String message) {

        try {

            // =====================================
            // 1. JSON -> DTO
            // =====================================

            BlockchainEventDTO dto =
                    objectMapper.readValue(
                            message,
                            BlockchainEventDTO.class
                    );

            System.out.println(
                    "Kafka event received: "
                            + dto.getEventName()
            );

            if (eventRepository
                    .findByTransactionHash(dto.getTransactionHash())
                    .isPresent()) {

                System.out.println(
                        "Duplicate event ignored: "
                                + dto.getTransactionHash()
                );

                return;
            }

            // =====================================
            // 2. Find Smart Contract
            // =====================================

            SmartContract smartContract =
                    smartContractRepository
                            .findByAddressIgnoreCase(
                                    dto.getContractAddress()
                            )
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Smart contract not found: "
                                                    + dto.getContractAddress()
                                    )
                            );

            // =====================================
            // 3. Analyze Event
            // =====================================

            AnalysisResult analysisResult =
                    eventAnalyzer.analyze(
                            dto,
                            smartContract
                    );

            // =====================================
            // 4. Create Event Entity
            // =====================================

            Event event = new Event();

            event.setEventName(
                    dto.getEventName()
            );

            event.setTransactionHash(
                    dto.getTransactionHash()
            );

            event.setBlockNumber(
                    dto.getBlockNumber()
            );

            event.setEventTimestamp(
                    dto.getEventTimestamp()
            );

            event.setFromAddress(
                    dto.getFrom()
            );

            event.setToAddress(
                    dto.getTo()
            );

            event.setAmount(
                    dto.getAmount()
            );

            event.setRawData(
                    dto.getRawData()
            );

            event.setStatus(
                    analysisResult.getStatus()
            );

            event.setSeverity(
                    analysisResult.getSeverity()
            );

            event.setSmartContract(
                    smartContract
            );

            // =====================================
            // 5. Save Event to PostgreSQL
            // =====================================

            Event savedEvent =
                    eventRepository.save(event);

            // =====================================
            // 6. Create AI Summary Request
            // =====================================

            SummaryRequest summaryRequest =
                    new SummaryRequest(
                            dto.getEventName(),
                            dto.getTransactionHash(),
                            dto.getBlockNumber(),
                            dto.getContractAddress(),

                            dto.getFrom(),
                            dto.getTo(),
                            dto.getAmount(),

                            analysisResult
                                    .getStatus()
                                    .name(),

                            analysisResult
                                    .getSeverity()
                                    .name(),

                            analysisResult
                                    .getRiskScore(),

                            analysisResult
                                    .getReasons()
                    );

            // =====================================
            // 7. Call AI Service
            // =====================================

            String generatedSummary =
                    aiServiceClient.generateSummary(
                            summaryRequest
                    );

            // =====================================
            // 8. Create and Save Summary
            // =====================================

            Summary summary = new Summary();

            summary.setContent(
                    generatedSummary
            );

            summary.setModelName(
                    "gemini-3.5-flash-lite"
            );

            summary.setEvent(
                    savedEvent
            );

            Summary savedSummary =
                    summaryRepository.save(summary);

            // =====================================
            // 9. Display Result
            // =====================================

            System.out.println("=================================");
            System.out.println("EVENT ANALYZED AND SAVED");
            System.out.println("=================================");
            System.out.println("ID         : " + savedEvent.getId());
            System.out.println("Event      : " + savedEvent.getEventName());
            System.out.println("Status     : " + savedEvent.getStatus());
            System.out.println("Severity   : " + savedEvent.getSeverity());
            System.out.println("Risk score : " + analysisResult.getRiskScore());
            System.out.println("Reasons    : " + analysisResult.getReasons());
            System.out.println("=================================");

            System.out.println("=================================");
            System.out.println("AI SUMMARY");
            System.out.println("=================================");
            System.out.println(generatedSummary);
            System.out.println("=================================");

            System.out.println("=================================");
            System.out.println("AI SUMMARY SAVED");
            System.out.println("=================================");
            System.out.println("Summary ID : " + savedSummary.getId());
            System.out.println("Model      : " + savedSummary.getModelName());
            System.out.println("Event ID   : " + savedEvent.getId());
            System.out.println("Content    : " + savedSummary.getContent());
            System.out.println("=================================");

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}