package com.intrenship.SmartContractMonitor.entities;

import com.intrenship.SmartContractMonitor.enums.EventStatus;
import com.intrenship.SmartContractMonitor.enums.Severity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event extends BaseEntity{

    @NotBlank
    @Column(nullable = false, length = 100)
    private String eventName;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String transactionHash;

    @Column(nullable = false)
    private Long blockNumber;

    @Column(nullable = false)
    private LocalDateTime eventTimestamp;

    @Column(columnDefinition = "TEXT")
    private String rawData;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @Column(length = 255)
    private String fromAddress;

    @Column(length = 255)
    private String toAddress;

    @Column(precision = 78, scale = 18)
    private BigDecimal amount;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "smart_contract_id",
            nullable = false
    )
    private SmartContract smartContract;

    @OneToOne(
            mappedBy = "event",
            cascade = CascadeType.ALL
    )
    private Summary summary;




}
