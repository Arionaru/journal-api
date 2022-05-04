package com.example.journalapi.service.impl;

import com.example.journalapi.service.KafkaMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

@RequiredArgsConstructor
public class KafkaMessageSenderImpl implements KafkaMessageSender {
    private final KafkaTemplate<String, Object> integrationJournalMessageKafkaTemplate;
    private final String topic;

    @Override
    public ListenableFuture<SendResult<String, Object>> send(Object message, String requestId) {
        return integrationJournalMessageKafkaTemplate.send(topic, requestId, message);
    }
}
