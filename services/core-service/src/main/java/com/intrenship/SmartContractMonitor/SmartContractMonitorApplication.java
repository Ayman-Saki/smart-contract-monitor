package com.intrenship.SmartContractMonitor;


import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@EnableKafka
@SpringBootApplication

public class SmartContractMonitorApplication  {


	public static void main(String[] args) {
		SpringApplication.run(SmartContractMonitorApplication.class, args);
	}


}
