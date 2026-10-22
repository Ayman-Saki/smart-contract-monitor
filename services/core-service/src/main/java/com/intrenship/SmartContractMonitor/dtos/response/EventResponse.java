package com.intrenship.SmartContractMonitor.dtos.response;

import com.intrenship.SmartContractMonitor.enums.EventStatus;
import com.intrenship.SmartContractMonitor.enums.Severity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class EventResponse {
    private Long id;

    private String eventName;

    private String transactionHash;

    private Long blockNumber;

    private LocalDateTime eventTimestamp;

    private Severity severity;

    private EventStatus status;

    private String blockchainType;
}
