package com.cit.services.distance;

import com.cit.Helper;
import com.cit.models.DistanceResult;
import com.cit.models.Event;
import com.cit.models.GPSCoordinate;
import com.cit.models.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import static com.cit.Helper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class LocalDistanceServiceTest {

    Map<UUID, Location> allLocations;

    LocalDistanceService localDistanceService;

    @BeforeEach
    void setup() throws FileNotFoundException {
        allLocations = getAllLocations();
        localDistanceService = new LocalDistanceService();
    }

    @Test
    void testWalkingDistanceBetweenTwoLocalBuildings_zeroAltitudeDifference() {

        // given
        String cardIdUnderTest = "580ddc98-0db9-473d-a721-348f353f1d2b";
        Event event1  = Helper.createTestEvent(allLocations,CIT_LIBRARY_WEST_WING_ENTRY_DOORS,cardIdUnderTest, Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
        Event event2  = Helper.createTestEvent(allLocations,CIT_LIBRARY_NORTH_GROUND_EXIT,cardIdUnderTest, Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());

        // then
        DistanceResult responseDTO =  responseDTO = localDistanceService.execute(event1.getLocation(), event2.getLocation(), IDistanceService.Mode.WALKING);

        // when
        assertEquals(true, responseDTO.getStatus().equals("OK"));
        assertEquals(51.35523198140736, responseDTO.getDistance());
        assertEquals(36, responseDTO.getDuration());
    }

    @Test
    void testWalkingAndEelavatorDistanceBetweenTwoLocalBuildings_withAltitudeDifference() {

        // given
        String cardIdUnderTest = "580ddc98-0db9-473d-a721-348f353f1d2b";
        Event event1  = Helper.createTestEvent(allLocations,STANFORD_DEPARTMENT_OF_ECONOMICS_2ND_FLOOR_WEST_WING_CA_USA,cardIdUnderTest, Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
        Event event2  = Helper.createTestEvent(allLocations,STANFORD_DEPARTMENT_OF_ECONOMICS_3RD_FLOOR_WEST_WING_CA_USA,cardIdUnderTest, Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());


        // then
        DistanceResult responseDTO =  responseDTO = localDistanceService.execute(event1.getLocation(), event2.getLocation(), IDistanceService.Mode.WALK_AND_ELAVOTOR);

        //System.out.println(responseDTO);

        // when
        assertEquals(true, responseDTO.getStatus().equals("OK"));
        assertEquals(10.004031466862925, responseDTO.getDistance());
        assertEquals(21, responseDTO.getDuration()); // includes wait time for elevator
    }

    @Test
    void testWalkingDistanceBetweenTwoLocalBuildings_withAltitudeDifference() {

        // given
        String cardIdUnderTest = "580ddc98-0db9-473d-a721-348f353f1d2b";
        Event event1  = Helper.createTestEvent(allLocations,STANFORD_DEPARTMENT_OF_ECONOMICS_2ND_FLOOR_WEST_WING_CA_USA,cardIdUnderTest, Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
        Event event2  = Helper.createTestEvent(allLocations,STANFORD_DEPARTMENT_OF_ECONOMICS_3RD_FLOOR_WEST_WING_CA_USA,cardIdUnderTest, Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());


        // then
        DistanceResult responseDTO =  responseDTO = localDistanceService.execute(event1.getLocation(), event2.getLocation(), IDistanceService.Mode.WALKING);

        //System.out.println(responseDTO);

        // when
        assertEquals(true, responseDTO.getStatus().equals("OK"));
        assertEquals(10.004031466862925, responseDTO.getDistance());
        assertEquals(7, responseDTO.getDuration()); // assumes using stairs is faster
    }


}