package com.intrenship.SmartContractMonitor.services;

import com.intrenship.SmartContractMonitor.entities.SmartContract;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface SmartContractService {
    SmartContract registerContract(SmartContract smartContract);

    SmartContract findById(Long id);

    Optional<SmartContract> findByAddress(String address);

    List<SmartContract> findAll();
}
