package com.example.journalapi.domain;

import java.time.LocalDateTime;
import java.util.Map;

public class EventDto {
    private Source source;
    private String sourceId;
    private String action;
    private Status status;
    private String result;
    private Map<String, Object> description;
    private LocalDateTime eventTime;
}
