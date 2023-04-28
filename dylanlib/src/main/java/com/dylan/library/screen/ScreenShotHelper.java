package com.dylan.library.screen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.dylan.library.utils.Logger;

import java.nio.ByteBuffer;


/**
 * Author: Dylan
 * Date: 2022/9/26
 * Desc:
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ScreenShotHelper {
    private static final int REQUEST_SCREEN_SHOT=666;
    private MediaProjectionManager mediaProjectionManager;
    private  ImageReader mImageReader;
    private WindowManager mWindowManager;
    private int mWindowWidth;
    private int mWindowHeight;
    private int mScreenDensity;
    private VirtualDisplay mVirtualDisplay;


    public ScreenShotHelper(Context context){
        mediaProjectionManager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mWindowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        mWindowWidth = displayMetrics.widthPixels;
        mWindowHeight =displayMetrics.heightPixels;
        mImageReader = ImageReader.newInstance(mWindowWidth, mWindowHeight, 0x1, 2);
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
        mScreenDensity = displayMetrics.densityDpi;
    }


    public void request(Activity activity){
        activity.startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), 666);
    }


    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==REQUEST_SCREEN_SHOT&&resultCode== Activity.RESULT_OK){
            MediaProjection mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
            if (null != mediaProjection) {
                mVirtualDisplay = mediaProjection.createVirtualDisplay("ScreenCapture", mWindowWidth, mWindowHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.getSurface(), null, null);
            }
            return true;
        }
        return false;
    }


    public Bitmap takeScreenShot(){
        Image image = mImageReader.acquireLatestImage();
        if (image == null) {
            Logger.e( "image is null.");
            return null;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap currentScreenShot = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        currentScreenShot.copyPixelsFromBuffer(buffer);
        currentScreenShot = Bitmap.createBitmap(currentScreenShot, 0, 0, width, height);
        image.close();
        return currentScreenShot;
    }

    public void release() {
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
    }
}