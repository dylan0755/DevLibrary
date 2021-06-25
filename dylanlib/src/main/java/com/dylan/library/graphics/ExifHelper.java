package com.dylan.library.graphics;

import android.media.ExifInterface;

import java.io.IOException;

/**
 * Author: Dylan
 * Date: 2020/2/27
 * Desc:
 */
public class ExifHelper {


    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }



    public static Exif getExifInfo(String path){
        Exif exif=new Exif();
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
            String dateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
            String make = exifInterface.getAttribute(ExifInterface.TAG_MAKE);
            String model = exifInterface.getAttribute(ExifInterface.TAG_MODEL);
            String flash = exifInterface.getAttribute(ExifInterface.TAG_FLASH);
            String imageLength = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
            String imageWidth = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
            String latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            String latitudeRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String longitudeRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            String exposureTime = exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
            String aperture = exifInterface.getAttribute(ExifInterface.TAG_APERTURE);
            String isoSpeedRatings = exifInterface.getAttribute(ExifInterface.TAG_ISO);
            String dateTimeDigitized = exifInterface.getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED);
            String subSecTime = exifInterface.getAttribute(ExifInterface.TAG_SUBSEC_TIME);
            String subSecTimeOrig = exifInterface.getAttribute(ExifInterface.TAG_SUBSEC_TIME_ORIG);
            String subSecTimeDig = exifInterface.getAttribute(ExifInterface.TAG_SUBSEC_TIME_DIG);
            String altitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_ALTITUDE);
            String altitudeRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF);
            String gpsTimeStamp = exifInterface.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP);
            String gpsDateStamp = exifInterface.getAttribute(ExifInterface.TAG_GPS_DATESTAMP);
            String whiteBalance = exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
            String focalLength = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
            String processingMethod = exifInterface.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD);
            exif.setOrientation(orientation);
            exif.setDateTime(dateTime);
            exif.setMake(make);
            exif.setModel(model);
            exif.setFlash(flash);
            exif.setImageLength(imageLength);
            exif.setImageWidth(imageWidth);
            exif.setLatitude(latitude);
            exif.setLongitude(longitude);
            exif.setLatitudeRef(latitudeRef);
            exif.setLongitudeRef(longitudeRef);
            exif.setExposureTime(exposureTime);
            exif.setAperture(aperture);
            exif.setIsoSpeedRatings(isoSpeedRatings);
            exif.setDateTimeDigitized(dateTimeDigitized);
            exif.setSubSecTime(subSecTime);
            exif.setSubSecTimeOrig(subSecTimeOrig);
            exif.setSubSecTimeDig(subSecTimeDig);
            exif.setAltitude(altitude);
            exif.setAltitudeRef(altitudeRef);
            exif.setGpsTimeStamp(gpsTimeStamp);
            exif.setGpsDateStamp(gpsDateStamp);
            exif.setWhiteBalance(whiteBalance);
            exif.setFocalLength(focalLength);
            exif.setProcessingMethod(processingMethod);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exif;
    }



}
