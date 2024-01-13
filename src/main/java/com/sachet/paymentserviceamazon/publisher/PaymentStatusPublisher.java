package com.sachet.paymentserviceamazon.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sachet.paymentserviceamazon.model.PaymentStatus;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class PaymentStatusPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentStatusPublisher.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String paymentStatusTopic;

    public PaymentStatusPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                  ObjectMapper objectMapper,
                                  @Value("${spring.kafka.paymentTopic}") String paymentStatusTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.paymentStatusTopic = paymentStatusTopic;
    }

    public CompletableFuture<SendResult<String, String>>
    publishPaymentStatus(PaymentStatus paymentStatus) throws JsonProcessingException {
        String publish = objectMapper.writeValueAsString(paymentStatus);
        LOGGER.info("Sending payment status event for the orderID:{}", paymentStatus.getOrderId());
        ProducerRecord<String, String> producerRecord =
                new ProducerRecord<>(paymentStatusTopic,paymentStatus.getPaymentId(), publish);
        return kafkaTemplate
                .send(producerRecord)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        LOGGER.error("Error while publishing the event {}", throwable.getMessage());
                    }else {
                        LOGGER.info("Successfully published the event {}", result);
                    }
                });
    }
}
