package com.intrenship.SmartContractMonitor.services;

import com.intrenship.SmartContractMonitor.entities.SmartContract;
import com.intrenship.SmartContractMonitor.repositories.SmartContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SmartContractServiceImpl implements SmartContractService  {

    private final SmartContractRepository smartContractRepository;
    @Override
    public SmartContract registerContract(SmartContract smartContract) {
        if (smartContractRepository.existsByAddress(smartContract.getAddress())) {
            throw new RuntimeException("Smart contract already exists.");
        }

        return smartContractRepository.save(smartContract);
    }

    @Override
    public SmartContract findById(Long id) {
        return smartContractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Smart contract not found."));
    }

    @Override
    public Optional<SmartContract> findByAddress(String address) {
        return smartContractRepository.findByAddressIgnoreCase(address);
    }

    @Override
    public List<SmartContract> findAll() {
        return smartContractRepository.findAll();
    }
}
