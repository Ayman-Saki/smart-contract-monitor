package com.intrenship.SmartContractMonitor.services;

import com.intrenship.SmartContractMonitor.entities.Summary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SummaryService {
    Summary generateSummary(Summary summary);

    Summary findById(Long id);

    List<Summary> findAll();
}
