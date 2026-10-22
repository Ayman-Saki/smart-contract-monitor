package com.intrenship.SmartContractMonitor.dtos.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SummaryRequest {

    private String eventName;
    private String transactionHash;
    private Long blockNumber;
    private String contractAddress;

    private String from;
    private String to;

    private BigDecimal amount;

    private String status;
    private String severity;
    private Integer riskScore;

    private List<String> reasons;
}