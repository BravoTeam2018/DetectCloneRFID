package com.cit.models;

import javax.persistence.Embeddable;

@lombok.Data
@lombok.Builder
@Embeddable
public class GPSCoordinate
{

    // GPS latitude, in decimal format
    private double latitude;

    // GPS longitude, in decimal format
    private double longitude;

}