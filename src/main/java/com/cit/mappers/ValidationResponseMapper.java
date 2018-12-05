package com.cit.mappers;

import com.cit.models.Event;
import com.cit.transfer.ValidationServiceMQTTResponseDTO;
import com.cit.transfer.ValidationServiceRestResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidationResponseMapper {

    private static final Logger log = LoggerFactory.getLogger(ValidationResponseMapper.class);

    public static final String IMPOSSIBLE_EVENT_TITLE = "Possible Cloned Access Card";
    public static final String POSSIBLE_EVENT_TITLE = "Regular access event. No issue found";

    public static final String IMPOSSIBLE_EVENT_DESC= "An access-card has been used that was very recently used in another location, indicating that it is unlikely to be the same card-holder";
    public static final String POSSIBLE_EVENT_DESC = "Regular access event. No issue found";

    public static final String SEVERITY_LOW = "Low";
    public static final String SEVERITY_HIGH = "High";


    private ValidationResponseMapper() {
        throw new IllegalStateException("Utility mapper class : call static methods only");
    }


    public static ValidationServiceMQTTResponseDTO toValidationServiceMQTTResponseDTO(Event current, Event previous, ValidationServiceRestResponseDTO validationServiceRestResponseDTO  ) {


        String title   = ( validationServiceRestResponseDTO.isValidEvent() ) ? POSSIBLE_EVENT_TITLE  :  IMPOSSIBLE_EVENT_TITLE ;
        String desc    = ( validationServiceRestResponseDTO.isValidEvent() ) ? POSSIBLE_EVENT_DESC  :  IMPOSSIBLE_EVENT_DESC;
        String severity =  ( validationServiceRestResponseDTO.isValidEvent() ) ? SEVERITY_LOW  :  SEVERITY_HIGH;

        return ValidationServiceMQTTResponseDTO.builder()
                .currentEvent(current)
                .previousEvent(previous)
                .description(desc)
                .severity(severity)
                .title(title)
                .build();

    }

    public static String toJsonString( ValidationServiceMQTTResponseDTO validationServiceMQTTResponseDTO ) {

        String result = null;

        ObjectMapper mapper = new ObjectMapper();
        try {
            result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(validationServiceMQTTResponseDTO);
        } catch (JsonProcessingException e) {
            log.error("Issue converting MQTT message = {}", e);
        }

        return result;
    }



}
