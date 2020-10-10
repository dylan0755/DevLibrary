package com.dylan.library.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Author: Dylan
 * Date: 2019/7/31
 * Desc:
 */

public class MatrixUtils {

    //获取ImageView 点击时在Bitmap的坐标
    public static PointF getBitmapClickPointF(ImageView iv, MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        // 目标点的坐标
        float dst[] = new float[2];
        // 获取到ImageView的matrix
        Matrix imageMatrix = iv.getImageMatrix();
        // 创建一个逆矩阵
        Matrix inverseMatrix = new Matrix();
        // 求逆，逆矩阵被赋值
        imageMatrix.invert(inverseMatrix);
        // 通过逆矩阵映射得到目标点 dst 的值
        inverseMatrix.mapPoints(dst, new float[]{x, y});
        PointF pointF = new PointF();
        pointF.set(dst[0], dst[1]);
        return pointF;
    }






    //四个顶点的坐标
    public static PointF[] getLocation(Matrix matrix, Rect rect) {
        if (matrix == null || rect == null) return null;
        RectF rectF = new RectF(0, 0, rect.width() * 1.0f, rect.height() * 1.0f);
        PointF[] points = new PointF[4];
        float[] location = new float[9];
        matrix.getValues(location);
        //左上角
        points[0] = new PointF(location[2], location[5]);
        //右上角
        points[1] = new PointF(location[2] + rectF.width() * location[0], points[0].y);
        //左下角
        points[2] = new PointF(points[0].x, location[5] + rectF.height() * location[4]);
        //右下角
        points[3] = new PointF(points[1].x, points[2].y);
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
        points[0] = new PointF(location[2], location[5]);
        //右上角
        points[1] = new PointF(location[2] + rectF.width() * location[0], points[0].y);
        //左下角
        points[2] = new PointF(points[0].x, location[5] + rectF.height() * location[4]);
        //右下角
        points[3] = new PointF(points[1].x, points[2].y);
        return points;
    }

    //X,Y的缩放倍率
    public static PointF getScaleSize(Matrix matrix) {
        float[] location = new float[9];
        matrix.getValues(location);
        PointF pointF = new PointF();
        pointF.x = location[0];
        pointF.y = location[4];
        return pointF;
    }

    //左上角坐标
    public static PointF getLefTop(Matrix matrix) {
        float[] location = new float[9];
        matrix.getValues(location);
        PointF pointF = new PointF();
        pointF.x = location[2];
        pointF.y = location[5];
        return pointF;
    }

    public static Rect getMatrixRect(Matrix matrix, int width, int height) {
        float[] location = new float[9];
        matrix.getValues(location);
        float sx = location[0];
        float sy = location[4];

        int cw = (int) (width * sx);
        int ch = (int) (height * sy);

        float leftX = location[2];
        float leftY = location[5];

        return new Rect((int) leftX, (int) leftY, (int) (cw + leftX), (int) (ch + leftY));
    }


    /**
     * 获取ImageView 当前图片的位置信息
     */
    public static Rect getMatrixRect(Matrix matrix, Bitmap bitmap) {
        int dw = bitmap.getWidth();
        int dh = bitmap.getHeight();
        float[] location = new float[9];
        matrix.getValues(location);
        float sx = location[0];
        float sy = location[4];

        int cw = (int) (dw * sx);
        int ch = (int) (dh * sy);

        float leftX = location[2];
        float leftY = location[5];

        return new Rect((int) leftX, (int) leftY, (int) (cw + leftX), (int) (ch + leftY));
    }

    /**
     * 获取ImageView 当前图片的位置信息
     */
    public static RectF getMatrixRectF(ImageView imageView) {
        RectF rectF = new RectF();
        Drawable d = imageView.getDrawable();
        if (d != null) {
            rectF.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            imageView.getImageMatrix().mapRect(rectF);
        }
        return rectF;
    }

    //缩小至最初的展示大小  即根据图片 宽高其中一边铺满屏幕
    public static Matrix zoomToOriginalShowRange(Matrix matrix, Bitmap bm, float rangeWidth, float rangeHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scale=getZoomScaleToOriginalShowRange(width,height,rangeWidth,rangeHeight);
        matrix.postScale(scale,scale);
        centerInRange(matrix, bm, rangeWidth, rangeHeight);
        return matrix;
    }


    public static float getZoomScaleToOriginalShowRange(float currentWidth, float currentHeight, float rangeWidth, float rangeHeight) {
        float scale = 1;
        //缩放
        if (currentWidth > rangeWidth && currentHeight > rangeHeight) {//宽高都超过规定宽高
            float wScale = currentWidth * 1.0f / rangeWidth;
            float hScale = currentHeight * 1.0f / rangeHeight;
            scale = Math.max(wScale, hScale);
            scale = 1.0f / scale;
        } else if (currentWidth >= rangeWidth && currentHeight <= rangeHeight) {//宽大于规定宽，高等于规定高，缩至规定宽
            float wScale = currentWidth * 1.0f / rangeWidth;
            scale = 1.0f / wScale;
        } else if (currentWidth <= rangeWidth && currentHeight > rangeHeight) {//图片高大于规定高度，而宽度没有，则图片缩至规定高度
            float hScale = currentHeight * 1.0f / rangeHeight;
            scale = 1.0f / hScale;
        } else if (currentWidth < rangeWidth && currentHeight < rangeHeight) {//宽高都小于规定宽高
            if (currentWidth >= currentHeight) {
                scale = currentWidth * 1.0f / rangeWidth;
            } else {
                float wScale = currentWidth * 1.0f / rangeWidth;
                float hScale = currentHeight * 1.0f / rangeHeight;
                scale = Math.max(wScale, hScale);
            }
            scale = 1.0f / scale;

        }
        return scale;
    }


    public static Rect getMatrixRectForOriginalShowRange(Bitmap bm, float rangeWidth, float rangeHeight){
         Matrix matrix=new Matrix();
         zoomToOriginalShowRange(matrix,bm,rangeWidth,rangeHeight);
         return getMatrixRect(matrix,bm);
    }



    //在范围内居中
    public static Matrix centerInRange(Matrix mMatrix, Bitmap bitmap, float rangeWidth, float rangeHeight) {
        Matrix m = new Matrix();
        m.set(mMatrix);
        RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        m.mapRect(rect);
        float height = rect.height();
        float width = rect.width();
        float deltaX = 0, deltaY = 0;
        // 图片小于控件大小，则居中显示。大于控件，上方留空则往上移，下方留空则往下移
        if (height < rangeHeight) {
            deltaY = (rangeHeight - height) / 2 - rect.top;
        } else if (rect.top > 0) {
            deltaY = -rect.top;
        } else if (rect.bottom < rangeHeight) {
            deltaY = rangeHeight - rect.bottom;
        }


        if (width < rangeWidth) {
            deltaX = (rangeWidth - width) / 2 - rect.left;
        } else if (rect.left > 0) {
            deltaX = -rect.left;
        } else if (rect.right < rangeWidth) {
            deltaX = rangeWidth - rect.right;
        }
        mMatrix.postTranslate(deltaX, deltaY);
        return mMatrix;
    }


}
