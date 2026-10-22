package com.intrenship.SmartContractMonitor.controllers;

import com.intrenship.SmartContractMonitor.dtos.response.EventDetailResponse;
import com.intrenship.SmartContractMonitor.dtos.response.EventResponse;
import com.intrenship.SmartContractMonitor.dtos.response.EventStatsResponse;
import com.intrenship.SmartContractMonitor.services.EventService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@AllArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public List<EventResponse> getAllEvents() {

        return eventService.getAllEvents();
    }

    @GetMapping("/stats")
    public EventStatsResponse getEventStats() {

        return eventService.getEventStats();
    }

    @GetMapping("/{id}")
    public EventDetailResponse getEventById(
            @PathVariable Long id
    ) {

        return eventService.getEventDetailsById(id);
    }
}