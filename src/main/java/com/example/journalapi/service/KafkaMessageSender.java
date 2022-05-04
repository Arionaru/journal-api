package com.example.journalapi.service;

import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

public interface KafkaMessageSender {
    ListenableFuture<SendResult<String, Object>> send(Object message, String requestId);
}
