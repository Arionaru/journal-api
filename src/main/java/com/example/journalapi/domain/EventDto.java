package com.example.journalapi.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    private String source;
    private String sourceId;
    private String action;
    private String status;
    private Map<String, Object> description;
    private LocalDateTime eventTime;
}
