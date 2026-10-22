package com.intrenship.SmartContractMonitor.repositories;

import com.intrenship.SmartContractMonitor.entities.Summary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SummaryRepository extends JpaRepository<Summary, Long> {
}
