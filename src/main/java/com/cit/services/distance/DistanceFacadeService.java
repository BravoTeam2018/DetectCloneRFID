package com.cit.services.distance;

import com.cit.models.DistanceResult;
import com.cit.models.Location;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class DistanceFacadeService implements IDistanceService {

    // cache all distance calculations in a map
    private Map<String,DistanceResult> distanceMap = new HashMap<>();

    private GoogleDistanceService googleDistanceService;
    private LocalDistanceService localDistanceService;
    private FlyAndDriveDistanceService flyAndDriveDistanceService;

    @Autowired
    public DistanceFacadeService(GoogleDistanceService googleDistanceService, LocalDistanceService localDistanceService, FlyAndDriveDistanceService flyAndDriveDistanceService) {
        this.googleDistanceService = googleDistanceService;
        this.localDistanceService = localDistanceService;
        this.flyAndDriveDistanceService = flyAndDriveDistanceService;
    }

    @Override
    public DistanceResult execute(Location current, Location previous, Mode mode) {
        DistanceResult distanceResult;

        String hash = toStringHash(current,previous,mode);

        // check have we done this calculation before
        distanceResult = distanceMap.get(hash);

        if(distanceResult==null) {

            if (mode == Mode.DRIVING) {
                distanceResult = googleDistanceService.execute(current, previous, mode);
            } else if (mode == Mode.WALKING || mode == Mode.WALK_AND_ELAVOTOR) {
                distanceResult = localDistanceService.execute(current, previous, mode);
            } else {
                distanceResult = flyAndDriveDistanceService.execute(current, previous, mode);
            }

        }

        // store in the cache so we don't have to do the calculation again
        distanceMap.put(hash,distanceResult);

        return distanceResult;
    }

    /**
     * Create a hash to we can look up previous calculation without having to redo
     * @param current
     * @param previous
     * @param mode
     * @return
     */
    private String toStringHash(Location current, Location previous, Mode mode) {
        return String.format("%s-$s-$s",current,previous,mode);
    }

}
