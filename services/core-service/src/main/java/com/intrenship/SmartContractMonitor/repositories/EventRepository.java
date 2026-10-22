package com.intrenship.SmartContractMonitor.repositories;

import com.intrenship.SmartContractMonitor.entities.Event;
import com.intrenship.SmartContractMonitor.entities.SmartContract;
import com.intrenship.SmartContractMonitor.enums.EventStatus;
import com.intrenship.SmartContractMonitor.enums.Severity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByTransactionHash(String transactionHash);

    long countBySmartContractAndEventTimestampBetween(
            SmartContract smartContract,
            LocalDateTime start,
            LocalDateTime end
    );

    long countBySmartContractAndEventNameAndEventTimestampBetween(
            SmartContract smartContract,
            String eventName,
            LocalDateTime start,
            LocalDateTime end
    );

    long countBySmartContractAndFromAddressAndEventTimestampBetween(
            SmartContract smartContract,
            String fromAddress,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Event> findBySmartContractAndEventNameAndAmountIsNotNull(
            SmartContract smartContract,
            String eventName
    );

    long countByStatus(EventStatus status);

    long countBySeverityIn(List<Severity> severities);


}
