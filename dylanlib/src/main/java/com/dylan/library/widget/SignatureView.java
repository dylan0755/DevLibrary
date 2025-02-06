package com.dylan.library.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2025/1/10
 * Desc:
 */
public class SignatureView extends View {

    /**
     * 笔画X坐标起点
     */
    private float mX;
    /**
     * 笔画Y坐标起点
     */
    private float mY;
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 路径
     */
    private Path mPath;
    /**
     * 记录每一个笔画的对象
     */
    private DrawPath mDrawPath;
    /**
     * 签名bitmap画布
     */
    private Canvas mBitmapCanvas;
    /**
     * 签名位图
     */
    private Bitmap mBitmap;
    /**
     * 画笔宽度
     */
    private int mPaintWidth = 10;
    /**
     * 笔画颜色
     */
    private int mPaintColor = Color.BLACK;
    /**
     * 背景色
     */
    private int mBgColor = Color.parseColor("#EFEFEF");

    /**
     * mSavePath：保存笔画的集合，list有序保存;mDeletePath：撤销的笔画
     */
    private List<DrawPath> mSavePath,mDeletePath;


    /**
     * 笔画路径和画笔储存
     */
    public class DrawPath {
        public Path path;// 路径
        public Paint paint;// 画笔
    }

    public SignatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setPanit();
        initCanvas();

        mSavePath = new ArrayList<DrawPath>();
        mDeletePath = new ArrayList<DrawPath>();
    }

    /**
     * 设置画笔
     */
    private void setPanit() {
        mPaint = new Paint();
        //设置抗锯齿
        mPaint.setAntiAlias(true);
        //设置签名笔画样式
        mPaint.setStyle(Paint.Style.STROKE);
        //设置笔画宽度
        mPaint.setStrokeWidth(mPaintWidth);
        //设置签名颜色
        mPaint.setColor(mPaintColor);
    }

    /**
     * 初始化bitmap、画布
     */
    private void initCanvas() {
        //创建跟view一样大的bitmap，用来保存签名
        mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mBitmapCanvas = new Canvas(mBitmap);
        mBitmapCanvas.drawColor(mBgColor);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                //将路径画到bitmap中，即一次笔画完成才去更新bitmap，而手势轨迹是实时显示在画板上的。
                mBitmapCanvas.drawPath(mPath, mPaint);
//                mPath.reset();
                //将一条完整的路径保存下来(相当于入栈操作)
                mSavePath.add(mDrawPath);
                mPath = null;// 重新置空
                break;
        }
        // 更新绘制
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画此次笔画之前的签名
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
        // 通过画布绘制多点形成的图形
        if(mPath != null) {//当手指滑动时也实时画上
            canvas.drawPath(mPath, mPaint);
        }
    }

    // 手指点下屏幕时调用
    private void touchDown(MotionEvent event) {
        // 每次按下都是新的一笔，创建新的path
        mPath = new Path();
        float x = event.getX();
        float y = event.getY();
        mX = x;
        mY = y;
        // mPath绘制的绘制起点
        mPath.moveTo(x, y);
        //新的笔画存在新的对象里，方便撤回操作
        mDrawPath = new DrawPath();
        mDrawPath.path = mPath;
        mDrawPath.paint = mPaint;
    }

    // 手指在屏幕上滑动时调用
    private void touchMove(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();
        final float previousX = mX;
        final float previousY = mY;
        final float dx = Math.abs(x - previousX);
        final float dy = Math.abs(y - previousY);
        // 两点之间的距离大于等于3时，生成贝塞尔绘制曲线
        if (dx >= 3 || dy >= 3) {
            // 设置贝塞尔曲线的操作点为起点和终点的一半
            float cX = (x + previousX) / 2;
            float cY = (y + previousY) / 2;
            // 二次贝塞尔，实现平滑曲线；previousX, previousY为操作点，cX, cY为终点
            mPath.quadTo(previousX, previousY, cX, cY);
            // 第二次执行时，第一次结束调用的坐标值将作为第二次调用的初始坐标值
            mX = x;
            mY = y;
        }
    }

    /**
     * 撤销上一步
     */
    public void goBack() {
        if (mSavePath != null && mSavePath.size() > 0) {
            DrawPath drawPath = mSavePath.get(mSavePath.size() - 1);
            mDeletePath.add(drawPath);
            mSavePath.remove(mSavePath.size() - 1);
            redrawBitmap();
        }
    }

    /**
     * 前进
     */
    public void goForward() {
        if (mDeletePath != null && mDeletePath.size() > 0) {
            DrawPath drawPath = mDeletePath.get(mDeletePath.size()-1);
            mSavePath.add(drawPath);
            mDeletePath.remove(mDeletePath.size()-1);
            redrawBitmap();
        }
    }

    /**
     * 重画bitmap
     */
    private void redrawBitmap() {
        /*mBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
                Bitmap.Config.RGB_565);
        mCanvas.setBitmap(mBitmap);// 重新设置画布，相当于清空画布*/
        initCanvas();
        Iterator<DrawPath> iter = mSavePath.iterator();
        while (iter.hasNext()) {
            DrawPath drawPath = iter.next();
            mBitmapCanvas.drawPath(drawPath.path, drawPath.paint);
        }
        invalidate();// 刷新
    }
    /**
     * 清除画板
     */
    public void clear() {
        if (mSavePath != null && mSavePath.size() > 0) {
            mSavePath.clear();
            mDeletePath.clear();
            redrawBitmap();
        }
    }

    /**
     * 保存画板
     *
     * @param path 保存到路径
     */
    public void save(String path) throws IOException {
        save(path, false, 0);
    }

    /**
     * 保存画板
     *
     * @param path       保存到路径
     * @param clearBlank 是否清除边缘空白区域
     * @param blank      要保留的边缘空白距离
     */
    public void save(String path, boolean clearBlank, int blank) throws IOException {

        Bitmap bitmap = mBitmap;
        //BitmapUtil.createScaledBitmapByHeight(srcBitmap, 300);//  压缩图片
        if (clearBlank) {
            bitmap = clearBlank(bitmap, blank);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] buffer = bos.toByteArray();
        if (buffer != null) {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            OutputStream outputStream = new FileOutputStream(file);
            outputStream.write(buffer);
            outputStream.close();
        }
    }

    /**
     * 获取画板的bitmap
     *
     * @return
     */
    public Bitmap getBitMap() {
        setDrawingCacheEnabled(true);
        buildDrawingCache();
        Bitmap bitmap = getDrawingCache();
        setDrawingCacheEnabled(false);
        return bitmap;
    }

    /**
     * 逐行扫描 清楚边界空白。
     *
     * @param bp
     * @param blank 边距留多少个像素
     * @return
     */
    private Bitmap clearBlank(Bitmap bp, int blank) {
        int HEIGHT = bp.getHeight();
        int WIDTH = bp.getWidth();
        int top = 0, left = 0, right = 0, bottom = 0;
        int[] pixs = new int[WIDTH];
        boolean isStop;
        //扫描上边距不等于背景颜色的第一个点
        for (int y = 0; y < HEIGHT; y++) {
            bp.getPixels(pixs, 0, WIDTH, 0, y, WIDTH, 1);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBgColor) {
                    top = y;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        //扫描下边距不等于背景颜色的第一个点
        for (int y = HEIGHT - 1; y >= 0; y--) {
            bp.getPixels(pixs, 0, WIDTH, 0, y, WIDTH, 1);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBgColor) {
                    bottom = y;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        pixs = new int[HEIGHT];
        //扫描左边距不等于背景颜色的第一个点
        for (int x = 0; x < WIDTH; x++) {
            bp.getPixels(pixs, 0, 1, x, 0, 1, HEIGHT);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBgColor) {
                    left = x;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        //扫描右边距不等于背景颜色的第一个点
        for (int x = WIDTH - 1; x > 0; x--) {
            bp.getPixels(pixs, 0, 1, x, 0, 1, HEIGHT);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBgColor) {
                    right = x;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        if (blank < 0) {
            blank = 0;
        }
        //计算加上保留空白距离之后的图像大小
        left = left - blank > 0 ? left - blank : 0;
        top = top - blank > 0 ? top - blank : 0;
        right = right + blank > WIDTH - 1 ? WIDTH - 1 : right + blank;
        bottom = bottom + blank > HEIGHT - 1 ? HEIGHT - 1 : bottom + blank;
        return Bitmap.createBitmap(bp, left, top, right - left, bottom - top);
    }

    /**
     * 设置画笔宽度 默认宽度为10px
     * 这边设置可以对接下来的笔画生效
     * @param mPaintWidth
     */
    public void setPaintWidth(int mPaintWidth) {
        mPaintWidth = mPaintWidth > 0 ? mPaintWidth : 10;
        this.mPaintWidth = mPaintWidth;
        setPanit();

    }


    public void setBgColor(@ColorInt int backColor) {
        mBgColor = backColor;

    }


    /**
     * 设置画笔颜色
     * 这边设置可以对接下来的笔画生效
     * @param paintColor 画笔颜色
     */
    public void setPaintColor(int paintColor) {
        this.mPaintColor = paintColor;
        setPanit();
    }

    /**
     * 是否有签名,根据是否有笔画来判断
     * @return
     */
    public boolean isSign() {
        if(mSavePath != null && mSavePath.size()>0) {
            return true;
        }else{
            return false;
        }
    }
}
