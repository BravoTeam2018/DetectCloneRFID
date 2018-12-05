package com.cit.services.distance;

import com.cit.models.DistanceResult;
import com.cit.models.Location;

public interface IDistanceService {

    enum Mode{WALKING, DRIVING, FLY_DRIVE}
    DistanceResult execute(Location current, Location previous, Mode mode );
}
