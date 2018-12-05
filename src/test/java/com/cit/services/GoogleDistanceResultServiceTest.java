package com.cit.services;

import com.cit.Helper;
import com.cit.config.ServicesConfig;
import com.cit.models.DistanceResult;
import com.cit.models.GPSCoordinate;
import com.cit.models.Location;
import com.cit.services.distance.GoogleDistanceService;
import com.cit.services.distance.IDistanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestGatewaySupport;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ContextConfiguration(classes = ServicesConfig.class)
class GoogleDistanceResultServiceTest {

    String apikey="GOOGLEKEY";
    RestTemplate restTemplate =  new RestTemplate();
    private MockRestServiceServer mockServer;
    GoogleDistanceService googleDistanceService;

    Location CITWest;
    Location CITNorth;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        // setup test data
        CITWest = Location.builder()
                .coordinates(GPSCoordinate.builder()
                        .latitude(51.884827)
                        .longitude(-8.533947)
                        .build())
                .altitude(100)
                .relativeLocation("CIT Library West Wing Entry Doors, Cork, Ireland")
                .build();

        CITNorth = Location.builder()
                .coordinates(GPSCoordinate.builder()
                        .latitude(51.884969)
                        .longitude(-8.533235)
                        .build())
                .altitude(100)
                .relativeLocation("CIT Library North Ground Exit, Cork, Ireland")
                .build();

        RestGatewaySupport gateway = new RestGatewaySupport();
        gateway.setRestTemplate(restTemplate);

        mockServer = MockRestServiceServer.createServer(gateway);

        googleDistanceService = new GoogleDistanceService(restTemplate,apikey);
    }

    @Test
    void testSGoogleDistanceService_walking() throws IOException {

        mockServer.expect(once(), requestTo(googleDistanceService.getRequestURL(CITWest, CITNorth, GoogleDistanceService.Mode.WALKING)))
                .andRespond(withSuccess(Helper.getGoogleJson("mockData/google.json"), MediaType.APPLICATION_JSON));

        DistanceResult responseDTO = googleDistanceService.execute(CITWest, CITNorth, IDistanceService.Mode.WALKING);

        System.out.println(responseDTO);
        assertEquals(true, responseDTO.getStatus().equals("OK"));
    }

    @Test
    void testSGoogleDistanceService_driving() throws IOException {

        mockServer.expect(once(), requestTo(googleDistanceService.getRequestURL(CITWest, CITNorth, GoogleDistanceService.Mode.DRIVING)))
                .andRespond(withSuccess(Helper.getGoogleJson("mockData/google.json"), MediaType.APPLICATION_JSON));

        DistanceResult responseDTO =  responseDTO = googleDistanceService.execute(CITWest, CITNorth, IDistanceService.Mode.DRIVING);

        System.out.println(responseDTO);
        assertEquals(true, responseDTO.getStatus().equals("OK"));
    }


}

