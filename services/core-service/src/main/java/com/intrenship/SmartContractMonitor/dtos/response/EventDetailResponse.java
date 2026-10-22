package com.intrenship.SmartContractMonitor.dtos.response;

import com.intrenship.SmartContractMonitor.enums.EventStatus;
import com.intrenship.SmartContractMonitor.enums.Severity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDetailResponse {

    private Long id;

    private String eventName;

    private String transactionHash;

    private Long blockNumber;

    private LocalDateTime eventTimestamp;

    private String fromAddress;

    private String toAddress;

    private BigDecimal amount;

    private Severity severity;

    private EventStatus status;

    private String blockchainType;

    private String contractAddress;

    private String rawData;

    private String summary;

    private String summaryModel;
}