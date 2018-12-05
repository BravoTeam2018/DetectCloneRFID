package com.cit.services.validation;

import com.cit.models.Event;
import com.cit.transfer.ValidationServiceMQTTResponseDTO;
import com.cit.transfer.ValidationServiceRestResponseDTO;

public interface IValidationService {
    ValidationServiceRestResponseDTO performEventValidation(Event current, Event previous);
    public void setEventListener(IValidationResultEvent listener);
    public void publishDetectionEvent(ValidationServiceMQTTResponseDTO validationServiceMQTTResponseDTO);

}
