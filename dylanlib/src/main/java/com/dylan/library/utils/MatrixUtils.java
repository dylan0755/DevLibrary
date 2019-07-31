package com.dylan.library.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Author: Dylan
 * Date: 2019/7/31
 * Desc:
 */

public class MatrixUtils {

    //四个顶点的坐标
    public static PointF[] getLocation(Matrix matrix, Rect rect) {
        if (matrix == null || rect == null) return null;
        RectF rectF = new RectF(0, 0, rect.width() * 1.0f, rect.height() * 1.0f);
        PointF[] points = new PointF[4];
        float[] location = new float[9];
        matrix.getValues(location);
        //左上角
        points[0]=new PointF(location[2],location[5]) ;
        //右上角
        points[1] =new PointF(location[2] + rectF.width() * location[0],points[0].y) ;
        //左下角
        points[2] =new PointF(points[0].x,location[5] + rectF.height() * location[4]) ;
        //右下角
        points[3] =new PointF(points[1].x,points[2].y);
        return points;
    }


    //四个顶点的坐标
    public static PointF[] getLocation(Matrix matrix, Bitmap bitmap) {
        if (matrix == null || bitmap == null) return null;
        RectF rectF = new RectF(0, 0, bitmap.getWidth() * 1.0f, bitmap.getHeight() * 1.0f);
        PointF[] points = new PointF[4];
        float[] location = new float[9];
        matrix.getValues(location);
        //左上角
        points[0]=new PointF(location[2],location[5]) ;
        //右上角
        points[1] =new PointF(location[2] + rectF.width() * location[0],points[0].y) ;
        //左下角
        points[2] =new PointF(points[0].x,location[5] + rectF.height() * location[4]) ;
        //右下角
        points[3] =new PointF(points[1].x,points[2].y);
        return points;
    }

    //X,Y的缩放倍率
    public static PointF getScaleSize(Matrix matrix) {
        float[] location = new float[9];
        matrix.getValues(location);
        PointF pointF=new PointF();
        pointF.x=location[0];
        pointF.y=location[4];
        return pointF;
    }

    //左上角坐标
    public static PointF getLefTop(Matrix matrix){
        float[] location = new float[9];
        matrix.getValues(location);
        PointF pointF=new PointF();
        pointF.x=location[2];
        pointF.y=location[5];
        return pointF;
    }





}
