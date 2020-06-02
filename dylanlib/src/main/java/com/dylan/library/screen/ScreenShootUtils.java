package com.dylan.library.screen;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.dylan.library.graphics.BitmapHelper;


/**
 * Created by Dylan on 2017/1/1.
 */


public class ScreenShootUtils {

    public static Bitmap captureDecorView(Activity activity) {
        View decordView = activity.getWindow().getDecorView();
        decordView.setDrawingCacheEnabled(true);
        decordView.buildDrawingCache();
        Bitmap bitmap = decordView.getDrawingCache();
        decordView.destroyDrawingCache();
        return bitmap;
    }


    public static Bitmap captureDecorViewExcludeStatusBar(Activity activity) {
        View decordView = activity.getWindow().getDecorView();
        decordView.setDrawingCacheEnabled(true);
        decordView.buildDrawingCache();
        Bitmap bitmap = decordView.getDrawingCache();
        int statusBarHeight = ScreenUtils.getStatusBarHeight(activity);
        bitmap = Bitmap.createBitmap(bitmap, 0,
                statusBarHeight, bitmap.getWidth(), bitmap.getHeight() - statusBarHeight);
        decordView.destroyDrawingCache();
        return bitmap;
    }


    public static Bitmap captureDecorViewExcludeStatusNavBar(Activity activity) {
        View decordView = activity.getWindow().getDecorView();
        decordView.setDrawingCacheEnabled(true);
        decordView.buildDrawingCache();
        Bitmap bitmap = decordView.getDrawingCache();
        int statusBarHeight = ScreenUtils.getStatusBarHeight(activity);
        int navHeight=0;
        if (NavBarUtils.hasNavBar(activity)){
            navHeight=NavBarUtils.getNavBarHeight(activity);
        }
        bitmap = Bitmap.createBitmap(bitmap, 0,
                statusBarHeight, bitmap.getWidth(), bitmap.getHeight() - statusBarHeight-navHeight);
        decordView.destroyDrawingCache();
        return bitmap;
    }




    public static Bitmap captureDecorView(Activity activity, String savePath) {
        return captureDecorView(activity, savePath, null);
    }

    public static Bitmap captureDecorView(Activity activity, String savePath, BitmapHelper.OutPutListenener listener) {
        if (activity == null) return null;
        View decordView = activity.getWindow().getDecorView();
        decordView.setDrawingCacheEnabled(true);
        decordView.buildDrawingCache();
        Bitmap bitm = decordView.getDrawingCache();
        BitmapHelper.saveBitmapASync(bitm, savePath, listener);
        return bitm;
    }


    public static Bitmap captureDecorView(Window window, String savePath, BitmapHelper.OutPutListenener listener) {
        View decordView =window.getDecorView();
        decordView.setDrawingCacheEnabled(true);
        decordView.buildDrawingCache();
        Bitmap bitm = decordView.getDrawingCache();
        BitmapHelper.saveBitmapASync(bitm, savePath, listener);
        return bitm;
    }




    /**
     * 截取scrollview的屏幕
     **/
    public static Bitmap captureScrollView(ScrollView scrollView, String savePath) {
        return captureScrollView(scrollView, savePath, null);
    }

    public static Bitmap captureScrollView(ScrollView scrollView, String savePath, BitmapHelper.OutPutListenener listener) {
        if (scrollView == null) return null;
        int h = 0;
        Bitmap bitmap;
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
        }

        if (scrollView.getWidth() == 0 || h == 0) {
            Log.e("captureScrollView: ", "width and height must be > 0");
            return null;
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        BitmapHelper.saveBitmapASync(bitmap, savePath, listener);
        return bitmap;
    }


    /**
     * 截图recyclerview
     **/
    public static Bitmap captureRecyclerView(RecyclerView recyclerview, String savePath) {
        return captureRecyclerView(recyclerview, savePath, null);
    }

    public static Bitmap captureRecyclerView(RecyclerView recyclerview, String savePath, BitmapHelper.OutPutListenener listener) {
        if (recyclerview == null) return null;
        int h = 0;
        Bitmap bitmap;
        // 获取recyclerview实际高度
        for (int i = 0; i < recyclerview.getChildCount(); i++) {
            h += recyclerview.getChildAt(i).getHeight();
        }


        if (recyclerview.getWidth() == 0 || h == 0) {
            Log.e("captureRecyclerView: ", "width and height must be > 0");
            return null;
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(recyclerview.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        recyclerview.draw(canvas);
        BitmapHelper.saveBitmapASync(bitmap, savePath, listener);
        return bitmap;

    }


    public static Bitmap captureImageView(ImageView imageView, String savePath) {
        return captureImageView(imageView, savePath, null);
    }

    public static Bitmap captureImageView(ImageView imageView, String savePath, BitmapHelper.OutPutListenener listener) {
        if (imageView == null) return null;
        // 创建对应大小的bitmap
        Bitmap bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(),
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        imageView.draw(canvas);
        BitmapHelper.saveBitmapASync(bitmap, savePath, listener);
        return bitmap;
    }


}
