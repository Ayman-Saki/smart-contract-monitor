package com.intrenship.SmartContractMonitor.repositories;

import com.intrenship.SmartContractMonitor.entities.SmartContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SmartContractRepository  extends JpaRepository<SmartContract, Long> {
    Optional<SmartContract> findByAddressIgnoreCase(String address);

    boolean existsByAddress(String address);
}

