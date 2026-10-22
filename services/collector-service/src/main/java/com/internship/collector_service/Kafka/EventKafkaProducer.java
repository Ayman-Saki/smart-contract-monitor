package com.internship.collector_service.Kafka;


import com.internship.collector_service.dto.BlockchainEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class EventKafkaProducer {

    private static final String TOPIC = "smart-contract-events";

    private final KafkaTemplate<String , String > kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendEvent(BlockchainEventDTO event) {

        String message = objectMapper.writeValueAsString(event);

        kafkaTemplate.send(
                TOPIC,
                event.getTransactionHash(),
                message
        );

        System.out.println(
                "Kafka event sent : " + message
        );
    }
}
