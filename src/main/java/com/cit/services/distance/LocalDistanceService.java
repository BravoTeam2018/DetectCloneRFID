package com.cit.services.distance;

import com.cit.models.DistanceResult;
import com.cit.models.Location;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LocalDistanceService implements IDistanceService {

    public static final double AVERAGE_WALLING_SPEED_MTRS_PER_SECOND = 1.4;
    public static final double AVERAGE_ELEVATOR_SPEED_MTRS_PER_SECOND = 10.0;
    public static final int AVERAGE_ELEVATOR_WAIT_SECOND = 20;

    public LocalDistanceService() {
        // No action required, just adding default constructor
    }

    public DistanceResult execute(Location current, Location previous, Mode mode ) {

        DistanceResult distanceResult;

        int durationSeconds=0;
        double distanceMtrs=0.0;

        if(mode==Mode.WALKING) {

            // This calculation takes in to account distance between gps location and also includes walking between two different altitudes

            distanceMtrs  = PanelDistanceCalculator.distanceInMtrsBetweenTwoLocationsIncludingAltitude(current, previous);

            durationSeconds = (int)(distanceMtrs / AVERAGE_WALLING_SPEED_MTRS_PER_SECOND);

        } else {  // WALK_AND_ELAVOTOR

            // This calculation takes in to account walking distance between gps location
            // then using an elevator if the location altitude(s) are different

            // calculate walking duration
            distanceMtrs  = PanelDistanceCalculator.distanceInMtrsBetweenTwoLocationsExcludingAltitude(current, previous);
            durationSeconds = (int)(distanceMtrs / AVERAGE_WALLING_SPEED_MTRS_PER_SECOND);

            // take into account speed of elevator
            double altitudeDistanceMtrs =  PanelDistanceCalculator.altitudeDifferenceDistanceInMtrsBetweenTwoLocations(current, previous);
            if (altitudeDistanceMtrs > 0) {

                // add the altitude to the distance
                //distanceMtrs = distanceMtrs + altitudeDistanceMtrs;

                distanceMtrs = Math.pow(distanceMtrs, 2) + Math.pow(altitudeDistanceMtrs, 2);
                distanceMtrs = Math.sqrt(distanceMtrs);


                // add on time for getting the elevator
                durationSeconds = durationSeconds + (int)(altitudeDistanceMtrs / AVERAGE_ELEVATOR_SPEED_MTRS_PER_SECOND);

                // include average wait for elevator
                durationSeconds = durationSeconds + AVERAGE_ELEVATOR_WAIT_SECOND;
            }
        }

        log.debug("Travel distance in Mtrs={} and duration in Seconds={} \n CurrentLocation={} \n PreviousLocation = {}",distanceMtrs, durationSeconds, current, previous );

        distanceResult = DistanceResult.builder()
                .distance(distanceMtrs)
                .duration(durationSeconds)
                .mode(mode.toString())
                .status("OK")
                .build();

        return distanceResult;
    }

}



