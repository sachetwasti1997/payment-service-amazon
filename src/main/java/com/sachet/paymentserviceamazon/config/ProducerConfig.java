package com.sachet.paymentserviceamazon.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class ProducerConfig {

    private final String paymentStatusTopic;

    public ProducerConfig(@Value("${spring.kafka.paymentTopic}") String paymentStatusTopic) {
        this.paymentStatusTopic = paymentStatusTopic;
    }

    @Bean
    public NewTopic paymentCreatedTopic() {
        return TopicBuilder
                .name(paymentStatusTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
