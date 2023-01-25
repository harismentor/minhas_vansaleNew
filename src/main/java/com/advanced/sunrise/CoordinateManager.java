package com.advanced.minhas;

public class CoordinateManager {

    // declare public constants

    /**
     * The minimum allowed latitude
     */
    public static float MIN_LATITUDE = Float.valueOf("-90.0000");

    /**
     * The maximum allowed latitude
     */
    public static float MAX_LATITUDE = Float.valueOf("90.0000");

    /**
     * The minimum allowed longitude
     */
    public static float MIN_LONGITUDE = Float.valueOf("-180.0000");

    /**
     * The maximum allowed longitude
     */
    public static float MAX_LONGITUDE = Float.valueOf("180.0000");

    /**
     * The diameter of the Earth used in calculations
     */
    public static float EARTH_DIAMETER = Float.valueOf("12756.274");

    /**
     * A method to validate a latitude value
     *
     * @param latitude the latitude to check is valid
     *
     * @return         true if, and only if, the latitude is within the MIN and MAX latitude
     */
    public static boolean isValidLatitude(float latitude) {
        if(latitude >= MIN_LATITUDE && latitude <= MAX_LATITUDE) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * A method to validate a longitude value
     *
     * @param longitude the longitude to check is valid
     *
     * @return          true if, and only if, the longitude is between the MIN and MAX longitude
     */
    public static boolean isValidLongitude(float longitude) {
        if(longitude >= MIN_LONGITUDE && longitude <= MAX_LONGITUDE) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * A private method to calculate the latitude constant
     *
     * @return a double representing the latitude constant
     */
    public static double latitudeConstant() {
        return EARTH_DIAMETER * (Math.PI / Float.valueOf("360"));
        //return EARTH_DIAMETER * (Float.valueOf("3.14") / Float.valueOf("360"));
    }

}
