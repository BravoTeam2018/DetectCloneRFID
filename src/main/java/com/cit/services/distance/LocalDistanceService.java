package com.cit.services.distance;

import com.cit.models.DistanceResult;
import com.cit.models.Location;
import com.cit.services.validation.rules.PanelDistanceCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LocalDistanceService implements IDistanceService {


    public static final double AVERAGE_WALLING_SPEED_MTRS_PER_SECOND = 1.4;

    public LocalDistanceService() {
        // No action required, just adding default constructor
    }

    public DistanceResult execute(Location current, Location previous, Mode mode ) {

        DistanceResult distanceResult;

        double dis = getTravelDistanceInMtrs(current, previous);
        int dur = getTravelAvarageWalkDurationBetweenTwoLocations(dis);

        distanceResult = DistanceResult.builder()
                .distance(dis)
                .duration(dur)
                .mode(mode.toString())
                .status("OK")
                .build();

        return distanceResult;
    }

    /**
     * Calculate Average walk Time in seconds between two locations
     * Time = DistanceResult / Speed;
     * assume average of 1.4 mtrs per second
     * @param distance DistanceResult to travel in Mtrs
     * @return Average walk Time in seconds between two locations
     */
    private int getTravelAvarageWalkDurationBetweenTwoLocations(double distance) {
        return (int)(distance / AVERAGE_WALLING_SPEED_MTRS_PER_SECOND);
    }

    /**
     * Calculate distance in meters between GPS point A and GPS point B = traveldistanceMtrsBetweenGPSPoints
     * @return distance in Mtrs
     */
    private int getTravelDistanceInMtrs(Location current, Location previous) {
        double travelDistanceMtrsBetweenGPSPoints  = PanelDistanceCalculator.distanceInMtrsBetweenTwoLocations(current, previous);
        if (log.isDebugEnabled()) {
            log.debug("Travel distance in Mtr={} \n CurrentLocation={} \n PreviousLocation = {}", travelDistanceMtrsBetweenGPSPoints, current, previous );
        }
        return (int)travelDistanceMtrsBetweenGPSPoints;
    }


}



