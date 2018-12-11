package com.cit.services.validation.rules;

import com.cit.Helper;
import com.cit.config.ServicesConfig;
import com.cit.models.Event;
import com.cit.models.Location;
import com.cit.services.distance.*;
import com.cit.services.locator.LocatorService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestGatewaySupport;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static com.cit.Helper.STANFORD_DEPARTMENT_OF_ECONOMICS_1ST_FLOOR_WEST_WING_CA_USA;
import static com.cit.Helper.STANFORD_DEPARTMENT_OF_ECONOMICS_2ND_FLOOR_WEST_WING_CA_USA;
import static com.cit.Helper.STANFORD_DEPARTMENT_OF_ECONOMICS_SERRA_MALL_WEST_WING_CA_USA;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@Slf4j
@ContextConfiguration(classes = {ServicesConfig.class})
public class EventValidationBeanTest {

    private static Map<UUID, Location> pannelMap;

    String apikey="GOOGLEKEY";
    String uri = "http://localhost:8080/api/locations";

    RestTemplate restTemplate = new RestTemplate();

    private LocatorService locatorService;
    private GoogleDistanceService googleDistanceService;
    private LocalDistanceService localDistanceService;
    private FlyAndDriveDistanceService flyAndDriveDistanceService;
    private DistanceFacadeService distanceFacadeService;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        RestGatewaySupport gateway = new RestGatewaySupport();
        gateway.setRestTemplate(restTemplate);

        mockServer = MockRestServiceServer.createServer(gateway);
        locatorService = new LocatorService(restTemplate,uri) ;
        googleDistanceService = new GoogleDistanceService(restTemplate,apikey);
        localDistanceService = new LocalDistanceService();
        flyAndDriveDistanceService = new FlyAndDriveDistanceService();
        distanceFacadeService = new DistanceFacadeService(googleDistanceService,localDistanceService, flyAndDriveDistanceService);


        pannelMap = Helper.getAllLocations();
    }


    @Test
    void testGetTravelDistanceInMtrs() throws IOException {
        Event previousEvent;
        Event currentEvent;



        previousEvent = Helper.createTestEvent( pannelMap,"7907775e-15ac-415f-a99c-e978856c8ec0", "7907775e-15ac-415f-a99c-e978856c8ec0", 1541349919142l);
        currentEvent = Helper.createTestEvent( pannelMap,"580ddc98-0db9-473d-a721-348f353f1d2b", "580ddc98-0db9-473d-a721-348f353f1d2b", 1541349936961l);


        mockServer.expect(manyTimes(), requestTo(uri))
                .andRespond(withSuccess(Helper.getGoogleJson("mockData/panels.json"), MediaType.APPLICATION_JSON));

        mockServer.expect(manyTimes(), requestTo(googleDistanceService.getRequestURL(currentEvent.getLocation(), previousEvent.getLocation(), GoogleDistanceService.Mode.WALKING)))
                .andRespond(withSuccess(Helper.getGoogleJson("mockData/google.json"), MediaType.APPLICATION_JSON));

        EventValidationBean eventValidationBean = new EventValidationBean(currentEvent, previousEvent, distanceFacadeService);

        boolean result = eventValidationBean.isItPossibleToWalkBetweenCurrentAndPreviousEvent();

        assertEquals(false, result);

        assertEquals(51, (int)eventValidationBean.getTravelDistanceInMtrs());


    }

    @Test
    void isThePanelsTheSameforCurrentAndPreviousEvents() throws IOException {

        Event previousEvent;
        Event currentEvent;

        long timestamp1 = 1541349919142l;
        long timestamp2 = timestamp1 + (60000); // 1 minutes later

        previousEvent = Helper.createTestEvent( pannelMap,"7907775e-15ac-415f-a99c-e978856c8ec0", "7907775e-15ac-415f-a99c-e978856c8ec0", timestamp1);
        currentEvent = Helper.createTestEvent( pannelMap,"7907775e-15ac-415f-a99c-e978856c8ec0", "7907775e-15ac-415f-a99c-e978856c8ec0", timestamp2);

        mockServer.expect(manyTimes(), requestTo(uri))
                .andRespond(withSuccess(Helper.getGoogleJson("mockData/panels.json"), MediaType.APPLICATION_JSON));

        mockServer.expect(manyTimes(), requestTo(googleDistanceService.getRequestURL(currentEvent.getLocation(), previousEvent.getLocation(), GoogleDistanceService.Mode.WALKING)))
                .andRespond(withSuccess(Helper.getGoogleJson("mockData/google.json"), MediaType.APPLICATION_JSON));

        EventValidationBean eventValidationBean = new EventValidationBean(currentEvent, previousEvent, distanceFacadeService);

        boolean result = eventValidationBean.isItPossibleToWalkBetweenCurrentAndPreviousEvent();

        assertEquals(true, result);



    }


    @Test
    void isItPossibleToWalkBetweenCurrentAndPreviousEvent_BugBroughtBackInfinityDistance() throws IOException {

/*
        logs which produced the bug

        ""2018-11-04 16:45:36 - Validation request  - getValidation(panelId="580ddc98-0db9-473d-a721-348f353f1d2b", cardId="580ddc98-0db9-473d-a721-348f353f1d2b", allowed="true")
            ""2018-11-04 16:45:36 - EventStoreService.getLastEventForCardId("cardId=" 580ddc98-0db9-473d-a721-348f353f1d2b
            ""2018-11-04 16:45:36 - Found previous event for performEventValidation("cardId=" 580ddc98-0db9-473d-a721-348f353f1d2b", Event= Event(timestamp=1541349919142, cardId=580ddc98-0db9-473d-a721-348f353f1d2b, panelId=7907775e-15ac-415f-a99c-e978856c8ec0, location=Location(coordinates=GPSCoordinate(latitude=51.884969, longitude=-8.533235), altitude=100.0, relativeLocation=CIT Library North Ground Exit, Cork, Ireland), accessAllowed=true)
            EventStoreService.storeEvent("Event=" Event(timestamp=1541349936961, cardId=580ddc98-0db9-473d-a721-348f353f1d2b, panelId=580ddc98-0db9-473d-a721-348f353f1d2b, location=Location(coordinates=GPSCoordinate(latitude=51.884827, longitude=-8.533947), altitude=100.0, relativeLocation=CIT Library West Wing Entry Doors, Cork, Ireland), accessAllowed=true)
*/

        Event previousEvent;
        Event currentEvent;

        previousEvent = Helper.createTestEvent( pannelMap,"7907775e-15ac-415f-a99c-e978856c8ec0", "7907775e-15ac-415f-a99c-e978856c8ec0", 1541349919142l);
        currentEvent = Helper.createTestEvent( pannelMap,"580ddc98-0db9-473d-a721-348f353f1d2b", "580ddc98-0db9-473d-a721-348f353f1d2b", 1541349936961L);


        mockServer.expect(manyTimes(), requestTo(uri))
                .andRespond(withSuccess(Helper.getGoogleJson("mockData/panels.json"), MediaType.APPLICATION_JSON));

        mockServer.expect(manyTimes(), requestTo(googleDistanceService.getRequestURL(currentEvent.getLocation(), previousEvent.getLocation(), GoogleDistanceService.Mode.WALKING)))
                .andRespond(withSuccess(Helper.getGoogleJson("mockData/google.json"), MediaType.APPLICATION_JSON));

        EventValidationBean eventValidationBean = new EventValidationBean(currentEvent, previousEvent, distanceFacadeService);

        boolean result = eventValidationBean.isItPossibleToWalkBetweenCurrentAndPreviousEvent();

        assertEquals(false, result);
    }


    @Test
    void isItPossibleToWalkBetweenCurrentAndPreviousEvent_TwoDifferentPanelsWithin1MinutesWalk() throws IOException {

        Event previousEvent;
        Event currentEvent;

        long timestamp1 = 1541349919142l;
        long timestamp2 = timestamp1 + (1*60000); // 1 minutes later

        previousEvent = Helper.createTestEvent( pannelMap,"7907775e-15ac-415f-a99c-e978856c8ec0", "7907775e-15ac-415f-a99c-e978856c8ec0", timestamp1);
        currentEvent = Helper.createTestEvent( pannelMap,"580ddc98-0db9-473d-a721-348f353f1d2b", "580ddc98-0db9-473d-a721-348f353f1d2b", timestamp2);

        mockServer.expect(manyTimes(), requestTo(uri))
                .andRespond(withSuccess(Helper.getGoogleJson("mockData/panels.json"), MediaType.APPLICATION_JSON));

        mockServer.expect(manyTimes(), requestTo(googleDistanceService.getRequestURL(currentEvent.getLocation(), previousEvent.getLocation(), GoogleDistanceService.Mode.WALKING)))
                .andRespond(withSuccess(Helper.getGoogleJson("mockData/google.json"), MediaType.APPLICATION_JSON));

        EventValidationBean eventValidationBean = new EventValidationBean(currentEvent, previousEvent, distanceFacadeService);

        boolean result = eventValidationBean.isItPossibleToWalkBetweenCurrentAndPreviousEvent();

        assertEquals(true, result);
    }

    @Test
    void isItPossibleToWalkBetweenCurrentAndPreviousEvent_TwoDifferentPanelsWithin2MinutesWalk() throws IOException {

        // should be possible within 2 minutes

        Event previousEvent;
        Event currentEvent;

        long timestamp1 = 1541349919142l;
        long timestamp2 = timestamp1 + (2*60000); // 2 minutes later

        previousEvent = Helper.createTestEvent( pannelMap, "7907775e-15ac-415f-a99c-e978856c8ec0", "7907775e-15ac-415f-a99c-e978856c8ec0", timestamp1);
        currentEvent = Helper.createTestEvent( pannelMap, "580ddc98-0db9-473d-a721-348f353f1d2b", "580ddc98-0db9-473d-a721-348f353f1d2b", timestamp2);


        mockServer.expect(manyTimes(), requestTo(uri))
                .andRespond(withSuccess(Helper.getGoogleJson("mockData/panels.json"), MediaType.APPLICATION_JSON));

        mockServer.expect(manyTimes(), requestTo(googleDistanceService.getRequestURL(currentEvent.getLocation(), previousEvent.getLocation(), IDistanceService.Mode.WALKING)))
                .andRespond(withSuccess(Helper.getGoogleJson("mockData/google.json"), MediaType.APPLICATION_JSON));

        EventValidationBean eventValidationBean = new EventValidationBean(currentEvent, previousEvent, distanceFacadeService);


        boolean result = eventValidationBean.isItPossibleToWalkBetweenCurrentAndPreviousEvent();

        assertEquals(true, result);
    }

    @Test
    void isItPossibleToWalkBetweenCurrentAndPreviousEvent_TwoDifferentPanelsWithin3MinutesWalk() throws IOException {

        // should be possible within 3 minutes
        Event previousEvent;
        Event currentEvent;

        long timestamp1 = 1541349919142L;
        long timestamp2 = timestamp1 + (3*60000); // 3 minutes later


        previousEvent = Helper.createTestEvent( pannelMap, "7907775e-15ac-415f-a99c-e978856c8ec0", "7907775e-15ac-415f-a99c-e978856c8ec0", timestamp1);
        currentEvent = Helper.createTestEvent( pannelMap, "580ddc98-0db9-473d-a721-348f353f1d2b", "580ddc98-0db9-473d-a721-348f353f1d2b", timestamp2);

        mockServer.expect(manyTimes(), requestTo(uri))
                .andRespond(withSuccess(Helper.getGoogleJson("mockData/panels.json"), MediaType.APPLICATION_JSON));

        mockServer.expect(manyTimes(), requestTo(googleDistanceService.getRequestURL(currentEvent.getLocation(), previousEvent.getLocation(), IDistanceService.Mode.WALKING)))
                .andRespond(withSuccess(Helper.getGoogleJson("mockData/google.json"), MediaType.APPLICATION_JSON));


        EventValidationBean eventValidationBean = new EventValidationBean(currentEvent, previousEvent, distanceFacadeService);

        boolean result = eventValidationBean.isItPossibleToWalkBetweenCurrentAndPreviousEvent();

        assertEquals(true, result);
    }

    @Test
    void isItPossibleToDriveBetweenCurrentAndPreviousEvent() throws IOException {
        /// this is where we need to implement logic to call out to google API

        // should be possible within 3 minutes
        Event previousEvent;
        Event currentEvent;

        long timestamp1 = 1541349919142l;
        long timestamp2 = timestamp1 + (3*60000); // 3 minutes later


        previousEvent = Helper.createTestEvent( pannelMap, "7907775e-15ac-415f-a99c-e978856c8ec0", "7907775e-15ac-415f-a99c-e978856c8ec0", timestamp1);
        currentEvent = Helper.createTestEvent( pannelMap, "580ddc98-0db9-473d-a721-348f353f1d2b", "7907775e-15ac-415f-a99c-e978856c8ec0", timestamp2);

        mockServer.expect(manyTimes(), requestTo(googleDistanceService.getRequestURL(currentEvent.getLocation(), previousEvent.getLocation(), IDistanceService.Mode.DRIVING)))
                .andRespond(withSuccess(Helper.getGoogleJson("mockData/google.json"), MediaType.APPLICATION_JSON));



        EventValidationBean eventValidationBean = new EventValidationBean(currentEvent, previousEvent, distanceFacadeService);

        boolean result = eventValidationBean.isItPossibleToDriveBetweenCurrentAndPreviousEvent();

        assertEquals(true, result);

    }

    @Test
    void shouldThrowExceptionIfBadKey() throws IOException {
        /// this is where we need to implement logic to call out to google API

        // should be possible within 3 minutes
        Event previousEvent;
        Event currentEvent;

        long timestamp1 = 1541349919142l;
        long timestamp2 = timestamp1 + (3*60000); // 3 minutes later

        googleDistanceService = new GoogleDistanceService(restTemplate,"BADKEY");
        locatorService = new LocatorService(restTemplate,uri) ;
        localDistanceService = new LocalDistanceService();
        flyAndDriveDistanceService =  new FlyAndDriveDistanceService();
        distanceFacadeService = new DistanceFacadeService(googleDistanceService,localDistanceService, flyAndDriveDistanceService);


        previousEvent = Helper.createTestEvent( pannelMap, "7907775e-15ac-415f-a99c-e978856c8ec0", "7907775e-15ac-415f-a99c-e978856c8ec0", timestamp1);
        currentEvent = Helper.createTestEvent( pannelMap, "580ddc98-0db9-473d-a721-348f353f1d2b", "7907775e-15ac-415f-a99c-e978856c8ec0", timestamp2);

        mockServer.expect(manyTimes(), requestTo(googleDistanceService.getRequestURL("BADKEY",currentEvent.getLocation(), previousEvent.getLocation(), IDistanceService.Mode.DRIVING)))
                .andRespond( withSuccess( Helper.getGoogleJson("mockData/google_badkey.json"), MediaType.APPLICATION_JSON));

        EventValidationBean eventValidationBean = new EventValidationBean(currentEvent, previousEvent, distanceFacadeService);


        assertThrows(IllegalArgumentException.class,
                ()->{
                    boolean result = eventValidationBean.isItPossibleToDriveBetweenCurrentAndPreviousEvent();
                });


    }


    @Test
    void bothCurrentAndPreviousEventsHaveStairsOrElevator() throws IOException {

        // Given

        Event previousEvent;
        Event currentEvent;

        long timestamp1 = 1541349919142l;
        long timestamp2 = timestamp1 + (3*60000); // 3 minutes later

        String cardUnderTest = "7907775e-15ac-415f-a99c-e978856c8ec0";


        previousEvent = Helper.createTestEvent( pannelMap, STANFORD_DEPARTMENT_OF_ECONOMICS_1ST_FLOOR_WEST_WING_CA_USA, cardUnderTest, timestamp1);
        currentEvent = Helper.createTestEvent( pannelMap, STANFORD_DEPARTMENT_OF_ECONOMICS_2ND_FLOOR_WEST_WING_CA_USA, cardUnderTest, timestamp2);


        // then

        mockServer.expect(manyTimes(), requestTo(googleDistanceService.getRequestURL(currentEvent.getLocation(), previousEvent.getLocation(), IDistanceService.Mode.WALK_AND_ELAVOTOR)))
                .andRespond(withSuccess(Helper.getGoogleJson("mockData/google.json"), MediaType.APPLICATION_JSON));

        EventValidationBean eventValidationBean = new EventValidationBean(currentEvent, previousEvent, distanceFacadeService);

        // when

        assertEquals(true, eventValidationBean.hasCurrentEventStairsOrElevators());
        assertEquals(true, eventValidationBean.hasPreviousEventStairsOrElevators());

    }

    @Test
    void onlyCurrentEventHasStairsOrElevator() throws IOException {

        // Given

        Event previousEvent;
        Event currentEvent;

        long timestamp1 = 1541349919142l;
        long timestamp2 = timestamp1 + (3*60000); // 3 minutes later

        String cardUnderTest = "7907775e-15ac-415f-a99c-e978856c8ec0";


        previousEvent = Helper.createTestEvent( pannelMap, STANFORD_DEPARTMENT_OF_ECONOMICS_SERRA_MALL_WEST_WING_CA_USA, cardUnderTest, timestamp1);
        currentEvent = Helper.createTestEvent( pannelMap, STANFORD_DEPARTMENT_OF_ECONOMICS_2ND_FLOOR_WEST_WING_CA_USA, cardUnderTest, timestamp2);


        // then

        mockServer.expect(manyTimes(), requestTo(googleDistanceService.getRequestURL(currentEvent.getLocation(), previousEvent.getLocation(), IDistanceService.Mode.WALK_AND_ELAVOTOR)))
                .andRespond(withSuccess(Helper.getGoogleJson("mockData/google.json"), MediaType.APPLICATION_JSON));

        EventValidationBean eventValidationBean = new EventValidationBean(currentEvent, previousEvent, distanceFacadeService);

        // when

        assertEquals(true, eventValidationBean.hasCurrentEventStairsOrElevators());
        assertEquals(false, eventValidationBean.hasPreviousEventStairsOrElevators());

    }




}