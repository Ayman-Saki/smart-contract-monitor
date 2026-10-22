package com.internship.collector_service;

import com.internship.collector_service.service.BlockchainService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class CollectorServiceApplication implements CommandLineRunner {

	private final BlockchainService blockchainService ;

	public static void main(String[] args) {

		SpringApplication.run(CollectorServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		blockchainService.getLatestBlock();
		blockchainService.getHistoricalEvents();
	}
}
