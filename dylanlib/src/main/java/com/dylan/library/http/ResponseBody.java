package com.dylan.library.http;

import com.dylan.library.proguard.NotProguard;

import java.math.BigDecimal;

@NotProguard
public class ResponseBody {
    public int status;
    public String duration;
    public String byteSize;
    public String result;

    @Override
    public String toString() {
        return "ResponseBody{" +
                "status=" + status +
                ", duration='" + duration + '\'' +
                ", byteSize='" + byteSize + '\'' +
                ", result='" + result + '\'' +
                '}';
    }


    public static String getFormatFileSize(long size) {
        double kiloByte = (double)size * 1.0 / 1024.0;
        if (kiloByte < 1.0) {
            return "0 B";
        } else {
            double megaByte = kiloByte / 1024.0;
            if (megaByte < 1.0) {
                BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
                return result1.setScale(2, 4).toPlainString() + " KB";
            } else {
                double gigaByte = megaByte / 1024.0;
                if (gigaByte < 1.0) {
                    BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
                    return result2.setScale(2, 4).toPlainString() + " MB";
                } else {
                    double teraBytes = gigaByte / 1024.0;
                    BigDecimal result4;
                    if (teraBytes < 1.0) {
                        result4 = new BigDecimal(Double.toString(gigaByte));
                        return result4.setScale(2, 4).toPlainString() + " GB";
                    } else {
                        result4 = new BigDecimal(teraBytes);
                        return result4.setScale(2, 4).toPlainString() + " TB";
                    }
                }
            }
        }
    }
}
