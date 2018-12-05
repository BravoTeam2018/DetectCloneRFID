package com.cit.services.validation;

@lombok.Data
@lombok.Builder
public class ValidationRulesResult {
    private boolean validEvent;
    private String reason;
}
