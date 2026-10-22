package com.intrenship.SmartContractMonitor.mapper;

import com.intrenship.SmartContractMonitor.dtos.response.EventResponse;
import com.intrenship.SmartContractMonitor.entities.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public EventResponse toResponse(Event event){

        EventResponse response = new EventResponse();

        response.setId(event.getId());
        response.setEventName(event.getEventName());
        response.setTransactionHash(event.getTransactionHash());
        response.setBlockNumber(event.getBlockNumber());
        response.setEventTimestamp(event.getEventTimestamp());
        response.setSeverity(event.getSeverity());
        response.setStatus(event.getStatus());
        response.setBlockchainType(
                event.getSmartContract().getBlockchainType()
        );

        return response;

    }
}
