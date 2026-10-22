package com.intrenship.SmartContractMonitor.services.analysis;

import com.intrenship.SmartContractMonitor.dtos.KafkaMessage.BlockchainEventDTO;
import com.intrenship.SmartContractMonitor.dtos.analysis.AnalysisResult;
import com.intrenship.SmartContractMonitor.entities.Event;
import com.intrenship.SmartContractMonitor.entities.SmartContract;
import com.intrenship.SmartContractMonitor.enums.EventStatus;
import com.intrenship.SmartContractMonitor.enums.Severity;
import com.intrenship.SmartContractMonitor.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventAnalyzer {

    private final EventRepository eventRepository;

    private static final String ZERO_ADDRESS =
            "0x0000000000000000000000000000000000000000";

    private static final BigInteger MAX_UINT256 =
            BigInteger.TWO.pow(256).subtract(BigInteger.ONE);

    public AnalysisResult analyze(
            BlockchainEventDTO dto,
            SmartContract smartContract
    ) {

        int score = 0;
        List<String> reasons = new ArrayList<>();

        // =========================================
        // 1. EVENT-SPECIFIC SEMANTIC RULES
        // =========================================

        // Transfer: Mint
        if ("Transfer".equalsIgnoreCase(dto.getEventName())
                && dto.getFrom() != null
                && ZERO_ADDRESS.equalsIgnoreCase(dto.getFrom())) {

            score += 20;
            reasons.add("Token mint detected");
        }

        // Transfer: Burn
        if ("Transfer".equalsIgnoreCase(dto.getEventName())
                && dto.getTo() != null
                && ZERO_ADDRESS.equalsIgnoreCase(dto.getTo())) {

            score += 20;
            reasons.add("Token burn detected");
        }

        // =========================================
        // 2. REPEATED INITIALIZATION
        // =========================================

        if ("Initialized".equalsIgnoreCase(dto.getEventName())) {

            long previousInitializations =
                    eventRepository
                            .countBySmartContractAndEventNameAndEventTimestampBetween(
                                    smartContract,
                                    "Initialized",
                                    LocalDateTime.of(1970, 1, 1, 0, 0),
                                    dto.getEventTimestamp()
                            );

            if (previousInitializations > 0) {

                score += 50;
                reasons.add("Repeated contract initialization detected");
            }
        }

        // =========================================
        // 3. RECENT EVENT FREQUENCY
        // =========================================

        LocalDateTime oneMinuteBefore =
                dto.getEventTimestamp().minusMinutes(1);

        long recentEvents =
                eventRepository
                        .countBySmartContractAndEventTimestampBetween(
                                smartContract,
                                oneMinuteBefore,
                                dto.getEventTimestamp()
                        );

        if (recentEvents >= 20) {

            score += 25;
            reasons.add("High event frequency detected");
        }

        // =========================================
        // 4. SAME EVENT TYPE FREQUENCY
        // =========================================

        long recentSameType =
                eventRepository
                        .countBySmartContractAndEventNameAndEventTimestampBetween(
                                smartContract,
                                dto.getEventName(),
                                oneMinuteBefore,
                                dto.getEventTimestamp()
                        );

        if (recentSameType >= 10) {

            score += 20;
            reasons.add(
                    "High frequency of "
                            + dto.getEventName()
                            + " events detected"
            );
        }

        // =========================================
        // 5. REPEATED ACTOR ACTIVITY
        // =========================================

        if (dto.getFrom() != null) {

            long actorActivity =
                    eventRepository
                            .countBySmartContractAndFromAddressAndEventTimestampBetween(
                                    smartContract,
                                    dto.getFrom(),
                                    oneMinuteBefore,
                                    dto.getEventTimestamp()
                            );

            if (actorActivity >= 10) {

                score += 20;
                reasons.add("Repeated activity from the same address");
            }
        }


        // =========================================
        // Unlimited Approval detection
        // =========================================

        if ("Approval".equalsIgnoreCase(dto.getEventName())
                && dto.getRawAmount() != null
                && dto.getRawAmount().equals(MAX_UINT256)) {

            score += 30;
            reasons.add("Unlimited token approval detected");
        }

        // =========================================
        // 6. HISTORICAL AMOUNT ANOMALY
        // =========================================

        if (dto.getAmount() != null) {

            List<Event> historicalEvents =
                    eventRepository
                            .findBySmartContractAndEventNameAndAmountIsNotNull(
                                    smartContract,
                                    dto.getEventName()
                            );

            // We need some history before comparing.
            if (historicalEvents.size() >= 5) {

                BigDecimal total = historicalEvents
                        .stream()
                        .map(Event::getAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

                BigDecimal average =
                        total.divide(
                                BigDecimal.valueOf(historicalEvents.size()),
                                18,
                                RoundingMode.HALF_UP
                        );

                // Current amount >= 5x historical average
                if (average.compareTo(BigDecimal.ZERO) > 0) {

                    BigDecimal ratio =
                            dto.getAmount().divide(
                                    average,
                                    4,
                                    RoundingMode.HALF_UP
                            );

                    if (ratio.compareTo(
                            BigDecimal.valueOf(10)
                    ) >= 0) {

                        score += 40;
                        reasons.add(
                                "Amount is extremely high compared with historical activity"
                        );

                    } else if (ratio.compareTo(
                            BigDecimal.valueOf(5)
                    ) >= 0) {

                        score += 30;
                        reasons.add(
                                "Amount is unusually high compared with historical activity"
                        );
                    }
                }
            }
        }

        // =========================================
        // 7. DETERMINE STATUS + SEVERITY
        // =========================================

        EventStatus status;
        Severity severity;

        if (score >= 70) {

            status = EventStatus.UNUSUAL;
            severity = Severity.CRITICAL;

        } else if (score >= 40) {

            status = EventStatus.UNUSUAL;
            severity = Severity.HIGH;

        } else if (score >= 20) {

            status = EventStatus.UNUSUAL;
            severity = Severity.MEDIUM;

        } else {

            status = EventStatus.NORMAL;
            severity = Severity.LOW;
        }

        return new AnalysisResult(
                status,
                severity,
                score,
                reasons
        );
    }
}