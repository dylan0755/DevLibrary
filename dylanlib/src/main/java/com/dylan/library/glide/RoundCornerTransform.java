package com.dylan.library.glide;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import androidx.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.BuildConfig;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.security.MessageDigest;

/**
 * Author: Dylan
 * Date: 2020/2/20
 * Desc:
 */
public class RoundCornerTransform extends BitmapTransformation {
    private float mRadius;
    private CornerType mCornerType;
    private static final int VERSION = 1;
    private static final int ROTATE = 8;
    private static final String ID = BuildConfig.APPLICATION_ID+"RoundCornerTransform."+VERSION ;
    private ImageView.ScaleType mScaleType;



    public RoundCornerTransform(int radius, CornerType cornerType, ImageView.ScaleType scaleType) {
        mRadius=Resources.getSystem().getDisplayMetrics().density * radius;
        mCornerType = cornerType;
        mScaleType=scaleType;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap bitmap;
        switch (mScaleType) {
            case CENTER_CROP:
                bitmap = TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight);
                break;
            case CENTER_INSIDE:
                bitmap = TransformationUtils.centerInside(pool, toTransform, outWidth, outHeight);
                break;
            case FIT_CENTER:
                bitmap = TransformationUtils.fitCenter(pool, toTransform, outWidth, outHeight);
                break;
            case CENTER :
                bitmap = TransformationUtils.rotateImageExif(pool, toTransform,ROTATE);
                break;
            default:
                bitmap=toTransform;
                break;
        }
        return roundCrop(pool, bitmap);
    }

    private Bitmap roundCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;
        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(result);
        Path path = new Path();
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);

        int width = source.getWidth();
        int height = source.getHeight();
        drawRoundRect(canvas,paint,path,width,height);
        return result;
    }


    private void drawRoundRect(Canvas canvas, Paint paint, Path path, int width, int height) {
        float[] rids;
        if (mCornerType == CornerType.ALL) {
            rids = new float[]{mRadius, mRadius, mRadius, mRadius, mRadius, mRadius, mRadius, mRadius};
            drawPath(rids, canvas, paint, path, width, height);
        } else if (mCornerType == CornerType.TOP_LEFT) {
            rids = new float[]{mRadius, mRadius, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
            drawPath(rids, canvas, paint, path, width, height);
        } else if (mCornerType ==CornerType.TOP_RIGHT) {
            rids = new float[]{0.0f, 0.0f, mRadius, mRadius, 0.0f, 0.0f, 0.0f, 0.0f};
            drawPath(rids, canvas, paint, path, width, height);
        } else if (mCornerType ==CornerType.BOTTOM_RIGHT) {
            rids = new float[]{0.0f, 0.0f, 0.0f, 0.0f, mRadius, mRadius, 0.0f, 0.0f};
            drawPath(rids, canvas, paint, path, width, height);
        } else if (mCornerType == CornerType.BOTTOM_LEFT) {
            rids = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, mRadius, mRadius};
            drawPath(rids, canvas, paint, path, width, height);
        } else if (mCornerType == CornerType.TOP) {
            rids = new float[]{mRadius, mRadius, mRadius, mRadius, 0.0f, 0.0f, 0.0f, 0.0f};
            drawPath(rids, canvas, paint, path, width, height);
        } else if (mCornerType ==CornerType.BOTTOM) {
            rids = new float[]{0.0f, 0.0f, 0.0f, 0.0f, mRadius, mRadius, mRadius, mRadius};
            drawPath(rids, canvas, paint, path, width, height);
        } else if (mCornerType == CornerType.LEFT) {
            rids = new float[]{mRadius, mRadius, 0.0f, 0.0f, 0.0f, 0.0f, mRadius, mRadius};
            drawPath(rids, canvas, paint, path, width, height);
        } else if (mCornerType == CornerType.RIGHT) {
            rids = new float[]{0.0f, 0.0f, mRadius, mRadius, mRadius, mRadius, 0.0f, 0.0f};
            drawPath(rids, canvas, paint, path, width, height);
        } else if (mCornerType ==CornerType.TOP_LEFT_BOTTOM_RIGHT) {
            rids = new float[]{mRadius, mRadius, 0.0f, 0.0f, mRadius, mRadius, 0.0f, 0.0f};
            drawPath(rids, canvas, paint, path, width, height);
        } else if (mCornerType == CornerType.TOP_RIGHT_BOTTOM_LEFT) {
            rids = new float[]{0.0f, 0.0f, mRadius, mRadius, 0.0f, 0.0f, mRadius, mRadius};
            drawPath(rids, canvas, paint, path, width, height);
        } else if (mCornerType ==CornerType.TOP_LEFT_TOP_RIGHT_BOTTOM_RIGHT) {
            rids = new float[]{mRadius, mRadius, mRadius, mRadius, mRadius, mRadius, 0.0f, 0.0f};
            drawPath(rids, canvas, paint, path, width, height);
        } else if (mCornerType ==CornerType.TOP_RIGHT_BOTTOM_RIGHT_BOTTOM_LEFT) {
            rids = new float[]{0.0f, 0.0f, mRadius, mRadius, mRadius, mRadius, mRadius, mRadius};
            drawPath(rids, canvas, paint, path, width, height);
        } else {
            throw new RuntimeException("RoundedCorners type not belong to CornerType");
        }
    }

    private void drawPath(float[] rids, Canvas canvas, Paint paint, Path path, int width, int height) {
        path.addRoundRect(new RectF(0, 0, width, height), rids, Path.Direction.CW);
       canvas.clipPath(path);
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof RoundCornerTransform;
    }


    @Override
    public int hashCode() {
        return (ID + mRadius).hashCode();
    }




    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update((ID + mRadius).getBytes(Key.CHARSET));
    }



}
