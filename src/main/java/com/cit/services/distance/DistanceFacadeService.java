package com.cit.services.distance;

import com.cit.models.DistanceResult;
import com.cit.models.Location;
import org.springframework.beans.factory.annotation.Autowired;

public class DistanceFacadeService implements IDistanceService {

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

        if (mode==Mode.DRIVING) {
            distanceResult = googleDistanceService.execute(current,previous,mode);
        }  else if (mode == Mode.WALKING || mode == Mode.WALK_AND_ELAVOTOR){
            distanceResult = localDistanceService.execute(current,previous,mode);
        } else {
            distanceResult = flyAndDriveDistanceService.execute(current,previous,mode);
        }

        return distanceResult;
    }


}
