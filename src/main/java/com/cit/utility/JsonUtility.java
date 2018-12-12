package com.cit.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtility {

    private static final Logger log = LoggerFactory.getLogger(JsonUtility.class);

    private JsonUtility() {
        throw new IllegalStateException("Utility class : call static methods only");
    }

    public static String toJsonString( Object obj ) {

        String result = null;

        ObjectMapper mapper = new ObjectMapper();
        try {
            result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Issue converting object = {}", e);
        }

        return result;
    }


}
