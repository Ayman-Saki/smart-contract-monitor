package com.intrenship.SmartContractMonitor.dtos.KafkaMessage;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BlockchainEventDTO {

    private String eventName;

    private String contractAddress;

    private String transactionHash;

    private Long blockNumber;

    private LocalDateTime eventTimestamp;

    private String from;

    private String to;

    private BigInteger rawAmount;

    private BigDecimal amount;

    private Integer decimals;

    private String rawData;
}
