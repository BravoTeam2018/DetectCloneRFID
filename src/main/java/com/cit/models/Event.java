package com.cit.models;

import javax.persistence.*;


@Entity
@Table(name = "events")
@lombok.Data
@lombok.Builder
public class Event {
    @Id
    private long timestamp;
    private String cardId;
    private String panelId;
    @Embedded
    private Location location;
    private boolean accessAllowed;
}

