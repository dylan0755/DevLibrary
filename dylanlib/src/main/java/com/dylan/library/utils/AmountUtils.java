package com.dylan.library.utils;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Dylan
 * Date: 2018/7/27
 * Desc:
 */

public class AmountUtils {

    static DecimalFormat format = new DecimalFormat("0.00");

    public static String formatTwoDecimal(double money) {
        String amount = format.format(money);
        return rvZeroAndDot(amount);
    }

    public static String formatSixDecimal(Double value) {
        if (value != null) {
            if (value.doubleValue() != 0.00) {
                DecimalFormat df = new DecimalFormat("###############0.000000");
                return rvZeroAndDot(df.format(value.doubleValue()));
            } else {
                return "0";
            }
        }
        return "";
    }

    public static String formatFourDecimal(Double value) {
        if (value != null) {
            if (value.doubleValue() != 0.00) {
                DecimalFormat df = new DecimalFormat("###############0.0000");
                return rvZeroAndDot(df.format(value.doubleValue()));
            } else {
                return "0";
            }
        }
        return "";
    }




    public static String rvZeroAndDot(String s) {
        if (s==null||s.isEmpty()) {
            return "";
        }
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

    public static String rvZerotoDouble(double d){
        String s = Double.toString(d);
        if (s.isEmpty()) {
            return null;
        }
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

    public static boolean isOnlyPointNumber(String number) {//保留6位小数正则
        Pattern pattern = Pattern.compile("^\\d+\\.?\\d{0,6}$");
        Matcher matcher = pattern.matcher(number);
        return matcher.matches();
    }

    //位数过长会使用科学计数法，让科学计数法正常显示
    public static String formatFloatNumber(Double value) {
        if (value != null) {
            if (value.doubleValue() != 0.00) {
                DecimalFormat df = new DecimalFormat("###############0.00");
                return rvZeroAndDot(df.format(value.doubleValue()));
            } else {
                return "0";
            }
        }
        return "";
    }


}
