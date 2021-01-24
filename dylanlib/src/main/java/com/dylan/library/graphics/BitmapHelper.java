package com.dylan.library.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.Type;
import android.support.annotation.FloatRange;
import android.support.annotation.RequiresApi;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.dylan.library.exception.ELog;
import com.dylan.library.io.FileUtils;
import com.dylan.library.io.IOCloser;
import com.dylan.library.utils.DensityUtils;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.utils.Logger;
import com.dylan.library.utils.MatrixUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Dylan on 2016/12/30.
 */

public class BitmapHelper {
    private static final String TAG = BitmapHelper.class.getSimpleName();

    //如果项目做了屏幕适配更改了density, 那么setImageBitmap 之后 获取的drawble 大小和 Bitmap的大小不一致，需要设置项目的density
    public static void setCurrentDensity(Bitmap bitmap, Context context) {
        bitmap.setDensity(context.getResources().getDisplayMetrics().densityDpi);
    }

    public static PointF getClickPointF(ImageView iv, MotionEvent e) {
        return MatrixUtils.getBitmapClickPointF(iv,e);
    }

    public static Point getClickPoint(ImageView iv, MotionEvent e) {
        return MatrixUtils.getBitmapClickPoint(iv,e);
    }

    public static boolean clickInTransparentArea(Bitmap bitmap, MotionEvent e) {
        if (bitmap == null || e == null) return false;
        if (bitmap.getPixel((int) (e.getX()), ((int) e.getY())) == 0) return true;
        return false;
    }


    public static Bitmap scale(Bitmap srcBitmap, int outWidth, int outHeight) {
        int width = srcBitmap.getWidth();
        int height = srcBitmap.getHeight();

        float scaleWidth = (outWidth) * 1.0f / width;
        float scaleHeight = (outHeight) * 1.0f / height;


        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(srcBitmap, 0, 0, width, height, matrix, false);
    }


    public static Bitmap scale(Bitmap srcBitmap, float widthScale, float heightScale) {
        int width = srcBitmap.getWidth();
        int height = srcBitmap.getHeight();


        Matrix matrix = new Matrix();
        matrix.postScale(widthScale, heightScale);
        return Bitmap.createBitmap(srcBitmap, 0, 0, width, height, matrix, true);
    }


    public static BitmapSize decodeBitmapSize(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        BitmapSize size = new BitmapSize();
        size.width = options.outWidth;
        size.height = options.outHeight;
        return size;
    }

    public static int readBitmapDegree(String path) {
        return ExifHelper.readPictureDegree(path);
    }


    public static Bitmap rotateToDegrees(Bitmap tmpBitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.setRotate(degrees);
        tmpBitmap = Bitmap.createBitmap(tmpBitmap, 0, 0, tmpBitmap.getWidth(), tmpBitmap.getHeight(), matrix, true);
        return tmpBitmap;
    }


    public static Bitmap getInSampleSizeBitmap(String picPath, int reqsW, int reqsH) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inPreferredConfig = Bitmap.Config.RGB_565;
            option.inJustDecodeBounds = true;//不添加到内存，只测量宽高
            BitmapFactory.decodeFile(picPath, option);
            option.inSampleSize = getInSampleSize(option, reqsW, reqsH);
            try {
                option.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(new FileInputStream(picPath), null, option);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            ELog.e(e);
            return null;
        }
    }

    //获得采样率
    public final static int getInSampleSize(BitmapFactory.Options options, int rqsW,
                                            int rqsH) {
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
    public static void saveBitmapSyncAndNotifyScan(Context context, Bitmap bitmap, String
            savePath) throws IOException {
        if (EmptyUtils.isEmpty(bitmap) || EmptyUtils.isEmpty(savePath)) return;
        File outPutFile = new File(savePath);
        if (!outPutFile.getParentFile().exists()) {
            outPutFile.getParentFile().mkdirs();
        }

        FileOutputStream fos = new FileOutputStream(savePath);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
        FileUtils.notifyScanImageFile(context, savePath);
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
    public static void saveBitmapASync(Bitmap bitmap, String savePath, OutPutListenener
            listener) {
        if (EmptyUtils.isEmpty(bitmap) || EmptyUtils.isEmpty(savePath)) return;
        saveBitmapASync(Bitmap.CompressFormat.PNG, bitmap, 100, savePath, listener);
    }


    private static void saveBitmapASync(final Bitmap.CompressFormat format,
                                        final Bitmap bitmap, final int qulity, final String savePath,
                                        final OutPutListenener listener) {
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
            //如果View 过大，生成Bitmap会失败
            Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bitmap);
            v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
            v.draw(c);
            return bitmap;
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


    public static Bitmap centerCrop(Bitmap srcBitmap, int outWidth, int outHeight) {
        if (srcBitmap == null) return null;
        if (srcBitmap.getWidth() == outWidth && srcBitmap.getHeight() == outHeight) {
            return srcBitmap;
        }
        final float scale;
        final float dx;
        final float dy;
        Matrix m = new Matrix();
        if (srcBitmap.getWidth() * outHeight > outWidth * srcBitmap.getHeight()) {
            scale = (float) outHeight / (float) srcBitmap.getHeight();
            dx = (outWidth - srcBitmap.getWidth() * scale) * 0.5f;
            dy = 0;
        } else {
            scale = (float) outWidth / (float) srcBitmap.getWidth();
            dx = 0;
            dy = (outHeight - srcBitmap.getHeight() * scale) * 0.5f;
        }

        m.setScale(scale, scale);
        m.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));

        // We don't add or remove alpha, so keep the alpha setting of the Bitmap we were given.
        if (Build.VERSION.SDK_INT >= 12 && srcBitmap != null) {
            srcBitmap.setHasAlpha(srcBitmap.hasAlpha());
        }
        Bitmap targetBitmap = Bitmap.createBitmap(outWidth, outHeight, srcBitmap.getConfig());
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setDither(true);
        Canvas canvas = new Canvas(targetBitmap);
        canvas.drawBitmap(srcBitmap, m, paint);
        return targetBitmap;
    }


    public static Bitmap centerCropRoundCorner(Bitmap srcBitmap, int cornerRadius,
                                               int outWidth, int outHeight) {
        Bitmap bitmap = centerCrop(srcBitmap, outWidth, outHeight);
        bitmap = transformRoundCorner(bitmap, cornerRadius, outWidth, outHeight);
        return bitmap;
    }

    public static Bitmap centerCropRoundCornerWithBorder(Bitmap srcBitmap, int cornerRadius,
                                                         int borderWidth, int borderColor, int outWidth, int outHeight) {
        Bitmap bitmap = centerCropRoundCorner(srcBitmap, cornerRadius, outWidth, outHeight);
        bitmap = transformRoundCornerWithBorder(bitmap, cornerRadius, borderWidth, borderColor, outWidth, outHeight);
        return bitmap;
    }


    public static Bitmap transformRoundCorner(Bitmap bitmap, int cornerRadius, int outWidth,
                                              int outHeight) {
        if (bitmap == null) return null;
        float radiusDp = Resources.getSystem().getDisplayMetrics().density * cornerRadius;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float widthScale = outWidth * 1f / width;
        float heightScale = outHeight * 1f / height;
        Matrix matrix = new Matrix();
        matrix.setScale(widthScale, heightScale);
        //创建输出的bitmap
        Bitmap desBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //创建canvas并传入desBitmap，这样绘制的内容都会在desBitmap上
        Canvas canvas = new Canvas(desBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setDither(true);
        //创建着色器
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        bitmapShader.setLocalMatrix(matrix);
        paint.setShader(bitmapShader);
        //创建矩形区域并且预留出border
        RectF rect = new RectF(0, 0, width, height);
        canvas.drawRoundRect(rect, radiusDp, radiusDp, paint);
        return desBitmap;
    }


    public static Bitmap transformRoundCornerWithBorder(Bitmap bitmap, int cornerRadius,
                                                        int borderWidth, int borderColor, int outWidth, int outHeight) {
        if (bitmap == null) {
            return null;
        }
        float radiusPx = Resources.getSystem().getDisplayMetrics().density * cornerRadius;
        borderColor = (int) (Resources.getSystem().getDisplayMetrics().density * borderColor);


        bitmap = transformRoundCorner(bitmap, cornerRadius, outWidth, outHeight);

        Bitmap targetBitmap = Bitmap.createBitmap(bitmap.getWidth() + 2 * borderWidth, bitmap.getHeight() + 2 * borderWidth, bitmap.getConfig());
        Canvas canvas = new Canvas(targetBitmap);
        canvas.drawBitmap(bitmap, borderWidth, borderWidth, null);
        if (borderWidth > 0) {
            //创建矩形区域并且预留出border
            RectF rect = new RectF(borderWidth, borderWidth, targetBitmap.getWidth() - borderWidth, targetBitmap.getHeight() - borderWidth);
            //绘制boarder
            Paint boarderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            boarderPaint.setColor(borderColor);
            boarderPaint.setStyle(Paint.Style.STROKE);
            boarderPaint.setStrokeWidth(borderWidth);
            canvas.drawRoundRect(rect, radiusPx, radiusPx, boarderPaint);
        }
        return targetBitmap;
    }


    public static Bitmap transformCircle(Bitmap bitmap, int outWidth, int outHeight) {
        if (bitmap == null) {
            return null;
        }

        int radius;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float widthScale = outWidth * 1f / width;
        float heightScale = outHeight * 1f / height;


        Bitmap desBitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        if (outHeight > outWidth) {
            radius = outWidth / 2;
        } else {
            radius = outHeight / 2;
        }

        Canvas canvas = new Canvas(desBitmap);


        int cx = outWidth / 2;
        int cy = outHeight / 2;


        //绘制圆形的Bitmap
        Matrix matrix = new Matrix();
        matrix.setScale(widthScale, heightScale);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        bitmapShader.setLocalMatrix(matrix);
        paint.setShader(bitmapShader);
        canvas.drawCircle(cx, cy, radius, paint);

        return desBitmap;
    }


    public static Bitmap transformCircleWithBorder(Bitmap bitmap, int borderWidth,
                                                   int borderColor, int outWidth, int outHeight) {
        if (bitmap == null) {
            return null;
        }

        //转成像素
        borderWidth = (int) (borderWidth * Resources.getSystem().getDisplayMetrics().density);

        int radius;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float widthScale = outWidth * 1f / width;
        float heightScale = outHeight * 1f / height;


        Bitmap desBitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        if (outHeight > outWidth) {
            radius = (outWidth - borderWidth * 2) / 2;
        } else {
            radius = (outHeight - borderWidth * 2) / 2;
        }

        Canvas canvas = new Canvas(desBitmap);


        int cx = outWidth / 2;
        int cy = outHeight / 2;


//        //绘制圆形的Bitmap,要在边框以内
        Matrix matrix = new Matrix();
        matrix.setScale(widthScale, heightScale);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        bitmapShader.setLocalMatrix(matrix);
        paint.setShader(bitmapShader);
        canvas.drawCircle(cx, cy, radius, paint);


        //绘制边框
        if (borderWidth > 0) {
            Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            borderPaint.setDither(true);
            borderPaint.setColor(borderColor);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(borderWidth);
            borderPaint.setStrokeJoin(Paint.Join.ROUND);
            borderPaint.setStrokeCap(Paint.Cap.ROUND);
            canvas.drawCircle(cx, cy, radius, borderPaint);
        }


        return desBitmap;


    }


    public static Bitmap getBitmapWithBorder(Bitmap bitmap, int outWidth, int outHeight,
                                             int border) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float widthScale = outWidth * 1f / width;
        float heightScale = outHeight * 1f / height;

        Matrix matrix = new Matrix();
        matrix.setScale(widthScale, heightScale);
        //创建底图
        Bitmap desBitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        //创建canvas
        Canvas canvas = new Canvas(desBitmap);
        //将原图绘制入底图
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        bitmapShader.setLocalMatrix(matrix);
        paint.setShader(bitmapShader);
        canvas.drawBitmap(bitmap, null, paint);
        if (border > 0) {
            //绘制boarder
            Paint boarderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            boarderPaint.setColor(Color.WHITE);
            boarderPaint.setStyle(Paint.Style.STROKE);
            boarderPaint.setStrokeWidth(border);
            Rect borderRect = new Rect(0, 0, outWidth, outHeight);
            canvas.drawRect(borderRect, boarderPaint);
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
            Rect bottomRect = new Rect(0, 0, srcWidth, srcHeight);
            canvas.drawBitmap(bitmapBottom, bottomRect, bottomRect, null);
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

    public static Bitmap layOnCenter(Bitmap bitmapBottom, Bitmap bitmapTop) {
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
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            Rect bottomRect = new Rect(0, 0, srcWidth, srcHeight);
            canvas.drawBitmap(bitmapBottom, bottomRect, bottomRect, null);
            canvas.drawBitmap(bitmapTop, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);

            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }



    public static Bitmap getBitmapFromText(String text,int textColor,int textSizePx) {
        TextPaint textPaint = new TextPaint();
        textPaint.setDither(true);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSizePx);
        textPaint.setColor(textColor);
        int width = (int) Math.ceil(textPaint.measureText(text));
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        int height = (int) Math.ceil(Math.abs(fontMetrics.bottom) + Math.abs(fontMetrics.top));
        Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(text,0,Math.abs(fontMetrics.ascent),textPaint);
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


    /**
     * 高斯模糊
     */

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Bitmap blurBitmap(Context context, Bitmap image, @FloatRange(from = 1, to = 25)
            float blurRadius) {
        // 图片缩放比例 (例如 1/10)
        int scaleRatio = 10;
        // 计算图片缩小后的长宽
        int width = Math.round(image.getWidth() * 1.0f / scaleRatio);
        int height = Math.round(image.getHeight() * 1.0f / scaleRatio);

        // 创建一张缩小后的图片做为渲染的图片
        Bitmap bitmap = Bitmap.createScaledBitmap(image, width, height, false);

        // 创建RenderScript内核对象
        RenderScript rs = RenderScript.create(context);
        // 创建一个模糊效果的RenderScript的工具对象，第二个参数Element相当于一种像素处理的算法，高斯模糊的话用这个就好
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        // 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间
        // 创建Allocation对象的时候其实内存是空的,需要使用copyTo()将数据填充进去
        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        // 创建相同类型的Allocation对象用来输出
        Type type = input.getType();
        Allocation output = Allocation.createTyped(rs, type);

        // 设置渲染的模糊程度, 25f是最大模糊度
        blurScript.setRadius(blurRadius);
        // 设置blurScript对象的输入内存
        blurScript.setInput(input);
        // 将输出数据保存到输出内存中
        blurScript.forEach(output);
        // 将数据填充到bitmap中
        output.copyTo(bitmap);

        // 销毁它们释放内存
        input.destroy();
        output.destroy();
        blurScript.destroy();
        rs.destroy();
        return bitmap;
    }

   //图片变灰
    public static Bitmap getGrayBitmap(Bitmap bitmap) {
        int width, height;
        height = bitmap.getHeight();
        width = bitmap.getWidth();

        Bitmap bmpGrayScale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayScale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bitmap, 0, 0, paint);
        return bmpGrayScale;
    }


    public interface OutPutListenener {
        void onSuccess();

        void onFailure();
    }


    public static void logWidthHeight(Bitmap bitmap) {
        logWidthHeight(TAG, bitmap);
    }

    public static void logWidthHeight(String tag, Bitmap bitmap) {
        if (bitmap == null) {
            Log.e(tag, "bitmap is null");
            return;
        }
        Log.e(tag, "width=" + bitmap.getWidth() + " height=" + bitmap.getHeight());
    }


    public static class BitmapSize {
        public int width;
        public int height;

        @Override
        public String toString() {
            return "BitmapSize{" +
                    "width=" + width +
                    ", height=" + height +
                    '}';
        }
    }
}
