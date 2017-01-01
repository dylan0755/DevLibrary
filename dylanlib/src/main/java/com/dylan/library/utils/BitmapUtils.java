package com.dylan.library.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dylan.library.io.IOCloser;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Dylan on 2016/12/30.
 */

public class BitmapUtils {


    private static String dirPath ;

    public static Bitmap getScaleBitmap(String imgPath) {
        try {
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inJustDecodeBounds = true;
            newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
            newOpts.inJustDecodeBounds = false;
            int w = newOpts.outWidth;
            int h = newOpts.outHeight;
            float hh = 720;
            float ww = 480;
            int be = 1;//be=1表示不缩放
            if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
                be = (int) (newOpts.outWidth / ww);
            } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
                be = (int) (newOpts.outHeight / hh);
            }
            if (be <= 0) be = 1;
            newOpts.inSampleSize = be;//设置缩放比例
            bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public static void outPut(Bitmap bitmap, String savePath) {
        outPut(Bitmap.CompressFormat.PNG,bitmap, 100, savePath,null);
    }

    public static void outPut(Bitmap bitmap, String savePath,OutPutListenener listener) {
        outPut(Bitmap.CompressFormat.PNG,bitmap, 100, savePath,listener);
    }



    public static void outPut(final Bitmap.CompressFormat format, final Bitmap bitmap, final int qulity, final String savePath, final OutPutListenener listener) {
        if (bitmap == null){
            Log.e("BitmapUtils.outPut()","bitmap==null");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 测试输出
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(savePath);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    if (null != out) {
                        bitmap.compress(format, qulity, out);
                        out.flush();
                        IOCloser.closeIO(out);
                        Handler handler=new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (listener!=null) listener.onSuccess();
                            }
                        });

                        Log.e("BitmapUtils: ","bitmap has compress success" );
                    }
                } catch (IOException e) {
                    Log.e("BitmapUtils: ", "e:" + e.getMessage());
                }
            }
        }).start();

    }


    public interface OutPutListenener{
        void onSuccess();
    }


}
