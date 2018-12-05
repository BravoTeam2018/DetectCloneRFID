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
