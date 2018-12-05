package com.cit.transfer;

import com.cit.models.Event;

@lombok.Data
@lombok.Builder
public class ValidationServiceMQTTResponseDTO {
    private String severity;
    private String title;
    private String description;
    private Event currentEvent;
    private Event previousEvent;
}
