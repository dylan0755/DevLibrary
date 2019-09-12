package com.dylan.library.graphics;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.dylan.library.exception.ELog;
import com.dylan.library.io.FileUtils;
import com.dylan.library.io.IOCloser;
import com.dylan.library.utils.EmptyUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Created by Dylan on 2016/12/30.
 */

public class BitmapHelper {


    public static Bitmap getInSampleSizeBitmap(String picPath, int reqsW, int reqsH) {
        try {
            FileInputStream inputStream = new FileInputStream(picPath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ReadableByteChannel channel = Channels.newChannel(inputStream);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (channel.read(buffer) != -1) {
                buffer.flip();
                while (buffer.hasRemaining())
                    baos.write(buffer.get());
                buffer.clear();
            }
            byte[] bts = baos.toByteArray();
            inputStream.close();
            channel.close();
            baos.close();
            //图片尺寸压缩
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bts, 0, bts.length, options);
            options.inSampleSize = caculateInSampleSize(options, reqsW, reqsH);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeByteArray(bts, 0, bts.length, options);
        } catch (Exception e) {
            e.printStackTrace();
            ELog.e(e);
            return null;
        }
    }

    //获得采样率
    public final static int caculateInSampleSize(BitmapFactory.Options options, int rqsW, int rqsH) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (rqsW == 0 || rqsH == 0)
            return 1;
        if (height > rqsH || width > rqsW) {
            final int heightRatio = Math.round((float) height / (float) rqsH);
            final int widthRatio = Math.round((float) width / (float) rqsW);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


    //同步保存并通知系统扫描
    public static void saveBitmapSyncAndNotifyScan(Context context, Bitmap bitmap, String savePath) throws IOException {
        if (EmptyUtils.isEmpty(bitmap) || EmptyUtils.isEmpty(savePath)) return;
        File outPutFile = new File(savePath);
        if (!outPutFile.getParentFile().exists()) {
            outPutFile.getParentFile().mkdirs();
        }

        FileOutputStream fos = new FileOutputStream(savePath);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
        FileUtils.notifyScanFile(context, savePath);
    }


    //同步保存
    public static void saveBitmapSync(Bitmap bitmap, String savePath) throws IOException {
        if (EmptyUtils.isEmpty(bitmap) || EmptyUtils.isEmpty(savePath)) return;

        File outPutFile = new File(savePath);
        if (!outPutFile.getParentFile().exists()) {
            outPutFile.getParentFile().mkdirs();
        }

        FileOutputStream fos = new FileOutputStream(savePath);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
    }


    //异步保存
    public static void saveBitmapASync(Bitmap bitmap, String savePath) {
        if (EmptyUtils.isEmpty(bitmap) || EmptyUtils.isEmpty(savePath)) return;
        saveBitmapASync(Bitmap.CompressFormat.PNG, bitmap, 100, savePath, null);
    }

    //异步保存
    public static void saveBitmapASync(Bitmap bitmap, String savePath, OutPutListenener listener) {
        if (EmptyUtils.isEmpty(bitmap) || EmptyUtils.isEmpty(savePath)) return;
        saveBitmapASync(Bitmap.CompressFormat.PNG, bitmap, 100, savePath, listener);
    }


    private static void saveBitmapASync(final Bitmap.CompressFormat format, final Bitmap bitmap, final int qulity, final String savePath, final OutPutListenener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //创建目录
                File file = new File(savePath);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
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
                        if (listener != null) listener.onSuccess();
                        Log.i("BitmapHelper: ", "bitmap has compress success");
                    }
                } catch (IOException e) {
                    ELog.e(e);
                }
            }
        }).start();

    }


    public static Bitmap convertViewToBitmap(final View v) {
        v.clearFocus();
        v.setPressed(false);
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }

    public static Bitmap getBitmapFromImageView(ImageView imageView) {
        if (imageView == null) return null;
        imageView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(imageView.getDrawingCache());
        imageView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    //Bitamp 圆角+白边框
    public static Bitmap transformRoundCornerWithBorder(Bitmap bitmap, int cornerRadius, int border) {
        if (bitmap == null) return null;
        return transformRoundCornerWithBorder(bitmap, bitmap.getWidth(), bitmap.getHeight(), cornerRadius, border);
    }


    public static Bitmap transformRoundCornerWithBorder(Bitmap bitmap, int outWidth, int outHeight, int cornerRadius, int border) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float widthScale = outWidth * 1f / width;
        float heightScale = outHeight * 1f / height;

        Matrix matrix = new Matrix();
        matrix.setScale(widthScale, heightScale);
        //创建输出的bitmap
        Bitmap desBitmap = Bitmap.createBitmap(width + 2 * border, height + 2 * border, Bitmap.Config.ARGB_8888);
        //创建canvas并传入desBitmap，这样绘制的内容都会在desBitmap上
        Canvas canvas = new Canvas(desBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //创建着色器
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        //给着色器配置matrix
        bitmapShader.setLocalMatrix(matrix);
        paint.setShader(bitmapShader);
        //创建矩形区域并且预留出border
        RectF rect = new RectF(border, border, border + width, border + height);
        //把传入的bitmap绘制到圆角矩形区域内
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);

        if (border > 0) {
            //绘制boarder
            Paint boarderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            boarderPaint.setColor(Color.WHITE);
            boarderPaint.setStyle(Paint.Style.STROKE);
            boarderPaint.setStrokeWidth(border);
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, boarderPaint);
        }
        return desBitmap;

    }


    //Bitamp 圆形+白边框
    public static Bitmap transformCircleWithBorder(Bitmap bitmap, int border) {
        if (bitmap == null) return null;
        return transformCircleWithBorder(bitmap, bitmap.getWidth(), bitmap.getHeight(), border);
    }

    public static Bitmap transformCircleWithBorder(Bitmap bitmap, int outWidth, int outHeight, int border) {

        if (bitmap == null) {
            return null;
        }
        int radius;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float widthScale = outWidth * 1f / width;
        float heightScale = outHeight * 1f / height;

        Matrix matrix = new Matrix();
        matrix.setScale(widthScale, heightScale);
        Bitmap desBitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        if (outHeight > outWidth) {
            radius = outWidth / 2;
        } else {
            radius = outHeight / 2;
        }
        //创建canvas
        Canvas canvas = new Canvas(desBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        bitmapShader.setLocalMatrix(matrix);
        paint.setShader(bitmapShader);
        canvas.drawCircle(outWidth / 2, outHeight / 2, radius - border, paint);
        if (border > 0) {
            //绘制boarder
            Paint boarderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            boarderPaint.setColor(Color.WHITE);
            boarderPaint.setStyle(Paint.Style.STROKE);
            boarderPaint.setStrokeWidth(border);
            canvas.drawCircle(outWidth / 2, outHeight / 2, radius - border, boarderPaint);
        }
        return desBitmap;


    }


    //两个Bitmap叠加
    public static Bitmap layOnCenter(Bitmap bitmapBottom, Bitmap bitmapTop, float toScale) {
        if (bitmapBottom == null) {
            return null;
        }

        if (bitmapTop == null) {
            return bitmapBottom;
        }
        //获取图片的宽高
        int srcWidth = bitmapBottom.getWidth();
        int srcHeight = bitmapBottom.getHeight();
        int logoWidth = bitmapTop.getWidth();
        int logoHeight = bitmapTop.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }

        if (logoWidth == 0 || logoHeight == 0) {
            return bitmapBottom;
        }

        //logo大小为二维码整体大小的 几分之几
        float scaleFactor = srcWidth * toScale / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(bitmapBottom, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(bitmapTop, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);

            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }





    /**
     * bitmap转为base64
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        if (base64Data == null || base64Data.isEmpty()) return null;
        base64Data = base64Data.replace("data:image/jpeg;base64", "");
        base64Data = base64Data.replace("data:image/png;base64", "");
        byte[] bytes = Base64.decode(base64Data, Base64.NO_WRAP);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    public interface OutPutListenener {
        void onSuccess();

        void onFailure();
    }

    public static class BlurImages {
        /**
         * 水平方向模糊度
         */
        private static float hRadius = 5;
        /**
         * 竖直方向模糊度
         */
        private static float vRadius = 5;
        /**
         * 模糊迭代度
         */
        private static int iterations = 3;


        public static void setRadius(float hradius, float vradius, int iteration) {
            hRadius = hradius;
            vRadius = vradius;
            iterations = iteration;
        }

        /**
         * 图片高斯模糊处理
         * ARGB_8888 每个像素占用4个字节
         * ARGB_4444 每个像素占用2个字节
         * ARGB_565 每个像素占用2个字节
         * ALPHA_8  每个像素占用1个字节
         */
        public static Drawable blurImages(Bitmap bmp, Context context) {
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            int[] inPixels = new int[width * height];
            int[] outPixels = new int[width * height];
            if (bmp == null || bmp.isRecycled()) {
                Log.e("blurImages: ", "bmp==null||bmp.isRecycled()");
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
        private static void blurFractional(int[] in, int[] out, int width, int height, float radius) {
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

}
