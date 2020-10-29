package com.dylan.library.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.dylan.library.screen.ScreenUtils;
import com.dylan.library.utils.Logger;
import com.dylan.library.utils.MathUtils;
import com.dylan.library.utils.MatrixUtils;
import com.dylan.library.widget.photoview.FlingRunnable;

import java.io.InputStream;
import java.util.List;

/**
 * Author: Administrator
 * Date: 2020/10/13
 * Desc:
 */
public abstract class DragMapView extends AppCompatImageView {
    protected Matrix mMatrix;
    protected Matrix mSavedMatrix;
    protected PointF mStartPoint;
    protected PointF mEndPoint;
    protected Bitmap sourceBitmap;//底图
    protected Bitmap mBitmap;
    private GestureDetector mGestureDetector;
    private FlingRunnable mFlingRunnable;
    protected float mImageWidth;
    protected float mImageHeight;
    protected int mScreenWidth;
    protected int mScreenHeight;
    private boolean isDragging;
    private int offsetX = 0;
    private int offsetY=0;



    public DragMapView(Context context) {
        this(context, null);
    }

    public DragMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context context) {
        mMatrix = new Matrix();
        mSavedMatrix = new Matrix();
        mSavedMatrix.set(mMatrix);
        mStartPoint = new PointF();
        mEndPoint = new PointF();
        GestureCallBack callBack = new GestureCallBack();
        mGestureDetector = new GestureDetector(getContext(), callBack);
        mGestureDetector.setOnDoubleTapListener(callBack);
        //速度
        mFlingRunnable = new FlingRunnable(context);
        mFlingRunnable.setOnFlingCallBack(new FilingCallBackImpl());
        mScreenWidth = ScreenUtils.getScreenWidth(context);
        mScreenHeight = ScreenUtils.getScreenHeight(context);
    }


    //设置底图
    public void setMapDataInputStream(InputStream inputStream) {
        sourceBitmap = BitmapFactory.decodeStream(inputStream);
        mBitmap = sourceBitmap;
        mBitmap.setDensity(getResources().getDisplayMetrics().densityDpi);
        setImageBitmap(mBitmap);
        final Rect rectF = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        mImageWidth = rectF.right;
        mImageHeight = rectF.bottom;
        Logger.e("" + mBitmap.getWidth() + "  " + mBitmap.getHeight() + "  " + rectF.toString());
        initMap();
    }

    protected void setMatrixTranslate(float dx, float dy) {
        mMatrix.setTranslate(dx, dy);
        setImageMatrix(mMatrix);
        mSavedMatrix.set(mMatrix);

        if (mBitmap!=null){
            Rect rect= MatrixUtils.getMatrixRect(mMatrix,mBitmap);
            offsetX = rect.left;
            offsetY=rect.top;
        }

    }


    protected abstract void initMap();


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mStartPoint.set(event.getRawX(), event.getRawY());
                mSavedMatrix.set(mMatrix);
                mFlingRunnable.initVelocityTracker();
                break;
            case MotionEvent.ACTION_MOVE:
                mFlingRunnable.addMovement(event);
                onDrag(event);
                Rect rect = MatrixUtils.getMatrixRect(mMatrix, mBitmap);
                offsetX = rect.left;
                offsetY=rect.top;
                break;
            case MotionEvent.ACTION_CANCEL:
                mFlingRunnable.recycleVelocityTracker();
                break;
            case MotionEvent.ACTION_UP:
                mFlingRunnable.computeCurrentVelocity();
                isDragging = false;
                break;
        }
        setImageMatrix(mMatrix);
        mGestureDetector.onTouchEvent(event);
        return true;
    }


    private void onDrag(MotionEvent event) {
        mEndPoint.set(event.getRawX(), event.getRawY());
        float moveX = (mEndPoint.x - mStartPoint.x);
        float moveY = (mEndPoint.y - mStartPoint.y);
        mMatrix.set(mSavedMatrix);
        moveX = checkDxBound(moveX, true);
        moveY = checkDyBound(moveY);
        mMatrix.postTranslate(moveX, moveY);
        mSavedMatrix.set(mMatrix);
        mStartPoint.set(mEndPoint.x, mEndPoint.y);
        isDragging = true;
    }


    /**
     * 拖动时左右边界控制
     *
     * @param dx
     * @return
     */
    private float checkDxBound(float dx, boolean isDragging) {
        float[] values = new float[9];
        mMatrix.getValues(values);
        float width = getWidth();
        float transX = values[Matrix.MTRANS_X];
        float scaleX = values[Matrix.MSCALE_X];
        if (Math.round(mImageWidth * scaleX) < width) {
            return 0;
        }


        if (transX + dx > 0) {//滑到最左边
            if (isDragging) getParent().requestDisallowInterceptTouchEvent(false);
            dx = -transX;
        } else if (transX + dx < -(mImageWidth * scaleX - width)) {//滑到最右边
            if (isDragging) getParent().requestDisallowInterceptTouchEvent(false);
            dx = -(mImageWidth * scaleX - width) - transX;
        }
        return dx;
    }

    private float checkDyBound(float dy) {
        float[] values = new float[9];
        mMatrix.getValues(values);
        float height = getHeight();
        if (Math.round(mImageHeight * values[Matrix.MSCALE_Y]) < height)
            return 0;
        if (values[Matrix.MTRANS_Y] + dy > 0)
            dy = -values[Matrix.MTRANS_Y];
        else if (values[Matrix.MTRANS_Y] + dy < -(mImageHeight * values[Matrix.MSCALE_Y] - height))
            dy = -(mImageHeight * values[Matrix.MSCALE_Y] - height) - values[Matrix.MTRANS_Y];
        return dy;
    }


    /**
     * 松手后的惯性滑动
     */
    class FilingCallBackImpl implements FlingRunnable.OnFlingCallBack {
        @Override
        public ImageView getImageView() {
            return DragMapView.this;
        }

        @Override
        public void onFiling(int currentX, int currentY, float deltaX, float deltaY) {
            getParent().requestDisallowInterceptTouchEvent(true);
            mEndPoint.set(currentX, currentY);
            mMatrix.set(mSavedMatrix);
            deltaX = checkDxBound(deltaX, false);
            deltaY = checkDyBound(deltaY);
            mMatrix.postTranslate(deltaX, deltaY);
            mSavedMatrix.set(mMatrix);
            mStartPoint.set(currentX, currentY);
            setImageMatrix(mMatrix);

            Rect rect = MatrixUtils.getMatrixRect(mMatrix, mBitmap);
            offsetX = rect.left;
            offsetY=rect.top;
            DragMapView.this.onFiling(currentX, currentY, deltaX, deltaY);

        }

    }


    protected void drawPolygon(List<Point> pointFS) {
        Bitmap tempBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        tempBitmap.setDensity(getResources().getDisplayMetrics().densityDpi);
        Canvas canvas = new Canvas(tempBitmap);
        Rect bottomRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        canvas.drawBitmap(mBitmap, bottomRect, bottomRect, null);
        //图像上画矩形
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);//不填充
        paint.setStrokeWidth(10);  //线的宽度
        canvas.drawLines(MathUtils.convertPointsToLinesFloatArray(pointFS), paint);
        mBitmap = tempBitmap;
        setImageBitmap(tempBitmap);
    }


    public boolean isDragging() {
        return isDragging;
    }


    public boolean isScrolling() {
        return mFlingRunnable.isScrolling();
    }


    public Matrix getmMatrix() {
        return mMatrix;
    }

    public void setmMatrix(Matrix mMatrix) {
        this.mMatrix = mMatrix;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    class GestureCallBack extends GestureDetector.SimpleOnGestureListener {


        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onClickScene(e);
        }
    }


    public abstract boolean onClickScene(MotionEvent e);

    public abstract void onFiling(int currentX, int currentY, float deltaX, float deltaY);

}
