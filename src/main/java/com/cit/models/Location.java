package com.cit.models;


import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@lombok.Data
@lombok.Builder
@Embeddable
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Location {

    // GPS coordinates of panel
    @Embedded
    private GPSCoordinate coordinates;

    // altitude above sea level
    private int altitude;

    // the 'relative' location of the panel in free form text
    private String relativeLocation;
}
