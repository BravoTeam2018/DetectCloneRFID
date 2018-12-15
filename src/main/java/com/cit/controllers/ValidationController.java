package com.cit.controllers;


import com.cit.exceptions.CardIdException;
import com.cit.exceptions.PanelIdException;
import com.cit.mappers.ValidationResponseMapper;
import com.cit.models.Event;
import com.cit.models.Location;
import com.cit.services.eventstore.IEventStoreService;
import com.cit.services.locator.ILocatorService;
import com.cit.services.notification.INotifierService;
import com.cit.services.validation.IValidationService;
import com.cit.transfer.ValidationServiceMQTTResponseDTO;
import com.cit.transfer.ValidationServiceRestResponseDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@Api(value = "/api/panels/request")
@RestController
@RequestMapping("/api/panels")
public class ValidationController {

    private ILocatorService locatorService;
    private IEventStoreService eventStoreService;
    private IValidationService validationService;
    private INotifierService notifierService;


    @Autowired
    public ValidationController(ILocatorService locatorService, IEventStoreService eventStoreService, IValidationService validationService, INotifierService notifierService ) {
        this.locatorService=locatorService;
        this.eventStoreService=eventStoreService;
        this.validationService=validationService;
        this.notifierService=notifierService;
    }

    @ApiOperation( value = "Used to add a previous event for cucumber acceptance tests testing only to be disabled in production", response = ValidationServiceRestResponseDTO.class)
    @GetMapping("/addprev")
    @ResponseStatus(HttpStatus.OK)
    public ValidationServiceRestResponseDTO getAddPreviousEvent(@RequestParam("panelId") String panelId,
                                                                @RequestParam("cardId") String cardId,
                                                                @RequestParam("allowed") boolean allowed ,
                                                                @RequestParam(value = "timeStamp", required = false) String timeStamp)  {
        return validation(panelId,cardId,allowed,timeStamp);
    }


    /**
     * getValidation - validation check against possible clone card
     * @return json payload with details of current and previous card/panel usage and if possible clone card.
     */
    @ApiOperation( value = "Validation check against possible clone card", response = ValidationServiceRestResponseDTO.class)
    @GetMapping("/request")
    @ResponseStatus(HttpStatus.OK)
    public ValidationServiceRestResponseDTO getValidation(@RequestParam("panelId") String panelId,
                                                          @RequestParam("cardId") String cardId,
                                                          @RequestParam("allowed") boolean allowed
    ){
        return validation(panelId,cardId,allowed,null);
    }


    public ValidationServiceRestResponseDTO validation(String panelId,
                                                       String cardId,
                                                       boolean allowed,
                                                       String timeStamp )
    {

        long timeNow ;

        // timeStamp is used to inject a previous time in to the system to simulate a past event
        // this if used by the cucumber tests
        if (timeStamp==null) {
            timeNow = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
        } else {
            timeNow = Long.parseLong(timeStamp);
        }

        // log the request
        if (log.isDebugEnabled()) {
            log.debug("Validation Request Parameters, panelId={}, cardId={}, allowed={}, timestamp={}, timenow{}", panelId, cardId, allowed,timeStamp, timeNow );
        }

        // throw exception if bad parameters
        validateRequestParameters(panelId, cardId);

        // Find the location associated with the panel
        Location locationOfPanel = locatorService.getLocationFromPanelId(panelId);


        // Create new event object based on request
        Event currentEvent = Event.builder()
                .panelId(panelId)
                .cardId(cardId)
                .location(locationOfPanel)
                .accessAllowed(allowed)
                .timestamp(timeNow)
                .build();

        // Find last event for card
        Event previousEvent = eventStoreService.getLastEventForCardId(cardId);


        // Perform validation checks and response DTO
        ValidationServiceRestResponseDTO response  = validationService.performEventValidation(currentEvent,previousEvent);


        // Finally store current event in cache so we can find it the next time the card is used
        eventStoreService.storeEvent(currentEvent);

        validationService.setEventListener( (ValidationServiceMQTTResponseDTO validationServiceMQTTResponseDTO) -> {

            log.debug("Clone detection result payload for subscribed MQTT Listeners = {}", validationServiceMQTTResponseDTO);

            String mqttMessageString = ValidationResponseMapper.toJsonString(validationServiceMQTTResponseDTO);

            log.debug("Clone detection result payload for subscribed MQTT Listeners (json) = {}", mqttMessageString);

            notifierService.publish(mqttMessageString);

        } );

        return response;
    }

    private void validateRequestParameters(String panelId, String cardId ) {

        // make sure its a valid panel UUID
        try{
            UUID.fromString(panelId);
        } catch (IllegalArgumentException exception){
            //handle the case where string is not valid UUID
            throw new PanelIdException("Bad format panelId="+panelId);
        }

        // make sure its a valid card UUID
        try{
            UUID.fromString(cardId);
        } catch (IllegalArgumentException exception){
            // handle the case where string is not valid UUID
            throw new CardIdException("Bad format cardId=" + cardId);
        }

    }

}



