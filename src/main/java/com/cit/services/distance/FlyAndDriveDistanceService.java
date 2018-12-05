package com.cit.services.distance;

import com.cit.models.DistanceResult;
import com.cit.models.Location;
import com.cit.services.validation.rules.PanelDistanceCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FlyAndDriveDistanceService implements IDistanceService {

    // Average of 200 mtrs per second fly drive speed in Meters per second
    public static final double AVERAGE_FLY_DRIVE_SPEED_MTRS_PER_SECOND = 200.0;

    public FlyAndDriveDistanceService() {
        // No action required, just adding default constructor
    }

    public DistanceResult execute(Location current, Location previous, Mode mode ) {

        DistanceResult distanceResult;

        // assume different country

        double dis = getTravelDistanceInMtrs(current, previous);
        int dur = getTravelAvarageFlyDurationBetweenTwoLocations(dis);

        distanceResult = DistanceResult.builder()
                .distance(dis)
                .duration(dur)
                .mode(mode.toString())
                .status("OK")
                .build();

        return distanceResult;
    }

    /**
     * Calculate Average Fly Drive Time in seconds between two locations
     * Time = DistanceResult / Speed;
     * assume average of 200 mtrs per second
     * @param distance DistanceResult to travel in Mtrs
     * @return Average fly Time in seconds between two locations
     */
    private int getTravelAvarageFlyDurationBetweenTwoLocations(double distance) {
        return (int)(distance / AVERAGE_FLY_DRIVE_SPEED_MTRS_PER_SECOND);
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



