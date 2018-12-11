package com.cit;

import com.cit.models.Event;
import com.cit.models.Location;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

public class Helper {

    public static final String CIT_LIBRARY_WEST_WING_ENTRY_DOORS = "580ddc98-0db9-473d-a721-348f353f1d2b";
    public static final String CIT_LIBRARY_NORTH_GROUND_EXIT = "7907775e-15ac-415f-a99c-e978856c8ec0";

    public static final String STANFORD_DEPARTMENT_OF_ECONOMICS_SERRA_MALL_WEST_WING_CA_USA = "bc6271c1-b539-4ece-9f5e-acc122af9fe4";
    public static final String STANFORD_DEPARTMENT_OF_ECONOMICS_1ST_FLOOR_WEST_WING_CA_USA = "5e11d5ee-7715-4080-bfe6-25c66d8ce821";
    public static final String STANFORD_DEPARTMENT_OF_ECONOMICS_2ND_FLOOR_WEST_WING_CA_USA = "3a253fa0-456e-4254-a871-5d660c5950ae";
    public static final String STANFORD_DEPARTMENT_OF_ECONOMICS_3RD_FLOOR_WEST_WING_CA_USA = "5ae6dbcd-9166-4d80-99d9-069e69bead15";


    public static String getGoogleJson(String filename) throws IOException {
        ClassLoader classLoader = ObjectMapper.class.getClassLoader();
        URL resource = classLoader.getResource(filename);
        File file = new File(resource.getPath());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode location = mapper.readTree(file);
        return mapper.writeValueAsString(location);
    }

    public static Map<UUID, Location> getAllLocations() throws FileNotFoundException {

        Map<UUID, Location> result = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            result = objectMapper.readValue(getGoogleJson("mockData/panels.json") , new TypeReference<Map<UUID, Location>>(){});

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Event createTestEvent(Map<UUID, Location> map, String panelId, String cardId, long timestamp) {
        return Event.builder()
                .panelId(panelId)
                .cardId(cardId)
                .location(map.get(UUID.fromString(panelId)))
                .accessAllowed(true)
                .timestamp(timestamp)
                .build();
    }


}
