package com.cit.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DistanceResult {
    private double distance;
    private int duration;
    private String mode;
    private String status;
}
