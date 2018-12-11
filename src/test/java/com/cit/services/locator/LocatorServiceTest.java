package com.cit.services.locator;

import com.cit.config.ServicesConfig;
import com.cit.exceptions.PanelNotFoundException;
import com.cit.models.Location;
import com.cit.services.locator.LocatorService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestGatewaySupport;

import java.io.*;
import java.net.URL;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


@ContextConfiguration(classes = ServicesConfig.class)
class LocatorServiceTest {

    String uri = "http://localhost:8080/api/locations";

    RestTemplate restTemplate = new RestTemplate();

    private LocatorService locatorService;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        RestGatewaySupport gateway = new RestGatewaySupport();
        gateway.setRestTemplate(restTemplate);

        mockServer = MockRestServiceServer.createServer(gateway);
        locatorService = new LocatorService(restTemplate,uri) ;
    }

    @Test
    void testFindPanelLocation_alreadyExists() throws IOException {

        String panelId = "580ddc98-0db9-473d-a721-348f353f1d2b";

        mockServer.expect(once(), requestTo(uri))
                .andRespond(withSuccess(getAllPanelsJson(), MediaType.APPLICATION_JSON));

        Location location = locatorService.getLocationFromPanelId(panelId);

        assertNotNull(location);
        assertEquals(100,location.getAltitude());
        assertEquals(51.884827,location.getCoordinates().getLatitude());
        assertEquals(-8.533947,location.getCoordinates().getLongitude());
        assertEquals("CIT Library West Wing Entry Doors, Cork, Ireland",location.getRelativeLocation());
    }


    @Test
    void testFindPanelLocation_NotExists() throws IOException {

        String panelId = "580ddc98-0db9-473d-a721-348f353fff";

        mockServer.expect(once(), requestTo(uri))
                .andRespond(withSuccess(getAllPanelsJson(), MediaType.APPLICATION_JSON));


        assertThrows(PanelNotFoundException.class,
                ()->{
                    Location location = locatorService.getLocationFromPanelId(panelId);
                });

    }

    /**
     *
     *  Utility function to load in test response data from /src/test/resources/panels.json
     *
     */

    static String getAllPanelsJson() throws IOException {
        ClassLoader classLoader = ObjectMapper.class.getClassLoader();
        URL resource = classLoader.getResource("mockData/panels.json");
        File file = new File(resource.getPath());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode location = mapper.readTree(file);
        return mapper.writeValueAsString(location);
    }

    private static Map<UUID, Location> getAllLocations() throws FileNotFoundException {

        Map<UUID, Location> result = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            result = objectMapper.readValue(getAllPanelsJson() , new TypeReference<Map<UUID, Location>>(){});

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


}