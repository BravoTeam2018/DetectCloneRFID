package com.cit.services.validation;

import com.cit.Helper;
import com.cit.config.ServicesConfig;
import com.cit.models.DistanceResult;
import com.cit.models.Event;
import com.cit.models.GPSCoordinate;
import com.cit.models.Location;
import com.cit.services.distance.*;
import com.cit.services.validation.rules.EventValidationBean;
import com.cit.services.validation.rules.EventValidationRuleBook;
import com.cit.services.validation.ValidationService;
import com.cit.services.validation.ValidationRulesResult;
import com.cit.transfer.ValidationServiceRestResponseDTO;
import com.deliveredtechnologies.rulebook.lang.RuleBookBuilder;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestGatewaySupport;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import static com.cit.Helper.*;
import static com.cit.services.validation.rules.EventValidationRuleBook.IMPOSSIBLE_TIME_DISTANCE_EVENT;
import static com.cit.services.validation.rules.EventValidationRuleBook.POSSIBLE_TIME_DISTANCE_EVENT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ContextConfiguration(classes = {ServicesConfig.class})
class ValidationServiceTest {

    private static Map<UUID, Location> allLocations;


    //TODO Load in within application properties
    String apikey = "AIzaSyBkzAoC6KtzmNl0ZSIyLZEq8Vr8jhRkItI";

    @Autowired
    private ValidationService validationService;

    RestTemplate restTemplate = new RestTemplate();

    private MockRestServiceServer mockServer;


    @InjectMocks
    GoogleDistanceService googleDistanceService;

    @InjectMocks
    DistanceFacadeService distanceFacadeService;

    RuleBook ruleBook;


    @BeforeEach
    void setUp() throws FileNotFoundException {

        // setup test data
        allLocations = getAllLocations();

        MockitoAnnotations.initMocks(this);

        googleDistanceService = new GoogleDistanceService(restTemplate, apikey);
        distanceFacadeService = new DistanceFacadeService(googleDistanceService, new LocalDistanceService(), new FlyAndDriveDistanceService());
        ruleBook = RuleBookBuilder.create(EventValidationRuleBook.class).withResultType(ValidationRulesResult.class)
                .withDefaultResult(ValidationRulesResult.builder().reason(POSSIBLE_TIME_DISTANCE_EVENT).validEvent(true).build())
                .build();

        validationService = new ValidationService(ruleBook, distanceFacadeService);

        RestGatewaySupport gateway = new RestGatewaySupport();
        gateway.setRestTemplate(restTemplate);

        mockServer = MockRestServiceServer.createServer(gateway);


    }

    @AfterEach
    void tearDown() {
    }


    /**
     * Feature: The validation system shall perform checks for possible clone cards
     * Scenario: Card used at location "CIT Library West Wing Entry Doors, Cork, Ireland" where previous event found at same location. Possible to travel within time frame
     * Given card "5507775e-15ac-415f-a99c-e978856c8ec0" used at panel "580ddc98-0db9-473d-a721-348f353f1d2b"
     * And previous event found 120 seconds before at panel "580ddc98-0db9-473d-a721-348f353f1d2b"
     * When check performed
     * Then responds with validEvent "true"
     */
    @Test
    void givenNoPreviousUsageOfCardPreviousShouldBeNull() {

        // Given

        Event previousEvent;
        Event currentEvent;

        long timestamp1 = 1541349919142l;
        long timestamp2 = timestamp1 + (2 * 60 * 1000); // 120 seconds later

        String cardUnderTest = "5507775e-15ac-415f-a99c-e978856c8ec0";


        previousEvent = null;//= Helper.createTestEvent( allLocations, STANFORD_DEPARTMENT_OF_ECONOMICS_SERRA_MALL_WEST_WING_CA_USA, cardUnderTest, timestamp1);
        currentEvent = Helper.createTestEvent(allLocations, CIT_LIBRARY_WEST_WING_ENTRY_DOORS, cardUnderTest, timestamp2);


        // then
        ValidationServiceRestResponseDTO validationServiceRestResponseDTO = validationService.performEventValidation(currentEvent, previousEvent);
        assertNotNull(validationServiceRestResponseDTO);

        // when
        assertEquals(true, validationServiceRestResponseDTO.isValidEvent());
        assertEquals(POSSIBLE_TIME_DISTANCE_EVENT, validationServiceRestResponseDTO.getReason());
        assertEquals(null, validationServiceRestResponseDTO.getPreviousEvent());
        assertEquals(currentEvent, validationServiceRestResponseDTO.getCurrentEvent());
    }

    /**
     * Feature: The validation system shall perform checks for possible clone cards w, where current and previous events were in different countries
     * <p>
     * Scenario: Card used at location "CIT Library West Wing Entry Doors, Cork, Ireland" where previous event found at location "Stanford Department of Economics, 1st Floor, West Wing, CA, USA". Possible to travel within time frame
     * Given card "4407775e-15ac-415f-a99c-e978856c8ec0" used at panel "580ddc98-0db9-473d-a721-348f353f1d2b"
     * And previous event found 120 seconds before at panel "d50f91a5-6f2b-4a70-ab6c-e0fec58c866e"
     * When check performed
     * Then responds with validEvent "false"
     */
    @Test
    void given_PreviousEvent_At_Standord1stFloor_CurrentEvent_At_CITWestWing_Within_1Second_ShouldFail() throws IOException {

        // Given
        Event previousEvent;
        Event currentEvent;

        long timestamp1 = 1541349919142l;
        long timestamp2 = timestamp1 + (1000); // 1 second later

        String cardUnderTest = "4407775e-15ac-415f-a99c-e978856c8ec0";

        previousEvent = Helper.createTestEvent(allLocations, STANFORD_DEPARTMENT_OF_ECONOMICS_1ST_FLOOR_WEST_WING_CA_USA, cardUnderTest, timestamp1);
        currentEvent = Helper.createTestEvent(allLocations, CIT_LIBRARY_WEST_WING_ENTRY_DOORS, cardUnderTest, timestamp2);


        // GoogleDistanceAPIResponseDTO(destinationAddresses=[37.428637,-122.165954], originAddresses=[51.884827,-8.533947], rows=[GoogleDistanceAPIResponseDTO.Rows(elements=[GoogleDistanceAPIResponseDTO.Rows.Elements(distance=null, duration=null, status=ZERO_RESULTS)])], status=OK)
        mockServer.expect(once(), requestTo(googleDistanceService.getRequestURL(currentEvent.getLocation(), previousEvent.getLocation(), GoogleDistanceService.Mode.DRIVING)))
                .andRespond(withSuccess(Helper.getGoogleJson("mockData/google_no_results.json"), MediaType.APPLICATION_JSON));

        // then
        ValidationServiceRestResponseDTO validationServiceRestResponseDTO = validationService.performEventValidation(currentEvent, previousEvent);
        assertNotNull(validationServiceRestResponseDTO);

        // when
        assertEquals(false, validationServiceRestResponseDTO.isValidEvent());
        assertEquals(IMPOSSIBLE_TIME_DISTANCE_EVENT, validationServiceRestResponseDTO.getReason());
        assertEquals(previousEvent, validationServiceRestResponseDTO.getPreviousEvent());
        assertEquals(currentEvent, validationServiceRestResponseDTO.getCurrentEvent());
    }

    /**
     * Feature: The validation system shall perform checks for possible clone cards w, where current and previous events were in different countries
     * <p>
     * Scenario: Card used at location "CIT Library West Wing Entry Doors, Cork, Ireland" where previous event found (48hours before) at location "Stanford Department of Economics, 1st Floor, West Wing, CA, USA". Possible to travel within time frame
     * Given card "4407775e-15ac-415f-a99c-e978856c8ec0" used at panel "580ddc98-0db9-473d-a721-348f353f1d2b"
     * And previous event found 172800 seconds before at panel "5e11d5ee-7715-4080-bfe6-25c66d8ce821"
     * When check performed
     * Then responds with validEvent "true"
     */
    @Test
    void given_PreviousEvent_At_Standord1stFloor_CurrentEvent_At_CITWestWing_Within_172800Seconds_ShouldSucceed() throws IOException {

        // Given
        Event previousEvent;
        Event currentEvent;

        long timestamp1 = 1541349919142l;
        long timestamp2 = timestamp1 + (172800 * 1000); // 172800 seconds later

        String cardUnderTest = "4407775e-15ac-415f-a99c-e978856c8ec0";

        previousEvent = Helper.createTestEvent(allLocations, STANFORD_DEPARTMENT_OF_ECONOMICS_1ST_FLOOR_WEST_WING_CA_USA, cardUnderTest, timestamp1);
        currentEvent = Helper.createTestEvent(allLocations, CIT_LIBRARY_WEST_WING_ENTRY_DOORS, cardUnderTest, timestamp2);


        // GoogleDistanceAPIResponseDTO(destinationAddresses=[37.428637,-122.165954], originAddresses=[51.884827,-8.533947], rows=[GoogleDistanceAPIResponseDTO.Rows(elements=[GoogleDistanceAPIResponseDTO.Rows.Elements(distance=null, duration=null, status=ZERO_RESULTS)])], status=OK)
        mockServer.expect(once(), requestTo(googleDistanceService.getRequestURL(currentEvent.getLocation(), previousEvent.getLocation(), GoogleDistanceService.Mode.DRIVING)))
                .andRespond(withSuccess(Helper.getGoogleJson("mockData/google_no_results.json"), MediaType.APPLICATION_JSON));

        // then
        ValidationServiceRestResponseDTO validationServiceRestResponseDTO = validationService.performEventValidation(currentEvent, previousEvent);
        assertNotNull(validationServiceRestResponseDTO);

        // when
        assertEquals(true, validationServiceRestResponseDTO.isValidEvent());
        assertEquals(POSSIBLE_TIME_DISTANCE_EVENT, validationServiceRestResponseDTO.getReason());
        assertEquals(previousEvent, validationServiceRestResponseDTO.getPreviousEvent());
        assertEquals(currentEvent, validationServiceRestResponseDTO.getCurrentEvent());
    }

    /**
     * Feature: The validation system shall perform checks for possible clone cards w, where current and previous events were in different countries
     * <p>
     * Scenario: Card used at location "CIT Library North Ground Exit, Cork, Ireland" where previous event found (60 seconds before) at a location "Stanford Department of Economics, 1st Floor, West Wing, CA, USA". Impossible to travel within time frame
     * Given card "6607775e-15ac-415f-a99c-e978856c8ec0" used at panel "7907775e-15ac-415f-a99c-e978856c8ec0"
     * And previous event found 60 seconds before at panel "5e11d5ee-7715-4080-bfe6-25c66d8ce821"
     * When check performed
     * Then responds with validEvent "false"
     */
    @Test
    void given_PreviousEvent_At_Standord1stFloor_CurrentEvent_At_CITWestWing_Within_60Seconds_ShouldFail() throws IOException {

        // Given
        Event previousEvent;
        Event currentEvent;

        long timestamp1 = 1541349919142l;
        long timestamp2 = timestamp1 + (60 * 1000); // 60 seconds later

        String cardUnderTest = "6607775e-15ac-415f-a99c-e978856c8ec0";

        previousEvent = Helper.createTestEvent(allLocations, STANFORD_DEPARTMENT_OF_ECONOMICS_1ST_FLOOR_WEST_WING_CA_USA, cardUnderTest, timestamp1);
        currentEvent = Helper.createTestEvent(allLocations, CIT_LIBRARY_WEST_WING_ENTRY_DOORS, cardUnderTest, timestamp2);


        // GoogleDistanceAPIResponseDTO(destinationAddresses=[37.428637,-122.165954], originAddresses=[51.884827,-8.533947], rows=[GoogleDistanceAPIResponseDTO.Rows(elements=[GoogleDistanceAPIResponseDTO.Rows.Elements(distance=null, duration=null, status=ZERO_RESULTS)])], status=OK)
        mockServer.expect(once(), requestTo(googleDistanceService.getRequestURL(currentEvent.getLocation(), previousEvent.getLocation(), GoogleDistanceService.Mode.DRIVING)))
                .andRespond(withSuccess(Helper.getGoogleJson("mockData/google_no_results.json"), MediaType.APPLICATION_JSON));

        // then
        ValidationServiceRestResponseDTO validationServiceRestResponseDTO = validationService.performEventValidation(currentEvent, previousEvent);
        assertNotNull(validationServiceRestResponseDTO);

        // when
        assertEquals(false, validationServiceRestResponseDTO.isValidEvent());
        assertEquals(IMPOSSIBLE_TIME_DISTANCE_EVENT, validationServiceRestResponseDTO.getReason());
        assertEquals(previousEvent, validationServiceRestResponseDTO.getPreviousEvent());
        assertEquals(currentEvent, validationServiceRestResponseDTO.getCurrentEvent());
    }


    /**
     * Feature: The validation system shall perform checks for possible clone cards within the same building
     * <p>
     * Scenario: Card used at location "Stanford Department of Economics, 3rd Floor, West Wing, CA, USA" where previous event found (10 seconds before) at location "Stanford Department of Economics, 1st Floor, West Wing, CA, USA". ImPossible to travel within time frame
     * Given card "3307775e-15ac-415f-a99c-e978856c8ec0" used at panel "5ae6dbcd-9166-4d80-99d9-069e69bead15"
     * And previous event found 10 seconds before at panel "5e11d5ee-7715-4080-bfe6-25c66d8ce821"
     * When check performed
     * Then responds with validEvent "false"
     */
    @Test
    void given_PreviousEvent_At_Standord1st3RDFloor__CurrentEvent_At_Standord1st1STDFloor_Within_10Seconds_ShouldFail() throws IOException {

        // Given
        Event previousEvent;
        Event currentEvent;

        long timestamp1 = 1541349919142l;
        long timestamp2 = timestamp1 + (10 * 1000); // 10 seconds later

        String cardUnderTest = "3307775e-15ac-415f-a99c-e978856c8ec0";

        previousEvent = Helper.createTestEvent(allLocations, STANFORD_DEPARTMENT_OF_ECONOMICS_3RD_FLOOR_WEST_WING_CA_USA, cardUnderTest, timestamp1);
        currentEvent = Helper.createTestEvent(allLocations, STANFORD_DEPARTMENT_OF_ECONOMICS_1ST_FLOOR_WEST_WING_CA_USA, cardUnderTest, timestamp2);

        // GoogleDistanceAPIResponseDTO(destinationAddresses=[37.428637,-122.165954], originAddresses=[51.884827,-8.533947], rows=[GoogleDistanceAPIResponseDTO.Rows(elements=[GoogleDistanceAPIResponseDTO.Rows.Elements(distance=null, duration=null, status=ZERO_RESULTS)])], status=OK)
        mockServer.expect(once(), requestTo(googleDistanceService.getRequestURL(currentEvent.getLocation(), previousEvent.getLocation(), GoogleDistanceService.Mode.DRIVING)))
                .andRespond(withSuccess(Helper.getGoogleJson("mockData/google_no_results.json"), MediaType.APPLICATION_JSON));

        // then
        ValidationServiceRestResponseDTO validationServiceRestResponseDTO = validationService.performEventValidation(currentEvent, previousEvent);


        // when
        assertNotNull(validationServiceRestResponseDTO);
        assertEquals(false, validationServiceRestResponseDTO.isValidEvent());
        assertEquals(IMPOSSIBLE_TIME_DISTANCE_EVENT, validationServiceRestResponseDTO.getReason());
        assertEquals(previousEvent, validationServiceRestResponseDTO.getPreviousEvent());
        assertEquals(currentEvent, validationServiceRestResponseDTO.getCurrentEvent());
    }

    /**
     * Feature: The validation system shall perform checks for possible clone cards within the same building
     * <p>
     * Scenario: Card used at location "Stanford Department of Economics, 3rd Floor, West Wing, CA, USA" where previous event found (20 seconds before) at location "Stanford Department of Economics, 1st Floor, West Wing, CA, USA". Possible to travel within time frame
     * Given card "3307775e-15ac-415f-a99c-e978856c8ec0" used at panel "5ae6dbcd-9166-4d80-99d9-069e69bead15"
     * And previous event found 20 seconds before at panel "5e11d5ee-7715-4080-bfe6-25c66d8ce821"
     * When check performed
     * Then responds with validEvent "true"
     */
    @Test
    void given_PreviousEvent_At_Standord1st3RDFloor__CurrentEvent_At_Standord1st1STDFloor_Within_20Seconds_ShouldFail() throws IOException {

        // Given
        Event previousEvent;
        Event currentEvent;

        long timestamp1 = 1541349919142l;
        long timestamp2 = timestamp1 + (20 * 1000); // 20 seconds later

        String cardUnderTest = "3307775e-15ac-415f-a99c-e978856c8ec0";

        previousEvent = Helper.createTestEvent(allLocations, STANFORD_DEPARTMENT_OF_ECONOMICS_3RD_FLOOR_WEST_WING_CA_USA, cardUnderTest, timestamp1);
        currentEvent = Helper.createTestEvent(allLocations, STANFORD_DEPARTMENT_OF_ECONOMICS_1ST_FLOOR_WEST_WING_CA_USA, cardUnderTest, timestamp2);

        // GoogleDistanceAPIResponseDTO(destinationAddresses=[37.428637,-122.165954], originAddresses=[51.884827,-8.533947], rows=[GoogleDistanceAPIResponseDTO.Rows(elements=[GoogleDistanceAPIResponseDTO.Rows.Elements(distance=null, duration=null, status=ZERO_RESULTS)])], status=OK)
        mockServer.expect(once(), requestTo(googleDistanceService.getRequestURL(currentEvent.getLocation(), previousEvent.getLocation(), GoogleDistanceService.Mode.DRIVING)))
                .andRespond(withSuccess(Helper.getGoogleJson("mockData/google_no_results.json"), MediaType.APPLICATION_JSON));

        // then
        ValidationServiceRestResponseDTO validationServiceRestResponseDTO = validationService.performEventValidation(currentEvent, previousEvent);


        // when
        assertNotNull(validationServiceRestResponseDTO);
        assertEquals(true, validationServiceRestResponseDTO.isValidEvent());
        assertEquals(POSSIBLE_TIME_DISTANCE_EVENT, validationServiceRestResponseDTO.getReason());
        assertEquals(previousEvent, validationServiceRestResponseDTO.getPreviousEvent());
        assertEquals(currentEvent, validationServiceRestResponseDTO.getCurrentEvent());
    }


    /**
     * Feature: The validation system shall perform checks for possible clone cards
     * Scenario: Card used at location "CIT Library West Wing Entry Doors, Cork, Ireland" where previous event found at same location. Possible to travel within time frame
     * Given card "5507775e-15ac-415f-a99c-e978856c8ec0" used at panel "580ddc98-0db9-473d-a721-348f353f1d2b"
     * And previous event found 120 seconds before at panel "580ddc98-0db9-473d-a721-348f353f1d2b"
     * When check performed
     * Then responds with validEvent "true"
     * And responds within less than 1 seconds
     */

    @Test
    void given_PreviousEvent_At_CITWestWing__CurrentEvent_At_CITWestWing_Within_1Seconds_ShouldFail() throws IOException {

        // Given
        Event previousEvent;
        Event currentEvent;

        long timestamp1 = 1541349919142L;
        long timestamp2 = timestamp1 + (1000); // 1 seconds later

        String cardUnderTest = "5507775e-15ac-415f-a99c-e978856c8ec0";

        previousEvent = Helper.createTestEvent(allLocations, CIT_LIBRARY_WEST_WING_ENTRY_DOORS, cardUnderTest, timestamp1);
        currentEvent = Helper.createTestEvent(allLocations, CIT_LIBRARY_WEST_WING_ENTRY_DOORS, cardUnderTest, timestamp2);

        mockServer.expect(once(), requestTo(googleDistanceService.getRequestURL(currentEvent.getLocation(), previousEvent.getLocation(), GoogleDistanceService.Mode.DRIVING)))
                .andRespond(withSuccess(Helper.getGoogleJson("mockData/google_no_results.json"), MediaType.APPLICATION_JSON));

        // then
        ValidationServiceRestResponseDTO validationServiceRestResponseDTO = validationService.performEventValidation(currentEvent, previousEvent);

        // when
        assertNotNull(validationServiceRestResponseDTO);
        assertEquals(true, validationServiceRestResponseDTO.isValidEvent());
        assertEquals(POSSIBLE_TIME_DISTANCE_EVENT, validationServiceRestResponseDTO.getReason());
        assertEquals(previousEvent, validationServiceRestResponseDTO.getPreviousEvent());
        assertEquals(currentEvent, validationServiceRestResponseDTO.getCurrentEvent());
    }


}