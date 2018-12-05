package com.cit.services.validation.rules;

import com.cit.models.Event;
import com.cit.models.Location;

import java.sql.Timestamp;

/**
 *  Used by rules engine to calculate distance and time between events
 */
public class PanelDistanceCalculator {

    private PanelDistanceCalculator() {
        throw new IllegalStateException("Utility class");
    }

    // Average walk speed of 1.4 mtrs per second = AVERAGE_WALK_SPEED_MTRS_PER_SECOND
    public static final double AVERAGE_WALK_SPEED_MTRS_PER_SECOND = 1.4;

    /**
     *
     * @param current   Current Access Event
     * @param previous  Prevous Access Event
     * @return distance in Mtrs between the two event's locations
     */
    public static double distanceInMtrsBetweenTwoEvents(Event current, Event previous) {

        double lat1 = current.getLocation().getCoordinates().getLatitude();
        double lon1 = current.getLocation().getCoordinates().getLongitude();
        double el1 = current.getLocation().getAltitude();

        double lat2 = previous.getLocation().getCoordinates().getLatitude();
        double lon2 = previous.getLocation().getCoordinates().getLongitude();
        double el2 = previous.getLocation().getAltitude();

        return PanelDistanceCalculator.distance(lat1,lat2,lon1,lon2,el1,el2);
    }

    /**
     *
     * @param current   Current Access Event
     * @param previous  Prevous Access Event
     * @return distance in Mtrs between the two event's locations
     */
    public static double distanceInMtrsBetweenTwoLocations(Location current, Location previous) {

        double lat1 = current.getCoordinates().getLatitude();
        double lon1 = current.getCoordinates().getLongitude();
        double el1 = current.getAltitude();

        double lat2 = previous.getCoordinates().getLatitude();
        double lon2 = previous.getCoordinates().getLongitude();
        double el2 = previous.getAltitude();

        return PanelDistanceCalculator.distance(lat1,lat2,lon1,lon2,el1,el2);
    }


    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns DistanceResult in Meters
     */
    private static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {
        final int R = 6371; // Radius of the earth
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        double height = el1 - el2;
        distance = Math.pow(distance, 2) + Math.pow(height, 2);
        return Math.sqrt(distance);
    }


    /**
     * Used to calculate the time in milli seconds between two timestamps
     * @param t1 first timestamp
     * @param t2 second timestamp
     * @return time in milli seconds between two timestamps
     */
    public static long timeInMiliSecondsBetweenTimeStamps(long t1, long t2) {

        java.util.Date date1 = new java.util.Date(t1);
        Timestamp timestamp1 = new Timestamp(date1.getTime());

        java.util.Date date2 = new java.util.Date(t2);
        Timestamp timestamp2 = new Timestamp(date2.getTime());

        // get time difference in seconds
        return timestamp2.getTime() - timestamp1.getTime();

    }

    /**
     * Used to calculate the time in seconds between two timestamps
     * @param t1 first timestamp
     * @param t2 second timestamp
     * @return time in seconds between two timestamps
     */
    public static int timeInSecondsBetweenTimeStamps(long t1, long t2) {

        // get time difference in seconds
        long milliseconds = timeInMiliSecondsBetweenTimeStamps(t1, t2);

        return (int) milliseconds / 1000;
    }

}
