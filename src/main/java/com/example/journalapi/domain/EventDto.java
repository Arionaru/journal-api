package com.example.journalapi.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class EventDto {
    private String source;
    private String sourceId;
    private String action;
    private String status;
    private Map<String, Object> description;
    private LocalDateTime eventTime;
}
