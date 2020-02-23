package com.dylan.library.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;

/**
 * Author: Dylan
 * Date: 2020/2/23
 * Desc:
 */
public class ImageViewUtils {

    public static void setImageBitmap(final ImageView iv, final Bitmap bitmap){
        iv.post(new Runnable() {
            @Override
            public void run() {
                iv.setImageBitmap(bitmap);
            }
        });
    }

    public static void setImageDrawable(final ImageView iv , int resId){
       final Drawable drawable= ContextCompat.getDrawable(iv.getContext(),resId);
        iv.post(new Runnable() {
            @Override
            public void run() {
                iv.setImageDrawable(drawable);
            }
        });
    }

    public static void setImageDrawable(final ImageView iv ,final Drawable drawable){
        iv.post(new Runnable() {
            @Override
            public void run() {
                iv.setImageDrawable(drawable);
            }
        });
    }



    public static int[] getImageSize(ImageView imageview) {

        DisplayMetrics displayMetrics= imageview.getContext().getResources().getDisplayMetrics();
        ViewGroup.LayoutParams lp = imageview.getLayoutParams();
        int width = imageview.getWidth();//获取实际宽度
        if (width <= 0) {
            width = lp.width;//获取在布局中的宽度
        }
        if (width <= 0) {
            width = getImageViewFileValue(imageview, "mMaxWidth");//检查最大值
        }
        if (width <= 0) {
            width = displayMetrics.widthPixels;//设置为屏幕的宽度
        }

        int height = imageview.getHeight();
        if (height <= 0) {
            height = lp.height;
        }
        if (height <= 0) {
            height = getImageViewFileValue(imageview, "mMaxHeight");
        }
        if (height <= 0) {
            height = displayMetrics.heightPixels;
        }
        return new int[]{width,height};
    }

    //反射获取属性值
    private static int getImageViewFileValue(Object object, String fieldName) {
        int value = 0;
        Field field = null;
        try {
            field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = field.getInt(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
}
