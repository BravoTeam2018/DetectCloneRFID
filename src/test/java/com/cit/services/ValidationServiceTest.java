package com.cit.services;

import com.cit.UnitTests;
import com.cit.config.ServicesConfig;
import com.cit.models.Event;
import com.cit.models.GPSCoordinate;
import com.cit.models.Location;
import com.cit.services.validation.rules.EventValidationRuleBook;
import com.cit.services.distance.GoogleDistanceService;
import com.cit.services.validation.ValidationService;
import com.cit.services.validation.ValidationRulesResult;
import com.cit.transfer.ValidationServiceRestResponseDTO;
import com.deliveredtechnologies.rulebook.lang.RuleBookBuilder;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@Category(UnitTests.class)
@ContextConfiguration(classes = {ServicesConfig.class})
class ValidationServiceTest {

    //TODO Load in within application properties
    String apikey="AIzaSyBkzAoC6KtzmNl0ZSIyLZEq8Vr8jhRkItI";

    @Autowired
    private ValidationService validationService;

    RestTemplate restTemplate =  new RestTemplate();

    private MockRestServiceServer mockServer;


    @InjectMocks
    GoogleDistanceService googleDistanceService;

    private Event eventCITLibraryWest_1;
    private Event eventCITLibraryWest_2;

    String cardIdUnderTest = "580ddc98-0db9-473d-a721-348f353f1d2b";

    String panelId_CITLibraryWest_1 = "580ddc98-0db9-473d-a721-348f353f1d2b";
    String panelId_CITLibraryWest_2 = "580ddc98-0db9-473d-a721-368f353f1d2b";

    @BeforeEach
    void setUp() {

        // setup test data
        MockitoAnnotations.initMocks(this);

        RuleBook ruleBook = RuleBookBuilder.create(EventValidationRuleBook.class).withResultType(ValidationRulesResult.class)
                .withDefaultResult(ValidationRulesResult.builder().reason("Possible time-distance event").validEvent(true).build())
                .build();

        googleDistanceService =  new GoogleDistanceService(restTemplate,apikey);

        validationService = new ValidationService(ruleBook, googleDistanceService);

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

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void validationResponseShouldNotBeNullWhenValidEventsPassedIn() {
        assertNotNull(validationService);



        ValidationServiceRestResponseDTO validationServiceRestResponseDTO = validationService.performEventValidation(eventCITLibraryWest_1, eventCITLibraryWest_2);
        assertNotNull(validationServiceRestResponseDTO);
    }


}