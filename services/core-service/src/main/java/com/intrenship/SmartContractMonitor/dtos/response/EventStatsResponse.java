package com.intrenship.SmartContractMonitor.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventStatsResponse {

    private long totalEvents;

    private long unusualEvents;

    private long highOrCriticalEvents;
}