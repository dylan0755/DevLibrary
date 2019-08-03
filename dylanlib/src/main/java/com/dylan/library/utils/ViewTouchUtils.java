package com.dylan.library.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by Dylan on 2017/5/25.
 */

public class ViewTouchUtils {

    // 求两点距离， 两点间距离公式
    public static float spacing(MotionEvent event) {
        float x = 0;
        float y = 0;
        try {
            x = event.getX(0) - event.getX(1);
            y = event.getY(0) - event.getY(1);
        } catch (IllegalArgumentException e) {
            // e.printStackTrace();
        }
        return (float) Math.sqrt(x * x + y * y); // 两点间距离公式(注意这里的X,Y都是差值) // 两点间距离公式(注意这里的X,Y都是差值)
    }


    public static float spacing(PointF startPoint, PointF endPoint) {
        float x = 0;
        float y = 0;
        try {
            x = startPoint.x - endPoint.x;
            y = startPoint.y - endPoint.y;
        } catch (IllegalArgumentException e) {
            // e.printStackTrace();
        }
        return (float) Math.sqrt(x * x + y * y); // 两点间距离公式(注意这里的X,Y都是差值) // 两点间距离公式(注意这里的X,Y都是差值)
    }
    // 求两点间中点

    public static PointF midPoint(MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        PointF pointF = new PointF();
        pointF.set(x / 2, y / 2);
        return pointF;
    }

    public static void midPoint(PointF pointF, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        pointF.set(x / 2, y / 2);
    }


    public static Matrix initBitmapMatrix(Matrix matrix, Bitmap bm, float parentWidth, float parentHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        //缩放
        if (width > parentWidth && height > parentHeight) {//宽高都超过控件大小
            float scale;
            float wScale = width * 1.0f / parentWidth;
            float hScale = height * 1.0f / parentHeight;
            scale = Math.max(wScale, hScale);
            scale = 1.0f / scale;
            matrix.postScale(scale, scale);
        } else if (width > parentWidth && height == parentHeight) {//宽大于，高等于，图片缩至控件宽度
            float wScale = width * 1.0f / parentWidth;
            float scale = 1.0f / wScale;
            matrix.postScale(scale, scale);
        } else if (width > parentWidth && height < parentHeight) {//宽大于，高小于
            float scale;
            float wScale = width * 1.0f / parentWidth;
            scale = 1.0f / wScale;
            matrix.postScale(scale, scale);
        } else if (width <= parentWidth && height > parentHeight) {//图片高大于控件高度，而宽度没有，则图片缩至控件高度
            float hScale = height * 1.0f / parentHeight;
            float scale = 1.0f / hScale;
            matrix.postScale(scale, scale);
        } else if (width < parentWidth && height < parentHeight) {//宽高都小于控件大小
            float scale;
            if (width == height || width > height) {
                scale = width * 1.0f / parentWidth;
            } else {
                float wScale = width * 1.0f / parentWidth;
                float hScale = height * 1.0f / parentHeight;
                scale = Math.min(wScale, hScale);
            }
            scale = 1.0f / scale;
            matrix.postScale(scale, scale);
        }
        center(matrix, bm, parentWidth, parentHeight);
        return matrix;
    }


    public static Matrix center(Matrix mMatrix, Bitmap mBitmap, float parentWidth, float parentHeight) {
        Matrix m = new Matrix();
        m.set(mMatrix);
        RectF rect = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        m.mapRect(rect);
        float height = rect.height();
        float width = rect.width();
        float deltaX = 0, deltaY = 0;
        // 图片小于控件大小，则居中显示。大于控件，上方留空则往上移，下方留空则往下移
        if (height < parentHeight) {
            deltaY = (parentHeight - height) / 2 - rect.top;
        } else if (rect.top > 0) {
            deltaY = -rect.top;
        } else if (rect.bottom < parentHeight) {
            deltaY = parentHeight - rect.bottom;
        }


        if (width < parentWidth) {
            deltaX = (parentWidth - width) / 2 - rect.left;
        } else if (rect.left > 0) {
            deltaX = -rect.left;
        } else if (rect.right < parentWidth) {
            deltaX = parentWidth - rect.right;
        }
        mMatrix.postTranslate(deltaX, deltaY);
        return mMatrix;
    }


    public static float checkDxBound(float dx, Matrix matrix, Bitmap bitmap, ImageView imageView) {
        float[] values = new float[9];
        matrix.getValues(values);
        float width = imageView.getWidth();
        float bmWidth = bitmap.getWidth();
        if (bmWidth * values[Matrix.MSCALE_X] < width)
            return 0;
        if (values[Matrix.MTRANS_X] + dx > 0)
            dx = -values[Matrix.MTRANS_X];
        else if (values[Matrix.MTRANS_X] + dx < -(bmWidth * values[Matrix.MSCALE_X] - width))
            dx = -(bmWidth * values[Matrix.MSCALE_X] - width) - values[Matrix.MTRANS_X];
        return dx;
    }

    public static float checkDyBound(float dy, Matrix matrix, Bitmap bitmap, ImageView imageView) {
        float[] values = new float[9];
        matrix.getValues(values);
        float height = imageView.getHeight();
        float bmHeight = bitmap.getHeight();
        if (bmHeight * values[Matrix.MSCALE_Y] < height)
            return 0;
        if (values[Matrix.MTRANS_Y] + dy > 0)
            dy = -values[Matrix.MTRANS_Y];
        else if (values[Matrix.MTRANS_Y] + dy < -(bmHeight * values[Matrix.MSCALE_Y] - height))
            dy = -(bmHeight * values[Matrix.MSCALE_Y] - height) - values[Matrix.MTRANS_Y];
        return dy;
    }

}
