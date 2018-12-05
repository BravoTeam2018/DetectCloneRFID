package com.cit.controllers;

import com.cit.UnitTests;
import com.cit.config.ServicesConfig;
import com.cit.exceptions.CardIdException;
import com.cit.exceptions.PanelIdException;
import com.cit.exceptions.PanelNotFoundException;
import com.cit.models.Event;
import com.cit.models.GPSCoordinate;
import com.cit.models.Location;
import com.cit.services.eventstore.EventStoreService;
import com.cit.services.locator.LocatorService;
import com.cit.services.notification.NotifierService;
import com.cit.services.validation.ValidationService;
import com.cit.services.validation.ValidationRulesResult;
import com.cit.transfer.ValidationServiceRestResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Rule;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import static com.cit.Helper.getAllLocations;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Category(UnitTests.class)
@ContextConfiguration(classes = {ServicesConfig.class})
@SpringBootTest
@RunWith(SpringRunner.class)
@WebAppConfiguration
class ValidationControllerTest {


    @Autowired
    ValidationController validationController;


    MockMvc mockMvc;

    @Autowired
    WebApplicationContext wac;

    @Mock
    LocatorService locatorService;

    @Mock
    EventStoreService eventStoreService;

    @Mock
    ValidationService validationService;

    @Mock
    NotifierService notifierService;

    Map<UUID, Location> allLocations;

    Event eventCITLibraryWest_1;
    Event eventCITLibraryWest_2;
    Event eventCITLibraryWest_1_BadPanelId;
    Event eventCITLibraryWest_1_BadCardId;

    Location locationCITLib;

    String cardIdUnderTest = "580ddc98-0db9-473d-a721-348f353f1d2b";

    String panelId_CITLibraryWest_1 = "580ddc98-0db9-473d-a721-348f353f1d2b";
    String panelId_CITLibraryWest_2 = "580ddc98-0db9-473d-a721-368f353f1d2b";
    String panelId_NOT_IN_SYSTEM = "580ddc98-0db9-473d-a721-368f353f1d2b0";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.initMocks(this);


        GPSCoordinate coord = GPSCoordinate.builder()
                .latitude(51.884827)
                .longitude(-8.533947)
                .build();

        locationCITLib = Location.builder()
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
                .panelId(panelId_CITLibraryWest_1)
                .cardId(cardIdUnderTest)
                .location(locationCITLib)
                .accessAllowed(true)
                .timestamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis())
                .build();

        eventCITLibraryWest_1_BadPanelId = Event.builder()
                .panelId("XXXXXXXXXXXXX")
                .cardId(cardIdUnderTest)
                .location(locationCITLib)
                .accessAllowed(true)
                .timestamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis())
                .build();

        eventCITLibraryWest_1_BadCardId = Event.builder()
                .panelId(panelId_CITLibraryWest_1)
                .cardId("XXXXXXXXXXXXX")
                .location(locationCITLib)
                .accessAllowed(true)
                .timestamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis())
                .build();


        allLocations = getAllLocations();

        validationController = new ValidationController(locatorService,eventStoreService,validationService, notifierService);

    }
    @AfterEach
    void tearDown() {
    }


    @Test
    void testCardWithPreviousEventsInSystemAndNewScanAtSameLocation() throws Exception {

        // ** GIVEN **
        
        this.mockMvc = standaloneSetup(this.validationController).build();// Standalone context

        // default to possible result
        ValidationRulesResult validationRulesResult = ValidationRulesResult.builder()
                .reason("Possible time-distance event.")
                .validEvent(true)
                .build();

        //
        // Build the response DTO
        //
        ValidationServiceRestResponseDTO rc = ValidationServiceRestResponseDTO.builder()
                .currentEvent(eventCITLibraryWest_1)
                .previousEvent(eventCITLibraryWest_2)
                .reason(validationRulesResult.getReason())
                .validEvent(validationRulesResult.isValidEvent())
                .build();

        // ** WHEN **
        
        // Mocking services
        doNothing().when(notifierService).publish(any(String.class));
        when(locatorService.getLocationFromPanelId(any(String.class))).thenReturn(locationCITLib);
        when( eventStoreService.getLastEventForCardId(any(String.class))).thenReturn(eventCITLibraryWest_2);
        doNothing().when( eventStoreService ).storeEvent( isA(Event.class) );
        when(validationService.performEventValidation(any(Event.class),any(Event.class))).thenReturn(rc);


        // ** THEN **
        
        ObjectMapper objectMapper = new ObjectMapper();
        String requestURI = String.format("/api/panels/request?panelId=%s&cardId=%s&allowed=true",eventCITLibraryWest_1.getPanelId(),eventCITLibraryWest_1.getCardId());
        mockMvc.perform(get(requestURI).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reason", is(validationRulesResult.getReason())))
                
                .andExpect(jsonPath("$.currentEvent.cardId", is(cardIdUnderTest)))
                .andExpect(jsonPath("$.currentEvent.accessAllowed", is(true)))
                .andExpect(jsonPath("$.currentEvent.location.altitude", is(eventCITLibraryWest_1.getLocation().getAltitude())))
                .andExpect(jsonPath("$.currentEvent.location.coordinates.latitude", is(eventCITLibraryWest_1.getLocation().getCoordinates().getLatitude())))
                .andExpect(jsonPath("$.currentEvent.location.coordinates.longitude", is(eventCITLibraryWest_1.getLocation().getCoordinates().getLongitude())))
                .andExpect(jsonPath("$.currentEvent.location.relativeLocation", is(eventCITLibraryWest_1.getLocation().getRelativeLocation())))
                .andExpect(jsonPath("$.currentEvent.panelId", is(eventCITLibraryWest_1.getPanelId())))
                .andExpect(jsonPath("$.currentEvent.timestamp", is(eventCITLibraryWest_1.getTimestamp())))

                .andExpect(jsonPath("$.previousEvent.cardId", is(cardIdUnderTest)))
                .andExpect(jsonPath("$.previousEvent.accessAllowed", is(true)))
                .andExpect(jsonPath("$.previousEvent.location.altitude", is(eventCITLibraryWest_2.getLocation().getAltitude())))
                .andExpect(jsonPath("$.previousEvent.location.coordinates.latitude", is(eventCITLibraryWest_2.getLocation().getCoordinates().getLatitude())))
                .andExpect(jsonPath("$.previousEvent.location.coordinates.longitude", is(eventCITLibraryWest_2.getLocation().getCoordinates().getLongitude())))
                .andExpect(jsonPath("$.previousEvent.location.relativeLocation", is(eventCITLibraryWest_2.getLocation().getRelativeLocation())))
                .andExpect(jsonPath("$.previousEvent.panelId", is(eventCITLibraryWest_2.getPanelId())))
                .andExpect(jsonPath("$.previousEvent.timestamp", is(eventCITLibraryWest_2.getTimestamp())))

                
                .andExpect(jsonPath("$.validEvent", is(true)));

    }

    @Test
    void testCardWithPreviousEventsInSystemAndNewScanAtDifferentLocationShortlyAfterFirstEvent() throws Exception {

        // ** GIVEN **

        this.mockMvc = standaloneSetup(this.validationController).build();// Standalone context

        // default to possible result
        ValidationRulesResult validationRulesResult = ValidationRulesResult.builder()
                .reason("Impossible time-distance event.")
                .validEvent(false)
                .build();

        //
        // Build the response DTO
        //
        ValidationServiceRestResponseDTO rc = ValidationServiceRestResponseDTO.builder()
                .currentEvent(eventCITLibraryWest_1)
                .previousEvent(eventCITLibraryWest_2)
                .reason(validationRulesResult.getReason())
                .validEvent(validationRulesResult.isValidEvent())
                .build();

        // ** WHEN **

        // Mocking services
        doNothing().when(notifierService).publish(any(String.class));
        when(locatorService.getLocationFromPanelId(any(String.class))).thenReturn(locationCITLib);
        when( eventStoreService.getLastEventForCardId(any(String.class))).thenReturn(eventCITLibraryWest_2);
        doNothing().when( eventStoreService ).storeEvent( isA(Event.class) );
        when(validationService.performEventValidation(any(Event.class),any(Event.class))).thenReturn(rc);


        // ** THEN **

        ObjectMapper objectMapper = new ObjectMapper();
        String requestURI = String.format("/api/panels/request?panelId=%s&cardId=%s&allowed=true",eventCITLibraryWest_1.getPanelId(),eventCITLibraryWest_1.getCardId());
        mockMvc.perform(get(requestURI).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reason", is(validationRulesResult.getReason())))
                .andExpect(jsonPath("$.currentEvent.cardId", is(cardIdUnderTest)))
                .andExpect(jsonPath("$.currentEvent.accessAllowed", is(true)))
                .andExpect(jsonPath("$.currentEvent.location.altitude", is(eventCITLibraryWest_1.getLocation().getAltitude())))
                .andExpect(jsonPath("$.currentEvent.location.coordinates.latitude", is(eventCITLibraryWest_1.getLocation().getCoordinates().getLatitude())))
                .andExpect(jsonPath("$.currentEvent.location.coordinates.longitude", is(eventCITLibraryWest_1.getLocation().getCoordinates().getLongitude())))
                .andExpect(jsonPath("$.currentEvent.location.relativeLocation", is(eventCITLibraryWest_1.getLocation().getRelativeLocation())))
                .andExpect(jsonPath("$.currentEvent.panelId", is(eventCITLibraryWest_1.getPanelId())))
                .andExpect(jsonPath("$.currentEvent.timestamp", is(eventCITLibraryWest_1.getTimestamp())))
                .andExpect(jsonPath("$.previousEvent.cardId", is(cardIdUnderTest)))
                .andExpect(jsonPath("$.previousEvent.accessAllowed", is(true)))
                .andExpect(jsonPath("$.previousEvent.location.altitude", is(eventCITLibraryWest_1.getLocation().getAltitude())))
                .andExpect(jsonPath("$.previousEvent.location.coordinates.latitude", is(eventCITLibraryWest_1.getLocation().getCoordinates().getLatitude())))
                .andExpect(jsonPath("$.previousEvent.location.coordinates.longitude", is(eventCITLibraryWest_1.getLocation().getCoordinates().getLongitude())))
                .andExpect(jsonPath("$.previousEvent.location.relativeLocation", is(eventCITLibraryWest_1.getLocation().getRelativeLocation())))
                .andExpect(jsonPath("$.previousEvent.panelId", is(eventCITLibraryWest_1.getPanelId())))
                .andExpect(jsonPath("$.previousEvent.timestamp", is(eventCITLibraryWest_1.getTimestamp())))
                .andExpect(jsonPath("$.validEvent", is(false)));
    }



    @Test
    void testCardWithNoPreviousEventsInSystem() throws Exception {

        // ** GIVEN **

        this.mockMvc = standaloneSetup(this.validationController).build();// Standalone context

        // default to possible result
        ValidationRulesResult validationRulesResult = ValidationRulesResult.builder()
                .reason("Possible time-distance event.")
                .validEvent(true)
                .build();

        //
        // Build the response DTO
        //
        ValidationServiceRestResponseDTO rc = ValidationServiceRestResponseDTO.builder()
                .currentEvent(eventCITLibraryWest_1)
                .previousEvent(null)
                .reason(validationRulesResult.getReason())
                .validEvent(validationRulesResult.isValidEvent())
                .build();

        // ** WHEN **

        // Mocking services
        doNothing().when(notifierService).publish(any(String.class));
        when(locatorService.getLocationFromPanelId(any(String.class))).thenReturn(locationCITLib);
        when( eventStoreService.getLastEventForCardId(any(String.class))).thenReturn(eventCITLibraryWest_2);
        doNothing().when( eventStoreService ).storeEvent( isA(Event.class) );
        when(validationService.performEventValidation(any(Event.class),any(Event.class))).thenReturn(rc);


        // ** THEN **

        ObjectMapper objectMapper = new ObjectMapper();
        String requestURI = String.format("/api/panels/request?panelId=%s&cardId=%s&allowed=true",eventCITLibraryWest_1.getPanelId(),eventCITLibraryWest_1.getCardId());
        mockMvc.perform(get(requestURI).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reason", is(validationRulesResult.getReason())))
                .andExpect(jsonPath("$.currentEvent.cardId", is(cardIdUnderTest)))
                .andExpect(jsonPath("$.currentEvent.accessAllowed", is(true)))
                .andExpect(jsonPath("$.currentEvent.location.altitude", is(eventCITLibraryWest_1.getLocation().getAltitude())))
                .andExpect(jsonPath("$.currentEvent.location.coordinates.latitude", is(eventCITLibraryWest_1.getLocation().getCoordinates().getLatitude())))
                .andExpect(jsonPath("$.currentEvent.location.coordinates.longitude", is(eventCITLibraryWest_1.getLocation().getCoordinates().getLongitude())))
                .andExpect(jsonPath("$.currentEvent.location.relativeLocation", is(eventCITLibraryWest_1.getLocation().getRelativeLocation())))
                .andExpect(jsonPath("$.currentEvent.panelId", is(eventCITLibraryWest_1.getPanelId())))
                .andExpect(jsonPath("$.currentEvent.timestamp", is(eventCITLibraryWest_1.getTimestamp())))
                .andExpect(jsonPath("$.previousEvent", is(nullValue())))
                .andExpect(jsonPath("$.validEvent", is(true)));

    }


    @Test
    void testCardScanWhenPanelDoesNotExistInSystem() throws Exception {

        // ** GIVEN **

        this.mockMvc = standaloneSetup(this.validationController).build();// Standalone context

        // default to possible result
        ValidationRulesResult validationRulesResult = ValidationRulesResult.builder()
                .reason("Possible time-distance event.")
                .validEvent(true)
                .build();

        //
        // Build the response DTO
        //
        ValidationServiceRestResponseDTO rc = ValidationServiceRestResponseDTO.builder()
                .currentEvent(eventCITLibraryWest_1)
                .previousEvent(null)
                .reason(validationRulesResult.getReason())
                .validEvent(validationRulesResult.isValidEvent())
                .build();

        // ** WHEN **

        // Mocking services
        doNothing().when(notifierService).publish(any(String.class));
        when(locatorService.getLocationFromPanelId(any(String.class))).thenThrow(new PanelNotFoundException("Panel not found Id=" + panelId_NOT_IN_SYSTEM));
        when( eventStoreService.getLastEventForCardId(any(String.class))).thenReturn(null);
        doNothing().when( eventStoreService ).storeEvent( isA(Event.class) );
        when(validationService.performEventValidation(any(Event.class),any(Event.class))).thenReturn(rc);
        exception.expect((PanelIdException.class));


        // ** THEN **

        ObjectMapper objectMapper = new ObjectMapper();
        String requestURI = String.format("/api/panels/request?panelId=%s&cardId=%s&allowed=true",panelId_NOT_IN_SYSTEM,eventCITLibraryWest_1.getCardId());
        mockMvc.perform(get(requestURI).contentType(MediaType.APPLICATION_JSON))
        .andExpect( status().is4xxClientError());
    }

    @Test
    void testCardScanWhenPanelIsNotProperUUIDFormat() throws Exception {

        // ** GIVEN **

        this.mockMvc = standaloneSetup(this.validationController).build();// Standalone context

        // default to possible result
        ValidationRulesResult validationRulesResult = ValidationRulesResult.builder()
                .reason("Possible time-distance event.")
                .validEvent(true)
                .build();

        //
        // Build the response DTO
        //
        ValidationServiceRestResponseDTO rc = ValidationServiceRestResponseDTO.builder()
                .currentEvent(eventCITLibraryWest_1_BadPanelId)
                .previousEvent(null)
                .reason(validationRulesResult.getReason())
                .validEvent(validationRulesResult.isValidEvent())
                .build();

        // ** WHEN **

        // Mocking services
        doNothing().when(notifierService).publish(any(String.class));
        when(locatorService.getLocationFromPanelId(any(String.class))).thenThrow(new PanelIdException("Bad format panelId=\"+panelId=" + eventCITLibraryWest_1_BadPanelId.getPanelId()));
        when( eventStoreService.getLastEventForCardId(any(String.class))).thenReturn(null);
        doNothing().when( eventStoreService ).storeEvent( isA(Event.class) );
        when(validationService.performEventValidation(any(Event.class),any(Event.class))).thenReturn(rc);
        exception.expect((PanelIdException.class));


        // ** THEN **
        ObjectMapper objectMapper = new ObjectMapper();
        String requestURI = String.format("/api/panels/request?panelId=%s&cardId=%s&allowed=true",eventCITLibraryWest_1_BadPanelId.getPanelId(),eventCITLibraryWest_1_BadPanelId.getCardId());
        mockMvc.perform(get(requestURI).contentType(MediaType.APPLICATION_JSON))
                .andExpect( status().is4xxClientError());
    }

    @Test
    void testCardScanWhenCardIdIsNotProperUUIDFormat() throws Exception {

        // ** GIVEN **

        this.mockMvc = standaloneSetup(this.validationController).build();// Standalone context

        // default to possible result
        ValidationRulesResult validationRulesResult = ValidationRulesResult.builder()
                .reason("Possible time-distance event.")
                .validEvent(true)
                .build();

        //
        // Build the response DTO
        //
        ValidationServiceRestResponseDTO rc = ValidationServiceRestResponseDTO.builder()
                .currentEvent(eventCITLibraryWest_1_BadCardId)
                .previousEvent(null)
                .reason(validationRulesResult.getReason())
                .validEvent(validationRulesResult.isValidEvent())
                .build();

        // ** WHEN **


        // Mocking services
        doNothing().when(notifierService).publish(any(String.class));
        when(locatorService.getLocationFromPanelId(any(String.class))).thenThrow(new CardIdException("Bad format panelId=\"+panelId=" + eventCITLibraryWest_1_BadCardId.getPanelId()));
        when( eventStoreService.getLastEventForCardId(any(String.class))).thenReturn(null);
        doNothing().when( eventStoreService ).storeEvent( isA(Event.class) );
        when(validationService.performEventValidation(any(Event.class),any(Event.class))).thenReturn(rc);
        exception.expect((PanelIdException.class));


        // ** THEN **
        ObjectMapper objectMapper = new ObjectMapper();
        String requestURI = String.format("/api/panels/request?panelId=%s&cardId=%s&allowed=true",eventCITLibraryWest_1_BadCardId.getPanelId(),eventCITLibraryWest_1_BadCardId.getCardId());
        mockMvc.perform(get(requestURI).contentType(MediaType.APPLICATION_JSON))
                .andExpect( status().is4xxClientError());
    }


}

