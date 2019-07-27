package com.dylan.mylibrary.ui.onlinepic;

import android.graphics.PointF;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Author: Dylan
 * Date: 2019/7/27
 * Desc:
 */

public class ClickViewPoint implements Serializable {
    private ArrayList<Point> pointList;

    public ArrayList<Point> getPointList() {
        return pointList;
    }

    public void setPointList(ArrayList<Point> pointList) {
        this.pointList = pointList;
    }




    public static class Point implements Serializable{
        public float x;
        public float y;
        public Point(){
        }
    }
}
