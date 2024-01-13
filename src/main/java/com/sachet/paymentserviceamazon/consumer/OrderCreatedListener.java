package com.sachet.paymentserviceamazon.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sachet.paymentserviceamazon.model.OrderCreated;
import com.sachet.paymentserviceamazon.repository.OrderRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedListener implements AcknowledgingMessageListener<String, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderCreatedListener.class);
    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;

    public OrderCreatedListener(ObjectMapper objectMapper,
                                OrderRepository orderRepository) {
        this.objectMapper = objectMapper;
        this.orderRepository = orderRepository;
    }

    @KafkaListener(
            topics = {"orderevent"},
            groupId = "${spring.kafka.paymentconsumers.group-id}",
            containerFactory = "kafkaOrderCreatedListenerContainerFactory"
    )
    @Override
    public void onMessage(ConsumerRecord<String, String> data, Acknowledgment acknowledgment) {
        LOGGER.info("Consuming the Order Created Event {}", data);
        try{
            OrderCreated order = objectMapper.readValue(data.value(), OrderCreated.class);
            assert acknowledgment != null;
            orderRepository.save(order);
            acknowledgment.acknowledge();
        }catch (JsonProcessingException ex){
            LOGGER.error("Error while processing the json {}", ex.getMessage());
        }
    }
}
