package com.dylan.library.graphics;

/**
 * Author: Dylan
 * Date: 2020/2/27
 * Desc:
 */
public class Exif {

    private int orientation;
    private String dateTime;
    private String make;
    private String model;
    private String flash;
    private String imageLength;
    private String imageWidth;
    private String latitude;
    private String longitude;
    private String latitudeRef;
    private String longitudeRef;
    private String exposureTime;
    private String aperture;
    private String isoSpeedRatings;
    private String dateTimeDigitized;
    private String subSecTime;
    private String subSecTimeOrig;
    private String subSecTimeDig;
    private String altitude;
    private String altitudeRef;
    private String gpsTimeStamp;
    private String gpsDateStamp;
    private String whiteBalance;
    private String focalLength;
    private String processingMethod;

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getFlash() {
        return flash;
    }

    public void setFlash(String flash) {
        this.flash = flash;
    }

    public String getImageLength() {
        return imageLength;
    }

    public void setImageLength(String imageLength) {
        this.imageLength = imageLength;
    }

    public String getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(String imageWidth) {
        this.imageWidth = imageWidth;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitudeRef() {
        return latitudeRef;
    }

    public void setLatitudeRef(String latitudeRef) {
        this.latitudeRef = latitudeRef;
    }

    public String getLongitudeRef() {
        return longitudeRef;
    }

    public void setLongitudeRef(String longitudeRef) {
        this.longitudeRef = longitudeRef;
    }

    public String getExposureTime() {
        return exposureTime;
    }

    public void setExposureTime(String exposureTime) {
        this.exposureTime = exposureTime;
    }

    public String getAperture() {
        return aperture;
    }

    public void setAperture(String aperture) {
        this.aperture = aperture;
    }

    public String getIsoSpeedRatings() {
        return isoSpeedRatings;
    }

    public void setIsoSpeedRatings(String isoSpeedRatings) {
        this.isoSpeedRatings = isoSpeedRatings;
    }

    public String getDateTimeDigitized() {
        return dateTimeDigitized;
    }

    public void setDateTimeDigitized(String dateTimeDigitized) {
        this.dateTimeDigitized = dateTimeDigitized;
    }

    public String getSubSecTime() {
        return subSecTime;
    }

    public void setSubSecTime(String subSecTime) {
        this.subSecTime = subSecTime;
    }

    public String getSubSecTimeOrig() {
        return subSecTimeOrig;
    }

    public void setSubSecTimeOrig(String subSecTimeOrig) {
        this.subSecTimeOrig = subSecTimeOrig;
    }

    public String getSubSecTimeDig() {
        return subSecTimeDig;
    }

    public void setSubSecTimeDig(String subSecTimeDig) {
        this.subSecTimeDig = subSecTimeDig;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getAltitudeRef() {
        return altitudeRef;
    }

    public void setAltitudeRef(String altitudeRef) {
        this.altitudeRef = altitudeRef;
    }

    public String getGpsTimeStamp() {
        return gpsTimeStamp;
    }

    public void setGpsTimeStamp(String gpsTimeStamp) {
        this.gpsTimeStamp = gpsTimeStamp;
    }

    public String getGpsDateStamp() {
        return gpsDateStamp;
    }

    public void setGpsDateStamp(String gpsDateStamp) {
        this.gpsDateStamp = gpsDateStamp;
    }

    public String getWhiteBalance() {
        return whiteBalance;
    }

    public void setWhiteBalance(String whiteBalance) {
        this.whiteBalance = whiteBalance;
    }

    public String getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(String focalLength) {
        this.focalLength = focalLength;
    }

    public String getProcessingMethod() {
        return processingMethod;
    }

    public void setProcessingMethod(String processingMethod) {
        this.processingMethod = processingMethod;
    }


    @Override
    public String toString() {
        return "Exif{" +
                "orientation=" + orientation +
                ", dateTime='" + dateTime + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", flash='" + flash + '\'' +
                ", imageLength='" + imageLength + '\'' +
                ", imageWidth='" + imageWidth + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitudeRef='" + latitudeRef + '\'' +
                ", longitudeRef='" + longitudeRef + '\'' +
                ", exposureTime='" + exposureTime + '\'' +
                ", aperture='" + aperture + '\'' +
                ", isoSpeedRatings='" + isoSpeedRatings + '\'' +
                ", dateTimeDigitized='" + dateTimeDigitized + '\'' +
                ", subSecTime='" + subSecTime + '\'' +
                ", subSecTimeOrig='" + subSecTimeOrig + '\'' +
                ", subSecTimeDig='" + subSecTimeDig + '\'' +
                ", altitude='" + altitude + '\'' +
                ", altitudeRef='" + altitudeRef + '\'' +
                ", gpsTimeStamp='" + gpsTimeStamp + '\'' +
                ", gpsDateStamp='" + gpsDateStamp + '\'' +
                ", whiteBalance='" + whiteBalance + '\'' +
                ", focalLength='" + focalLength + '\'' +
                ", processingMethod='" + processingMethod + '\'' +
                '}';
    }
}
