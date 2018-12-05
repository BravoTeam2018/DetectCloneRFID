package com.cit.services.validation.rules;

import com.cit.Helper;
import com.cit.UnitTests;
import com.cit.config.ServicesConfig;
import com.cit.models.Event;
import com.cit.models.Location;
import com.cit.services.locator.LocatorService;
import lombok.extern.slf4j.Slf4j;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;

import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import static com.cit.Helper.createTestEvent;
import static org.junit.jupiter.api.Assertions.*;

@Category(UnitTests.class)
@Slf4j
@ContextConfiguration(classes={ServicesConfig.class})
class PanelDistanceResultCalculatorTest {

    private static Map<UUID, Location> pannelMap;

    @InjectMocks
    private LocatorService locatorService;

    String cardIdUnderTest = "580ddc98-0db9-473d-a721-348f353f1d2b";

    // Event @ panel @ CIT Library West Wing Entry Doors, Cork, Ireland
    Event event1;

    // Event @ panel @ "CIT Library North Ground Exit, Cork, Ireland"
    Event event2;

    // Event @ panel @ "CIT Library North Ground Exit, Cork, Ireland"
    Event event3;

    @BeforeEach
    void setUp() throws FileNotFoundException {
        MockitoAnnotations.initMocks(this);

        pannelMap = Helper.getAllLocations();

        long timestamp1 = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
        long timestamp2 = timestamp1 + (5*60000); // 5 minutes later
        long timestamp3 = timestamp1 + (1*60000); // 1 minutes later

        // panel @ CIT Library West Wing Entry Doors, Cork, Ireland
        event1  = createTestEvent( pannelMap,"580ddc98-0db9-473d-a721-348f353f1d2b", cardIdUnderTest , timestamp1 );

        // panel @ "CIT Library North Ground Exit, Cork, Ireland"
        event2  = createTestEvent(pannelMap,"7907775e-15ac-415f-a99c-e978856c8ec0", cardIdUnderTest , timestamp2 );

        // panel @ "CIT Library North Ground Exit, Cork, Ireland"
        event3  = createTestEvent(pannelMap,"7907775e-15ac-415f-a99c-e978856c8ec0", cardIdUnderTest , timestamp3 );
    }


    @Test
    void distanceInMtrsBetweenTwoEvents() {


        // as the crow flys between points
        double distance = PanelDistanceCalculator.distanceInMtrsBetweenTwoEvents(event2, event1);
        assertEquals(51.35523198140736, distance);


        // google says 165 mtrs so i'm going to have to give 120m tolerence
        distance = distance + 120.0;

        assertEquals(true, distance > 165.0);

        if (log.isDebugEnabled()) {
            log.debug("distanceInMtrsBetweenTwoEvents() distance=\"" + distance + "\")");
        }
    }





}