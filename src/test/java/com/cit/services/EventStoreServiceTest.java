package com.cit.services;

import com.cit.DetectCloneRFIDApplication;
import com.cit.UnitTests;
import com.cit.config.ServicesConfig;
import com.cit.models.Event;
import com.cit.models.GPSCoordinate;
import com.cit.models.Location;
import com.cit.services.eventstore.EventStoreService;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;


@Category(UnitTests.class)
@ContextConfiguration(classes = {ServicesConfig.class, DetectCloneRFIDApplication.class})
@TestPropertySource
class EventStoreServiceTest {

    private EventStoreService eventStoreService;

    private Event eventCITLibraryWest_1;
    private Event eventCITLibraryWest_2;

    String cardIdUnderTest = "580ddc98-0db9-473d-a721-348f353f1d2b";

    String panelId_CITLibraryWest_1 = "580ddc98-0db9-473d-a721-348f353f1d2b";
    String panelId_CITLibraryWest_2 = "580ddc98-0db9-473d-a721-368f353f1d2b";

    @BeforeEach
    void setUp() {

        // setup test data

        GPSCoordinate coord = GPSCoordinate.builder()
                .latitude(51.884827)
                .longitude(-8.533947)
                .build();

        Location locationCITLib = Location.builder()
                .coordinates(coord)
                .altitude(100)
                .relativeLocation("CIT Library West Wing Entry Doors, Cork, Ireland")
                .build();

        eventCITLibraryWest_1 = Event.builder()
                .panelId(panelId_CITLibraryWest_1)
                .cardId(cardIdUnderTest)
                .location(locationCITLib)
                .accessAllowed(true)
                .timestamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis())
                .build();

        eventCITLibraryWest_2 = Event.builder()
                .panelId(panelId_CITLibraryWest_2)
                .cardId(cardIdUnderTest)
                .location(locationCITLib)
                .accessAllowed(true)
                .timestamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis())
                .build();

        eventStoreService = new EventStoreService();

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void cacheIsEmptyShouldNotFindEvent() {

        Event event = eventStoreService.getLastEventForCardId(cardIdUnderTest);
        assertNull(event);
    }

    @Test
    void cacheCanGetCardEventFromCache() {

        eventStoreService.storeEvent(eventCITLibraryWest_1);

        Event event = eventStoreService.getLastEventForCardId(cardIdUnderTest);

        assertNotNull(event);

        assertEquals(event.getPanelId(),panelId_CITLibraryWest_1);
    }

    @Test
    void cacheCanGetCardEventFromCacheAndReplaceItwithNewEvent() {

        // first event
        eventStoreService.storeEvent(eventCITLibraryWest_1);

        Event event = eventStoreService.getLastEventForCardId(cardIdUnderTest);


        assertNotNull(event);
        assertEquals(event.getPanelId(),panelId_CITLibraryWest_1);

        // second Event

        // first event
        eventStoreService.storeEvent(eventCITLibraryWest_2);

        Event event2 = eventStoreService.getLastEventForCardId(cardIdUnderTest);


        assertNotNull(event2);
        assertEquals(event2.getPanelId(),panelId_CITLibraryWest_2);

    }

    @Test
    void exceptionTestingWhenStoreEventParameterIsNull() {
        Event nullEvent = null;

        assertThrows(IllegalArgumentException.class,
                ()->{
                    eventStoreService.storeEvent(nullEvent);
                });

    }

}