package com.dylan.mylibrary.util;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Administrator
 * Date: 2020/10/12
 * Desc: 各场景坐标
 */
public class ScenePointHelper {
    public static final int plaintHolderWidth = 270;
    public static final int plaintHolderHeight = 183;

    private List<Point> chickenZonePointFs;//鸡圈
    private List<Point> chickensPoints;
    private List<Point> plaintHolderLeftTopPoints;//种植占位图
    private List<Point> plaintLeftTopPoints;//树的坐标
    private List<Point> fishPoints;


    public ScenePointHelper() {
        chickenZonePointFs = new ArrayList<>();
        Point leftTop = new Point(2421, 1793);
        Point rightTop = new Point(3048, 1383);
        Point rightBottom = new Point(3483, 1682);
        Point leftBottom = new Point(2832, 1999);
        //按四边形顺序
        chickenZonePointFs.add(leftTop);
        chickenZonePointFs.add(rightTop);
        chickenZonePointFs.add(rightBottom);
        chickenZonePointFs.add(leftBottom);


        //小鸡的坐标
        chickensPoints = new ArrayList<>();
        chickensPoints.add(new Point(2472, 1747));
        chickensPoints.add(new Point(2687, 1670));
        chickensPoints.add(new Point(2923, 1586));
        chickensPoints.add(new Point(2744, 1410));
        chickensPoints.add(new Point(3057, 1334));


        //6个种植区域占位图的坐标
        plaintHolderLeftTopPoints = new ArrayList<>();
        plaintHolderLeftTopPoints.add(new Point(3016, 2140));
        plaintHolderLeftTopPoints.add(new Point(3341, 1969));
        plaintHolderLeftTopPoints.add(new Point(3662, 1833));
        plaintHolderLeftTopPoints.add(new Point(3330, 2292));
        plaintHolderLeftTopPoints.add(new Point(3655, 2141));
        plaintHolderLeftTopPoints.add(new Point(3974, 1980));

        //6棵树的坐标
        plaintLeftTopPoints = new ArrayList<>();
        plaintLeftTopPoints.add(new Point(3016, 2040));
        plaintLeftTopPoints.add(new Point(3341, 1869));
        plaintLeftTopPoints.add(new Point(3662, 1733));
        plaintLeftTopPoints.add(new Point(3330, 2192));
        plaintLeftTopPoints.add(new Point(3655, 2041));
        plaintLeftTopPoints.add(new Point(3974, 1880));

        //鱼
        fishPoints = new ArrayList<>();
        fishPoints.add(new Point(1830, 2088));
        fishPoints.add(new Point(1698, 2172));
        fishPoints.add(new Point(2064, 2157));

    }


    public List<Point> getChickenZoneRectBound() {
        return chickenZonePointFs;
    }


    public List<Point> getChickensPoints() {
        return chickensPoints;
    }

    public List<Point> getFishPoints() {
        return fishPoints;
    }

    //6个种植占位图的左上角起点坐标
    public List<Point> getPlaintHolderLeftTopPoints() {
        return plaintHolderLeftTopPoints;
    }


    //6棵树的左上角起点坐标
    public List<Point> getPlaintLeftTopPoints() {
        return plaintLeftTopPoints;
    }


    //某棵树
    public List<Point> getPlaintHolderRectBound(int index) {
        List<Point> list = new ArrayList<>();
        Point startPoint = plaintHolderLeftTopPoints.get(index);
        list.add(startPoint);//左上角
        list.add(new Point(startPoint.x + plaintHolderWidth, startPoint.y));//右上角
        list.add(new Point(startPoint.x + plaintHolderWidth, startPoint.y + plaintHolderHeight));//右下角
        list.add(new Point(startPoint.x, startPoint.y + plaintHolderHeight));//左下角
        return list;
    }


    //某只鸡
    public List<Point> getChickenRectBound(int index) {
        if (index > chickensPoints.size() - 1) return new ArrayList<>();
        List<Point> list = new ArrayList<>();
        Point startPoint = chickensPoints.get(index);
        int width, height;
        if (index == 0) {
            width = 188;
            height = 170;
        } else if (index == 1) {
            width = 188;
            height = 167;
        } else if (index == 2) {
            width = 188;
            height = 173;
        } else if (index == 3) {
            width = 189;
            height = 168;
        } else {
            width = 188;
            height = 203;
        }
        list.add(startPoint);//左上角
        list.add(new Point(startPoint.x + width, startPoint.y));//右上角
        list.add(new Point(startPoint.x + width, startPoint.y + height));//右下角
        list.add(new Point(startPoint.x, startPoint.y + height));//左下角
        return list;
    }

    //某只鱼
    public List<Point> getFishRectBound(int index) {
        if (index > fishPoints.size() - 1) return new ArrayList<>();
        List<Point> list = new ArrayList<>();
        Point startPoint = fishPoints.get(index);
        int width, height;
        if (index == 0) {
            width = 167;
            height = 85;
        } else if (index == 1) {
            width = 221;
            height = 107;
        } else {
            width = 188;
            height = 156;
        }
        list.add(startPoint);//左上角
        list.add(new Point(startPoint.x + width, startPoint.y));//右上角
        list.add(new Point(startPoint.x + width, startPoint.y + height));//右下角
        list.add(new Point(startPoint.x, startPoint.y + height));//左下角
        return list;
    }


    //批发市场
    public List<Point> getMarketRectBound() {
        List<Point> list = new ArrayList<>();
        list.add(new Point(924, 1255));//左上角
        list.add(new Point(1339, 1029));//右上角
        list.add(new Point(1773, 1309));//右下角
        list.add(new Point(1241, 1611));//左下角
        return list;
    }

    //商店
    public List<Point> getShopRectBound() {
        List<Point> list = new ArrayList<>();
        list.add(new Point(1841, 781));
        list.add(new Point(2108, 655));
        list.add(new Point(2340, 774));
        list.add(new Point(2395, 997));

        list.add(new Point(2392, 1075));
        list.add(new Point(2083, 1197));
        list.add(new Point(1865, 1082));
        return list;
    }

    //仓库
    public List<Point> getWareHouseRectBound() {
        List<Point> list = new ArrayList<>();
        list.add(new Point(3719, 1415));
        list.add(new Point(3788, 1041));
        list.add(new Point(4091, 890));
        list.add(new Point(4408, 1258));
        list.add(new Point(4082, 1531));
        return list;
    }

    //鱼塘
    public List<Point> getFishpondRectBound() {
        List<Point> list = new ArrayList<>();
        list.add(new Point(1572, 1930));
        list.add(new Point(1635, 2385));
        list.add(new Point(2042, 2444));
        list.add(new Point(2126, 2374));
        list.add(new Point(2524, 2488));
        list.add(new Point(2669, 2291));
        list.add(new Point(2658, 2288));
        list.add(new Point(1809, 1938));
        return list;
    }


    public void clear() {
        chickenZonePointFs.clear();
        chickensPoints.clear();
        plaintHolderLeftTopPoints.clear();
        plaintLeftTopPoints.clear();
        fishPoints.clear();
    }

}
