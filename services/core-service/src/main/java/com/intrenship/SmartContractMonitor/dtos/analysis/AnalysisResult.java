package com.intrenship.SmartContractMonitor.dtos.analysis;

import com.intrenship.SmartContractMonitor.enums.EventStatus;
import com.intrenship.SmartContractMonitor.enums.Severity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AnalysisResult {

    private EventStatus status;
    private Severity severity;
    private int riskScore;
    private List<String> reasons;
}