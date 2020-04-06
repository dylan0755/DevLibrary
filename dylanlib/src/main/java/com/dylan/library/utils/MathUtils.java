package com.dylan.library.utils;

import android.graphics.PointF;

import java.math.BigDecimal;

/**
 * Author: Dylan
 * Date: 2019/7/26
 * Desc:
 */

public class MathUtils {

    /**
     *  直线方程   y=ax+b   由起点和终点两个点确定a与b，
     *  代入直线上任意一点，前提是知道该点的x坐标或y坐标
     *
     * @param startPointF  起点坐标
     * @param endPointF    终点坐标
     * @param innerPoint   线上任意一点坐标
     * @return
     */
    public static PointF getInnerPointX(PointF startPointF, PointF endPointF, PointF innerPoint){
        float deltaX=endPointF.x-startPointF.x;
        float deltaY=endPointF.y-startPointF.y;
        innerPoint.x=(innerPoint.y-startPointF.y+(deltaY/deltaX)*startPointF.x)/(deltaY/deltaX);
        return innerPoint;
    }

    public static PointF getInnerPointY(PointF startPointF, PointF endPointF, PointF innerPoint){
        float deltaX=endPointF.x-startPointF.x;
        float deltaY=endPointF.y-startPointF.y;
        innerPoint.y=(deltaY/deltaX)*innerPoint.x+startPointF.y-(deltaY/deltaX)*startPointF.x;
        return innerPoint;
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

    public static String rvZeroAndDot(double d){
        String s = Double.toString(d);
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

    //加
    public static double add(double v1,double v2){
        BigDecimal b1=new BigDecimal(Double.toString(v1));
        BigDecimal b2=new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    public static double add(String v1,String v2){
        if (v1==null||v1.isEmpty())v1="0";
        if (v2==null||v2.isEmpty())v2="0";
        BigDecimal b1=new BigDecimal(v1);
        BigDecimal b2=new BigDecimal(v2);
        return b1.add(b2).doubleValue();
    }

    //减
    public static double subtract(double v1,double v2){
        BigDecimal b1=new BigDecimal(Double.toString(v1));
        BigDecimal b2=new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    public static double subtract(String v1,String v2){
        if (v1==null||v1.isEmpty())v1="0";
        if (v2==null||v2.isEmpty())v2="0";
        BigDecimal b1=new BigDecimal(v1);
        BigDecimal b2=new BigDecimal(v2);
        return b1.subtract(b2).doubleValue();
    }

    //乘
    public static double multiply(double v1,double v2){
        BigDecimal b1=new BigDecimal(Double.toString(v1));
        BigDecimal b2=new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    public static double multiply(String v1,String v2){
        if (v1==null||v1.isEmpty())v1="0";
        if (v2==null||v2.isEmpty())v2="0";
        BigDecimal b1=new BigDecimal(v1);
        BigDecimal b2=new BigDecimal(v2);
        return b1.multiply(b2).doubleValue();
    }




    /**
     *
     * @param v1
     * @param v2
     * @param scale 保留几位小数
     * @return
     */
    public static double divide(double v1,double v2,int scale){
        if (scale<0){
            scale=2;
        }
        BigDecimal b1=new BigDecimal(Double.toString(v1));
        BigDecimal b2=new BigDecimal(Double.toString(v2));
        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double divide(String v1,String v2,int scale){
        if (v1==null||v1.isEmpty())v1="0";
        if (v2==null||v2.isEmpty())v2="0";
        if (scale<0){
            scale=2;
        }
        BigDecimal b1=new BigDecimal(v1);
        BigDecimal b2=new BigDecimal(v2);
        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }


}
