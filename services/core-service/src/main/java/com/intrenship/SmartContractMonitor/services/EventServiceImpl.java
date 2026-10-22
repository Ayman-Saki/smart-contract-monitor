package com.intrenship.SmartContractMonitor.services;

import com.intrenship.SmartContractMonitor.dtos.response.EventDetailResponse;
import com.intrenship.SmartContractMonitor.dtos.response.EventResponse;
import com.intrenship.SmartContractMonitor.dtos.response.EventStatsResponse;
import com.intrenship.SmartContractMonitor.entities.Event;
import com.intrenship.SmartContractMonitor.enums.EventStatus;
import com.intrenship.SmartContractMonitor.enums.Severity;
import com.intrenship.SmartContractMonitor.mapper.EventMapper;
import com.intrenship.SmartContractMonitor.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    @Override
    public Event recordEvent(Event event) {
        if (eventRepository.findByTransactionHash(event.getTransactionHash()).isPresent()) {
            throw new RuntimeException("Transaction already exists.");
        }

        return eventRepository.save(event);
    }


    @Override
    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found."));
    }

    @Override
    public Optional<Event> findByTransactionHash(String transactionHash) {
        return eventRepository.findByTransactionHash(transactionHash);
    }

    @Override
    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    @Override
    public List<EventResponse> getAllEvents() {

        List<Event> events = eventRepository.findAll();

        return events.stream()
                .map(eventMapper::toResponse)
                .toList();

    }

    @Override
    public EventResponse getEventById(Long id) {

        Event event = findById(id);

        return eventMapper.toResponse(event);
    }

    @Override
    public EventDetailResponse getEventDetailsById(Long id) {

        Event event = findById(id);

        return EventDetailResponse.builder()

                .id(event.getId())

                .eventName(
                        event.getEventName()
                )

                .transactionHash(
                        event.getTransactionHash()
                )

                .blockNumber(
                        event.getBlockNumber()
                )

                .eventTimestamp(
                        event.getEventTimestamp()
                )

                .fromAddress(
                        event.getFromAddress()
                )

                .toAddress(
                        event.getToAddress()
                )

                .amount(
                        event.getAmount()
                )

                .severity(
                        event.getSeverity()
                )

                .status(
                        event.getStatus()
                )

                .blockchainType(
                        event.getSmartContract()
                                .getBlockchainType()
                )

                .contractAddress(
                        event.getSmartContract()
                                .getAddress()
                )

                .rawData(
                        event.getRawData()
                )

                .summary(
                        event.getSummary() != null
                                ? event.getSummary().getContent()
                                : null
                )

                .summaryModel(
                        event.getSummary() != null
                                ? event.getSummary().getModelName()
                                : null
                )

                .build();
    }

    @Override
    public EventStatsResponse getEventStats() {

        long totalEvents =
                eventRepository.count();

        long unusualEvents =
                eventRepository.countByStatus(
                        EventStatus.UNUSUAL
                );

        long highOrCriticalEvents =
                eventRepository.countBySeverityIn(
                        List.of(
                                Severity.HIGH,
                                Severity.CRITICAL
                        )
                );

        return new EventStatsResponse(
                totalEvents,
                unusualEvents,
                highOrCriticalEvents
        );
    }
}
