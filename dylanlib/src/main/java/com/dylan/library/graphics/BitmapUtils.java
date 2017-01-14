package com.dylan.library.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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





    public static class BlurImages{
        /** 水平方向模糊度 */
        private static float hRadius = 5;
        /** 竖直方向模糊度 */
        private static float vRadius = 5;
        /** 模糊迭代度 */
        private static int iterations =3;


        public  static void setRadius(float hradius,float vradius, int iteration){
             hRadius=hradius;
            vRadius=vradius;
            iterations=iteration;
        }
        /**
         * 图片高斯模糊处理
         * ARGB_8888 每个像素占用4个字节
         * ARGB_4444 每个像素占用2个字节
         *  ARGB_565 每个像素占用2个字节
         *  ALPHA_8  每个像素占用1个字节
         */
        public static Drawable blurImages(Bitmap bmp, Context context) {
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            int[] inPixels = new int[width * height];
            int[] outPixels = new int[width * height];
            if (bmp==null||bmp.isRecycled()){
                Log.e("blurImages: ","bmp==null||bmp.isRecycled()" );
                return null;
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
            for (int i = 0; i < iterations; i++) {
                blur(inPixels, outPixels, width, height, hRadius);
                blur(outPixels, inPixels, height, width, vRadius);
            }
            blurFractional(inPixels, outPixels, width, height, hRadius);
            blurFractional(outPixels, inPixels, height, width, vRadius);
            bitmap.setPixels(inPixels, 0, width, 0, 0, width, height);
            Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
            return drawable;
        }
        /**
         * 图片高斯模糊算法
         */
        private static void blur(int[] in, int[] out, int width, int height, float radius) {
            int widthMinus1 = width - 1;
            int r = (int) radius;
            int tableSize = 2 * r + 1;
            int divide[] = new int[256 * tableSize];
            for (int i = 0; i < 256 * tableSize; i++)
                divide[i] = i / tableSize;
            int inIndex = 0;
            for (int y = 0; y < height; y++) {
                int outIndex = y;
                int ta = 0, tr = 0, tg = 0, tb = 0;
                for (int i = -r; i <= r; i++) {
                    int rgb = in[inIndex + clamp(i, 0, width - 1)];
                    ta += (rgb >> 24) & 0xff;
                    tr += (rgb >> 16) & 0xff;
                    tg += (rgb >> 8) & 0xff;
                    tb += rgb & 0xff;
                }
                for (int x = 0; x < width; x++) {
                    out[outIndex] = (divide[ta] << 24) | (divide[tr] << 16) | (divide[tg] << 8) | divide[tb];
                    int i1 = x + r + 1;
                    if (i1 > widthMinus1)
                        i1 = widthMinus1;
                    int i2 = x - r;
                    if (i2 < 0)
                        i2 = 0;
                    int rgb1 = in[inIndex + i1];
                    int rgb2 = in[inIndex + i2];
                    ta += ((rgb1 >> 24) & 0xff) - ((rgb2 >> 24) & 0xff);
                    tr += ((rgb1 & 0xff0000) - (rgb2 & 0xff0000)) >> 16;
                    tg += ((rgb1 & 0xff00) - (rgb2 & 0xff00)) >> 8;
                    tb += (rgb1 & 0xff) - (rgb2 & 0xff);
                    outIndex += height;
                }
                inIndex += width;
            }
        }

        /**
         * 图片高斯模糊算法
         * 
         */
        private  static void blurFractional(int[] in, int[] out, int width, int height, float radius) {
            radius -= (int) radius;
            float f = 1.0f / (1 + 2 * radius);
            int inIndex = 0;
            for (int y = 0; y < height; y++) {
                int outIndex = y;
                out[outIndex] = in[0];
                outIndex += height;
                for (int x = 1; x < width - 1; x++) {
                    int i = inIndex + x;
                    int rgb1 = in[i - 1];
                    int rgb2 = in[i];
                    int rgb3 = in[i + 1];
                    int a1 = (rgb1 >> 24) & 0xff;
                    int r1 = (rgb1 >> 16) & 0xff;
                    int g1 = (rgb1 >> 8) & 0xff;
                    int b1 = rgb1 & 0xff;
                    int a2 = (rgb2 >> 24) & 0xff;
                    int r2 = (rgb2 >> 16) & 0xff;
                    int g2 = (rgb2 >> 8) & 0xff;
                    int b2 = rgb2 & 0xff;
                    int a3 = (rgb3 >> 24) & 0xff;
                    int r3 = (rgb3 >> 16) & 0xff;
                    int g3 = (rgb3 >> 8) & 0xff;
                    int b3 = rgb3 & 0xff;
                    a1 = a2 + (int) ((a1 + a3) * radius);
                    r1 = r2 + (int) ((r1 + r3) * radius);
                    g1 = g2 + (int) ((g1 + g3) * radius);
                    b1 = b2 + (int) ((b1 + b3) * radius);
                    a1 *= f;
                    r1 *= f;
                    g1 *= f;
                    b1 *= f;
                    out[outIndex] = (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
                    outIndex += height;
                }
                out[outIndex] = in[width - 1];
                inIndex += width;
            }
        }
        public static int clamp(int x, int a, int b) {
            return (x < a) ? a : (x > b) ? b : x;
        }
    }


    public interface OutPutListenener{
        void onSuccess();
    }


}
