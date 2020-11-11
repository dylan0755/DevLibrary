package com.dylan.library.map;

public class MapUtils {
    private static final double EARTH_RADIUS = 6378137.0;//地球半径

    //得出距离  单位米
    public static double getLatLngMeterDistance(double longitude1, double latitude1 , double longitude2, double latitude2) {
        double lat1 = rad(latitude1);
        double lat2 = rad(latitude2);
        double a = lat1 - lat2;
        double b = rad(longitude1) - rad(longitude2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

}
