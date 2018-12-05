package com.cit.repositories;


import com.cit.models.Event;
import com.cit.models.GPSCoordinate;
import com.cit.models.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class EventRepositoryTest {

    @Mock
    private EventRepository eventRepository;

    private Event returnEvent;

    @BeforeEach
    void setUp() {

        GPSCoordinate coord = GPSCoordinate.builder()
                .latitude(51.884827)
                .longitude(-8.533947)
                .build();

        Location location = Location.builder()
                .coordinates(coord)
                .altitude(100)
                .relativeLocation("CIT Library West Wing Entry Doors, Cork, Ireland")
                .build();

        returnEvent = Event.builder()
                .panelId("580ddc98-0db9-473d-a721-348f353f1d2b")
                .cardId("580ddc98-0db9-473d-a721-348f353f1d2b")
                .location(location)
                .accessAllowed(true)
                .timestamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis())
                .build();

        eventRepository = mock(EventRepository.class);

    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void findDistinctFirstByCardIdAndTimestamp() {

        when(eventRepository.findDistinctFirstByCardId(anyString())).thenReturn(returnEvent);

        Event evnt = eventRepository.findDistinctFirstByCardId("580ddc98-0db9-473d-a721-348f353f1d2b");

        System.out.println(evnt.toString());

        assertEquals("580ddc98-0db9-473d-a721-348f353f1d2b", evnt.getCardId());
    }

    @Test
    void findAllByCardId() {
        when(eventRepository.findAllByCardId(anyString())).thenReturn( Arrays.asList( returnEvent, returnEvent ) );

        List<Event> events = eventRepository.findAllByCardId("580ddc98-0db9-473d-a721-348f353f1d2b");

        System.out.println(events.toString());

        for (Event event : events) {
            assertEquals("580ddc98-0db9-473d-a721-348f353f1d2b", event.getCardId());
        }
    }

}