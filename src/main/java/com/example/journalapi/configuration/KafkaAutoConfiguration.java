package com.example.journalapi.configuration;

import com.example.journalapi.service.KafkaMessageSender;
import com.example.journalapi.service.impl.KafkaMessageSenderImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

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

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    @Bean(name = "journalMessageSender")
    @ConditionalOnProperty("spring.kafka.topics.journal-request")
    @ConditionalOnMissingBean(name = "journalMessageSender")
    public KafkaMessageSender journalMessageSender(
            KafkaTemplate<String, Object> journalMessageKafkaTemplate,
            @Value("${spring.kafka.topics.journal-request}") String journalKafkaTopic) {
        log.info("Create KafkaIntegrationJournalMessageSender bean of IntegrationJournalMessageSender");
        return new KafkaMessageSenderImpl(journalMessageKafkaTemplate, journalKafkaTopic);
    }

    @Bean(name = "journalKafkaTemplate")
    @ConditionalOnMissingBean(name = "journalMessageKafkaTemplate")
    @ConditionalOnProperty("spring.kafka.topics.journal-request")
    public KafkaTemplate<String, Object> journalMessageKafkaTemplate(
            ProducerFactory<String, Object> journalMessageKafkaProducerFactory) {
        log.info("Create KafkaTemplate<String, Message> bean");
        return new KafkaTemplate<>(journalMessageKafkaProducerFactory);
    }

    @Bean(name = "journalMessageKafkaProducerFactory")
    @ConditionalOnMissingBean(name = "journalMessageKafkaProducerFactory")
    @ConditionalOnProperty("spring.kafka.topics.journal-request")
    public ProducerFactory<String, Object> journalMessageKafkaProducerFactory() {
        log.info("Create ProducerFactory<String, Message> bean");
        return createKafkaProducerFactory();
    }

    private <V> ProducerFactory<String, V> createKafkaProducerFactory() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties());
        props.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, enableIdempotence);
        props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, maxRequestSize);
        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), new JsonSerializer<>(objectMapper()));
    }
}
