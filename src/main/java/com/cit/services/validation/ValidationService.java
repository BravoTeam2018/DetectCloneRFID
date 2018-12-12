package com.cit.services.validation;

import com.cit.mappers.ValidationResponseMapper;
import com.cit.models.Event;
import com.cit.services.validation.rules.EventValidationBean;
import com.cit.services.distance.IDistanceService;
import com.cit.transfer.ValidationServiceMQTTResponseDTO;
import com.cit.transfer.ValidationServiceRestResponseDTO;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.cit.services.validation.rules.EventValidationRuleBook.POSSIBLE_TIME_DISTANCE_EVENT;

@Service
public class ValidationService implements IValidationService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private RuleBook eventRuleBook;
    private IDistanceService distanceService;
    private IValidationResultEvent listener =  null;

    @Autowired
    public ValidationService(RuleBook eventRuleBook, IDistanceService distanceService) {
        this.eventRuleBook=eventRuleBook;
        this.distanceService=distanceService;
    }


    @Override
    public ValidationServiceRestResponseDTO performEventValidation(Event current, Event previous) {


        // default to possible result
        ValidationRulesResult validationRulesResult = ValidationRulesResult.builder()
                .reason(POSSIBLE_TIME_DISTANCE_EVENT)
                .validEvent(true)
                .build();

        if (log.isDebugEnabled()) {
            log.debug("PerformEventValidation(CurrentEvent={} , PreviousEvent={})", current, previous );
        }

        if (previous != null) {

            NameValueReferableMap facts = new FactMap();
            facts.setValue("eventValidation", new EventValidationBean(current,previous,distanceService));

            eventRuleBook.run(facts);

            Optional<Result> optionalResult = eventRuleBook.getResult();
            if (optionalResult.isPresent()) {

                ValidationRulesResult result = (ValidationRulesResult) optionalResult.get().getValue();
                validationRulesResult.setReason(result.getReason());
                validationRulesResult.setValidEvent(result.isValidEvent());
            }

        } else {  // must be a valid Event
            if (log.isDebugEnabled()) {
                log.debug("Can not find previous event for cardId={} , Event={}" ,current.getCardId(), current );
            }
        }


        //
        // Build the response DTO
        //
        ValidationServiceRestResponseDTO validationServiceRestResponseDTO =  ValidationServiceRestResponseDTO.builder()
                .currentEvent(current)
                .previousEvent(previous)
                .reason(validationRulesResult.getReason())
                .validEvent(validationRulesResult.isValidEvent())
                .build();


        // prepare response for MQTTBroker my mapping
        ValidationServiceMQTTResponseDTO validationServiceMQTTResponseDTO = ValidationResponseMapper.toValidationServiceMQTTResponseDTO(current, previous, validationServiceRestResponseDTO);

        // publish the event to subscriber
        publishDetectionEvent(validationServiceMQTTResponseDTO);

        return validationServiceRestResponseDTO;

    }


    public void setEventListener(IValidationResultEvent listener) {
        this.listener = listener;
    }

    public void publishDetectionEvent(ValidationServiceMQTTResponseDTO validationServiceMQTTResponseDTO) {
        if ( this.listener!=null ) {
            this.listener.detectionResult(validationServiceMQTTResponseDTO);
        }
    }



}
