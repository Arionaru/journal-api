package com.example.journalapi.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.protocol.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaAutoConfiguration {
    private final KafkaProperties kafkaProperties;
    @Value("${spring.kafka.producer.linger-ms:#{10}}")
    private Integer lingerMs;
    @Value("${spring.kafka.producer.enable-idempotence:#{true}}")
    private Boolean enableIdempotence;
    @Value("${spring.kafka.producer.max-request-size}")
    private Integer maxRequestSize;

    @Bean(name = "journalKafkaTemplate")
    @ConditionalOnMissingBean(name = "journalMessageKafkaTemplate")
    @ConditionalOnProperty("spring.kafka.topics.journal-request")
    public KafkaTemplate<String, Message> journalMessageKafkaTemplate(
            ProducerFactory<String, Message> journalMessageKafkaProducerFactory) {
        log.info("Create KafkaTemplate<String, Message> bean");
        return new KafkaTemplate<>(journalMessageKafkaProducerFactory);
    }

    @Bean(name = "journalMessageKafkaProducerFactory")
    @ConditionalOnMissingBean(name = "journalMessageKafkaProducerFactory")
    @ConditionalOnProperty("spring.kafka.topics.journal-request")
    public ProducerFactory<String, Message> journalMessageKafkaProducerFactory() {
        log.info("Create ProducerFactory<String, Message> bean");
        return createKafkaProducerFactory();
    }

    private <V> ProducerFactory<String, V> createKafkaProducerFactory() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties());
        props.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, enableIdempotence);
        props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, maxRequestSize);
        return new DefaultKafkaProducerFactory<>(props);
    }
}
