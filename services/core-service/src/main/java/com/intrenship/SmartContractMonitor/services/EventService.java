package com.intrenship.SmartContractMonitor.services;

import com.intrenship.SmartContractMonitor.dtos.response.EventDetailResponse;
import com.intrenship.SmartContractMonitor.dtos.response.EventResponse;
import com.intrenship.SmartContractMonitor.dtos.response.EventStatsResponse;
import com.intrenship.SmartContractMonitor.entities.Event;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface EventService {
    Event recordEvent(Event event);

    Event findById(Long id);

    Optional<Event> findByTransactionHash(String transactionHash);

    List<Event> findAll();

    List<EventResponse> getAllEvents();

    EventResponse getEventById(Long id);

    EventDetailResponse getEventDetailsById(Long id);

    EventStatsResponse getEventStats();
}
