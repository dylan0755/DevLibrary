package com.dylan.library.utils;

import android.graphics.Point;
import android.graphics.PointF;

import com.dylan.library.map.MapUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2019/7/26
 * Desc:
 */

public class MathUtils {

    /**
     * 判断点是否在多边形内
     */
    public static boolean isInPolygon(PointF point, List<PointF> pts) {

        int N = pts.size();
        boolean boundOrVertex = true;
        int intersectCount = 0;//交叉点数量
        double precision = 2e-10; //浮点类型计算时候与0比较时候的容差
        PointF p1, p2;//临近顶点
        PointF p = point; //当前点

        p1 = pts.get(0);
        for (int i = 1; i <= N; ++i) {
            if (p.equals(p1)) {
                return boundOrVertex;
            }

            p2 = pts.get(i % N);
            if (p.x < Math.min(p1.x, p2.x) || p.x > Math.max(p1.x, p2.x)) {
                p1 = p2;
                continue;
            }

            //射线穿过算法
            if (p.x > Math.min(p1.x, p2.x) && p.x < Math.max(p1.x, p2.x)) {
                if (p.y <= Math.max(p1.y, p2.y)) {
                    if (p1.x == p2.x && p.y >= Math.min(p1.y, p2.y)) {
                        return boundOrVertex;
                    }

                    if (p1.y == p2.y) {
                        if (p1.y == p.y) {
                            return boundOrVertex;
                        } else {
                            ++intersectCount;
                        }
                    } else {
                        double xinters = (p.x - p1.x) * (p2.y - p1.y) / (p2.x - p1.x) + p1.y;
                        if (Math.abs(p.y - xinters) < precision) {
                            return boundOrVertex;
                        }

                        if (p.y < xinters) {
                            ++intersectCount;
                        }
                    }
                }
            } else {
                if (p.x == p2.x && p.y <= p2.y) {
                    PointF p3 = pts.get((i + 1) % N);
                    if (p.x >= Math.min(p1.x, p3.x) && p.x <= Math.max(p1.x, p3.x)) {
                        ++intersectCount;
                    } else {
                        intersectCount += 2;
                    }
                }
            }
            p1 = p2;
        }
        if (intersectCount % 2 == 0) {//偶数在多边形外
            return false;
        } else { //奇数在多边形内
            return true;
        }
    }


    public static boolean isInPolygon(Point point, List<Point> pts) {

        int N = pts.size();
        boolean boundOrVertex = true;
        int intersectCount = 0;//交叉点数量
        double precision = 2e-10; //浮点类型计算时候与0比较时候的容差
        Point p1, p2;//临近顶点
        Point p = point; //当前点

        p1 = pts.get(0);
        for (int i = 1; i <= N; ++i) {
            if (p.equals(p1)) {
                return boundOrVertex;
            }

            p2 = pts.get(i % N);
            if (p.x < Math.min(p1.x, p2.x) || p.x > Math.max(p1.x, p2.x)) {
                p1 = p2;
                continue;
            }

            //射线穿过算法
            if (p.x > Math.min(p1.x, p2.x) && p.x < Math.max(p1.x, p2.x)) {
                if (p.y <= Math.max(p1.y, p2.y)) {
                    if (p1.x == p2.x && p.y >= Math.min(p1.y, p2.y)) {
                        return boundOrVertex;
                    }

                    if (p1.y == p2.y) {
                        if (p1.y == p.y) {
                            return boundOrVertex;
                        } else {
                            ++intersectCount;
                        }
                    } else {
                        double xinters = (p.x - p1.x) * (p2.y - p1.y) / (p2.x - p1.x) + p1.y;
                        if (Math.abs(p.y - xinters) < precision) {
                            return boundOrVertex;
                        }

                        if (p.y < xinters) {
                            ++intersectCount;
                        }
                    }
                }
            } else {
                if (p.x == p2.x && p.y <= p2.y) {
                    Point p3 = pts.get((i + 1) % N);
                    if (p.x >= Math.min(p1.x, p3.x) && p.x <= Math.max(p1.x, p3.x)) {
                        ++intersectCount;
                    } else {
                        intersectCount += 2;
                    }
                }
            }
            p1 = p2;
        }
        if (intersectCount % 2 == 0) {//偶数在多边形外
            return false;
        } else { //奇数在多边形内
            return true;
        }
    }



    //绘制多边形
    public static float[] convertPointsToLinesFloatArray(List<Point> points) {
        if (points.size() < 3) return null;
        float[] pfloats = new float[points.size() * 4];
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            pfloats[i * 4] = point.x;
            pfloats[i * 4 + 1] = point.y;
            //下一个点
            if ((i + 1) == points.size()) break;
            pfloats[i * 4 + 2] = points.get(i + 1).x;
            pfloats[i * 4 + 3] = points.get(i + 1).y;
        }
        //最后一条线
        pfloats[pfloats.length - 4] = points.get(points.size() - 1).x;
        pfloats[pfloats.length - 3] = points.get(points.size() - 1).y;
        pfloats[pfloats.length - 2] = points.get(0).x;
        pfloats[pfloats.length - 1] = points.get(0).y;
        return pfloats;
    }

    //绘制多边形
    public static float[] convertPointFsToLinesFloatArray(List<PointF> points) {
        if (points.size() < 3) return null;
        float[] pfloats = new float[points.size() * 4];
        for (int i = 0; i < points.size(); i++) {
            PointF point = points.get(i);
            pfloats[i * 4] = point.x;
            pfloats[i * 4 + 1] = point.y;
            //下一个点
            if ((i + 1) == points.size()) break;
            pfloats[i * 4 + 2] = points.get(i + 1).x;
            pfloats[i * 4 + 3] = points.get(i + 1).y;
        }
        //最后一条线
        pfloats[pfloats.length - 4] = points.get(points.size() - 1).x;
        pfloats[pfloats.length - 3] = points.get(points.size() - 1).y;
        pfloats[pfloats.length - 2] = points.get(0).x;
        pfloats[pfloats.length - 1] = points.get(0).y;
        return pfloats;
    }

    private static List<PointF> convertFloatArrayToPoints(float[] points) {
        if (points.length % 2 != 0) return new ArrayList<>();
        List<PointF> pointList = new ArrayList<>(points.length / 2);
        for (int i = 0; i < points.length; i += 2) {
            PointF point = new PointF(points[i], points[i + 1]);
            pointList.add(point);
        }
        return pointList;
    }








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



    //得出距离  单位米
    public static double getLatLngMeterDistance(double longitude1, double latitude1 , double longitude2, double latitude2) {
        return MapUtils.getLatLngMeterDistance(longitude1,latitude1,longitude2,latitude2);
    }



}
