package com.intrenship.SmartContractMonitor.services;

import com.intrenship.SmartContractMonitor.entities.Summary;
import com.intrenship.SmartContractMonitor.repositories.SummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SummaryServiceImpl implements SummaryService {

    private final SummaryRepository summaryRepository;

    @Override
    public Summary generateSummary(Summary summary) {
        return summaryRepository.save(summary);
    }

    @Override
    public Summary findById(Long id) {
        return summaryRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Summary not found"));
    }

    @Override
    public List<Summary> findAll() {
        return summaryRepository.findAll();
    }
}
