package com.internship.collector_service.service;

import com.internship.collector_service.Kafka.EventKafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.generated.Uint64;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.utils.Numeric;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import com.internship.collector_service.dto.BlockchainEventDTO;

import java.math.BigDecimal;
import java.util.Collections;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockchainService {

    private final Web3j web3j;
    private final EventKafkaProducer eventKafkaProducer;

    private static final String TRANSFER_TOPIC =
            "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef";

    private static final String APPROVAL_TOPIC =
            "0x8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925";

    private static final String INITIALIZED_TOPIC =
            EventEncoder.encode(
                    new org.web3j.abi.datatypes.Event(
                            "Initialized",
                            List.of(
                                    new TypeReference<Uint64>() {}
                            )
                    )
            );
    private static final String CONTRACT_ADDRESS =
            "0xD5f2c36B0C0439ED4eF42764D01A31Bcbe5E8e80";


    public void getLatestBlock() throws Exception {

        var blockNumber = web3j
                .ethBlockNumber()
                .send();

        System.out.println("Latest Sepolia block : " + blockNumber.getBlockNumber());
    }

    private int getTokenDecimals() throws Exception {

        Function function = new Function(
                "decimals",
                Collections.emptyList(),
                Collections.singletonList(
                        new org.web3j.abi.TypeReference<Uint8>() {}
                )
        );

        String encodedFunction = FunctionEncoder.encode(function);

        EthCall response = web3j
                .ethCall(
                        Transaction.createEthCallTransaction(
                                null,
                                CONTRACT_ADDRESS,
                                encodedFunction
                        ),
                        DefaultBlockParameter.valueOf("latest")
                )
                .send();

        if (response.hasError()) {
            throw new RuntimeException(
                    "Error calling decimals(): "
                            + response.getError().getMessage()
            );
        }

        List<Type> decoded = FunctionReturnDecoder.decode(
                response.getValue(),
                function.getOutputParameters()
        );

        if (decoded.isEmpty()) {
            throw new RuntimeException(
                    "Could not retrieve token decimals"
            );
        }

        return ((Uint8) decoded.get(0)).getValue().intValue();
    }

    private String detectEventName(Log log) {

        if (log.getTopics() == null || log.getTopics().isEmpty()) {
            return "Unknown";
        }

        String topic0 = log.getTopics().get(0);

        if (TRANSFER_TOPIC.equalsIgnoreCase(topic0)) {
            return "Transfer";
        }

        if (APPROVAL_TOPIC.equalsIgnoreCase(topic0)) {
            return "Approval";
        }

        if (INITIALIZED_TOPIC.equalsIgnoreCase(topic0)) {
            return "Initialized";
        }

        return "Unknown";
    }

    public void getHistoricalEvents() throws Exception {

        BigInteger fromBlock = BigInteger.valueOf(8683450);
        BigInteger toBlock = BigInteger.valueOf(8683450);

        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(fromBlock),
                DefaultBlockParameter.valueOf(toBlock),
                CONTRACT_ADDRESS
        );


        var logs = web3j
                .ethGetLogs(filter)
                .send();

        if (logs.hasError()) {

            System.out.println("Error code: " + logs.getError().getCode());
            System.out.println("Error message: " + logs.getError().getMessage());

            return;
        }

        System.out.println(
                "Blocks " + fromBlock
                        + " → " + toBlock
                        + " : "
                        + logs.getLogs().size()
                        + " events"
        );

        // Token decimals are the same for Transfer and Approval.
        int tokenDecimals = getTokenDecimals();

        logs.getLogs().forEach(logResult -> {

            try {

                // Convert LogResult -> actual Web3j Log
                Log log = (Log) logResult.get();

                // =================================
                // Detect event
                // =================================

                String eventName = detectEventName(log);

                // If the contract emits something we don't support,
                // simply ignore it.
                if ("Unknown".equals(eventName)) {

                    System.out.println(
                            "Unknown event ignored. Topic: "
                                    + log.getTopics().get(0)
                    );

                    return;
                }

                // =================================
                // Get block information
                // =================================

                var blockResponse = web3j
                        .ethGetBlockByNumber(
                                DefaultBlockParameter.valueOf(
                                        log.getBlockNumber()
                                ),
                                false
                        )
                        .send();

                var block = blockResponse.getBlock();

                if (block == null) {

                    System.out.println(
                            "Block not found: "
                                    + log.getBlockNumber()
                    );

                    return;
                }

                // =================================
                // Common information
                // =================================

                String transactionHash =
                        log.getTransactionHash();

                BigInteger blockNumber =
                        log.getBlockNumber();

                BigInteger timestamp =
                        block.getTimestamp();

                LocalDateTime eventTimestamp =
                        LocalDateTime.ofInstant(
                                Instant.ofEpochSecond(
                                        timestamp.longValue()
                                ),
                                ZoneOffset.UTC
                        );

                // =================================
                // Event-specific values
                // =================================

                String from = null;
                String to = null;

                BigInteger rawAmount = null;
                BigDecimal amount = null;
                Integer decimals = null;

                String rawData = log.getData();

                // =================================
                // Decode depending on event type
                // =================================

                switch (eventName) {

                    case "Transfer" -> {

                        from =
                                "0x" +
                                        log.getTopics()
                                                .get(1)
                                                .substring(26);

                        to =
                                "0x" +
                                        log.getTopics()
                                                .get(2)
                                                .substring(26);

                        rawAmount =
                                Numeric.toBigInt(log.getData());

                        decimals = tokenDecimals;

                        amount =
                                new BigDecimal(rawAmount)
                                        .divide(
                                                BigDecimal.TEN.pow(decimals)
                                        );
                    }

                    case "Approval" -> {

                        // Approval:
                        // topic[1] = owner
                        // topic[2] = spender

                        from =
                                "0x" +
                                        log.getTopics()
                                                .get(1)
                                                .substring(26);

                        to =
                                "0x" +
                                        log.getTopics()
                                                .get(2)
                                                .substring(26);

                        rawAmount =
                                Numeric.toBigInt(log.getData());

                        decimals = tokenDecimals;

                        amount =
                                new BigDecimal(rawAmount)
                                        .divide(
                                                BigDecimal.TEN.pow(decimals)
                                        );
                    }

                    case "Initialized" -> {

                        /*
                         * Initialized(uint64 version)
                         *
                         * No from address
                         * No to address
                         * No token amount
                         *
                         * We keep the encoded version in rawData.
                         */

                        from = null;
                        to = null;
                        rawAmount = null;
                        amount = null;
                        decimals = null;
                    }
                }

                // =================================
                // Create DTO
                // =================================

                BlockchainEventDTO event =
                        new BlockchainEventDTO(
                                eventName,
                                log.getAddress(),
                                transactionHash,
                                blockNumber.longValue(),
                                eventTimestamp,
                                from,
                                to,
                                rawAmount,
                                amount,
                                decimals,
                                rawData
                        );

                // =================================
                // Display DTO
                // =================================

                System.out.println("=================================");
                System.out.println("Blockchain Event DTO");
                System.out.println("=================================");
                System.out.println("Event       : " + event.getEventName());
                System.out.println("Address     : " + event.getContractAddress());
                System.out.println("Transaction : " + event.getTransactionHash());
                System.out.println("Block       : " + event.getBlockNumber());
                System.out.println("Timestamp   : " + event.getEventTimestamp());
                System.out.println("From        : " + event.getFrom());
                System.out.println("To          : " + event.getTo());
                System.out.println("Raw amount  : " + event.getRawAmount());
                System.out.println("Decimals    : " + event.getDecimals());
                System.out.println("Amount      : " + event.getAmount());
                System.out.println("Raw data    : " + event.getRawData());
                System.out.println("=================================");

                // =================================
                // Send to Kafka
                // =================================

                eventKafkaProducer.sendEvent(event);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
