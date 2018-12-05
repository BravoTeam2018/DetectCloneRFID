package com.cit.services.validation;

import com.cit.transfer.ValidationServiceMQTTResponseDTO;

public interface IValidationResultEvent {

    void detectionResult(ValidationServiceMQTTResponseDTO validationServiceMQTTResponseDTO);

}