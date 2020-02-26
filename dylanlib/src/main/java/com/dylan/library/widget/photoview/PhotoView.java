package com.dylan.library.widget.photoview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ImageView;


import com.dylan.library.utils.MatrixUtils;
import com.dylan.library.utils.ViewTouchUtils;
import com.dylan.library.widget.callback.OnTouchCallBack;
import com.dylan.library.widget.callback.ScaleAnimatorListener;

import static java.lang.System.currentTimeMillis;

/**
 * Created by Dylan on 2017/5/17.
 */

/**
 * 关键点   onMeasure 中会调用多次，
 * 所以第一次的高度未必就是正确的，所以不能取第一次测量的值
 */
public class PhotoView extends AppCompatImageView {
    private long lastDownTime;
    private boolean doublePoint;
    private boolean doubleClick;
    private boolean hasMove;
    private boolean isDragged;
    private boolean hasImageMatrix;
    private OnTouchCallBack touchCallBack;
    public static final int RESTORE_DUTRATION = 50;
    private static final int ANIM_DURATION = 200;
    private static final int DOUBLE_INTERVAL = 300;//两次点击的间隔时间为多长会响应双击事件
    private static final int ACTION_UP_TYPE_SINGLE = 0;
    private static final int ACTION_UP_TYPE_DOUBLE = 1;
    private static final int ACTION_UP_TYPE_MOVE = 2;
    private int actionUpType;
    private PointF originalPointF;
    private PointF[] originalMatrixPoints;
    private float mOldDist;
    protected Matrix mMatrix;
    protected Matrix mSavedMatrix;
    private PointF mMidPoint;
    private PointF mStartPoint;
    private PointF mEndPoint;
    private float parentWidth;
    private float parentHeight;
    private float minWidth;
    private float minHeight;
    private float maxWidth;
    private float maxHeight;
    private float currentWidth;
    private float currentHeight;
    private Bitmap mBitmap;
    private int touchSlop;
    private boolean hasMeasure;
    private boolean isScaling = false;//是否正在缩放
    private ValueAnimator mAnimator;
    private final int ZOOM = 1001;
    private final int DRAG = 1002;
    private final int NONE = 1003;
    private int mMode = NONE;
    private GestureDetector mGestureDetector;

    private FlingRunnable mFlingRunnable;


    public PhotoView(Context context) {
        this(context, null);
    }

    public PhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setScaleType(ScaleType.MATRIX);
        GestureCallBack callBack = new GestureCallBack();
        mGestureDetector = new GestureDetector(getContext(), callBack);
        mGestureDetector.setOnDoubleTapListener(callBack);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        touchSlop = configuration.getScaledTouchSlop();
        mMatrix = new Matrix();
        mSavedMatrix = new Matrix();
        mSavedMatrix.set(mMatrix);
        mMidPoint = new PointF();
        mStartPoint = new PointF();
        mEndPoint = new PointF();
        //速度
        mFlingRunnable = new FlingRunnable(context);
        mFlingRunnable.setOnFlingCallBack(new FilingCallBackImpl());

    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!hasMeasure && getMeasuredWidth() != 0) {
            parentWidth = getMeasuredWidth();
            parentHeight = getMeasuredHeight();
            currentWidth = parentWidth;
            currentHeight = parentHeight;
            minWidth = parentWidth * 0.8f;
            minHeight = parentHeight * 0.8f;
            maxWidth = parentWidth * 4.0f;
            maxHeight = parentHeight * 4.0f;
            hasMeasure = true;
            //还没有进行Matrix，则开始Matrix缩放
            if (mBitmap != null && !isHasImageMatrix()) {
                setMatrixBitmap(mBitmap);
            }
        }
    }


    /**
     * 设置图片进来的时候设置图片居中显示
     *
     * @param bm
     */
    @Override
    public void setImageBitmap(final Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        //宽高已经不为0，则直接进行矩阵缩放，如果为0则会在OnLayout 得到宽高之后再缩放
        if (parentWidth != 0) {
            hasImageMatrix = false;
            setMatrixBitmap(mBitmap);
        }
    }

    //Matrix缩放
    protected void setMatrixBitmap(final Bitmap bm) {
        mMatrix.reset();
        mSavedMatrix.reset();
        mMatrix.set(mSavedMatrix);
        if (bm == null) {
            super.setImageBitmap(bm);
        } else {
            boolean hasDo = false;
            if (matrixListener != null) {
                hasDo = matrixListener.preMatrix(this, mMatrix);
            }
            if (!hasDo) {
                //调整图片到中间铺满状态
                MatrixUtils.zoomOutToMaxRange(mMatrix, bm, parentWidth, parentHeight);
                MatrixUtils.centerInRange(mMatrix, bm, parentWidth, parentHeight);
            }
            //记录矩阵后图片左上角的坐标
            originalMatrixPoints = MatrixUtils.getLocation(mMatrix, mBitmap);
            originalPointF = originalMatrixPoints[0];
            mSavedMatrix.set(mMatrix);
            hasImageMatrix = true;
            setImageMatrix(mMatrix);
            super.setImageBitmap(bm);


        }

    }


    public boolean isHasImageMatrix() {
        return hasImageMatrix;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        interceptEventIfIsZoomIn();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mMode = NONE;
                mStartPoint.set(event.getRawX(), event.getRawY());
                mSavedMatrix.set(mMatrix);
                hasMove = false;
                mFlingRunnable.initVelocityTracker();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                //如果动画还在继续，则一步到位，结束动画，防止快速操作
                if (mAnimator != null && mAnimator.isRunning()) mAnimator.end();
                doublePoint = true;
                mOldDist = ViewTouchUtils.spacing(event);
                if (mOldDist > 10.0f) {
                    mMode = ZOOM;
                    ViewTouchUtils.midPoint(mMidPoint, event);
                    mSavedMatrix.set(mMatrix);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mBitmap == null) return true;
                if (event.getPointerCount() > 2) return true;
                 mFlingRunnable.addMovement(event);
                if (mMode == ZOOM) {
                    onZoom(event);
                } else {
                    mEndPoint.set(event.getRawX(), event.getRawY());
                    float distance = ViewTouchUtils.spacing(mEndPoint, mStartPoint);
                    if (distance < touchSlop / 8) {//滑动距离过小，相当于点击事件
                        break;
                    }
                    hasMove = true;
                    if (currentWidth > parentWidth || currentHeight > parentHeight) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        mMode = DRAG;
                        isDragged = true;
                        onDrag(event);
                    }

                }
                break;
            case MotionEvent.ACTION_POINTER_UP:  //图片的回弹
                if (currentWidth < parentWidth || currentHeight < parentHeight) {
                    float scaleW = currentWidth / parentWidth;
                    float scaleH = currentHeight / parentHeight;
                    float scale = 1 / Math.min(scaleW, scaleH);
                    scaleAnimator(scale, new PointF(parentWidth / 2, parentHeight / 2), parentWidth, parentHeight);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                mFlingRunnable.recycleVelocityTracker();
                break;
            case MotionEvent.ACTION_UP:
                if (doublePoint) {
                    resetFlag();
                    if (currentWidth > maxWidth || currentHeight > maxHeight) {//双指抬起的时候若超过最大宽高则缩至最大宽高
                        float scalew = currentWidth / maxWidth;
                        float scaleh = currentHeight / maxHeight;
                        float scale = Math.max(scalew, scaleh);
                        scale = 1 / scale;
                        scaleAnimator(scale, mMidPoint, maxWidth, maxHeight);
                    }
                    if (lastDownTime == 0) lastDownTime = currentTimeMillis();
                    return true;
                }


                if (lastDownTime == 0) {
                    lastDownTime = currentTimeMillis();
                } else {
                    long timevalue = System.currentTimeMillis() - lastDownTime;
                    lastDownTime = System.currentTimeMillis();
                    if (timevalue <= DOUBLE_INTERVAL) {//双击，放到最大
                        doubleClick = true;
                        //双击以触摸点为中心放到最大，或恢复原来的宽高
                        float scalew = currentWidth / parentWidth;
                        float scaleh = currentHeight / parentHeight;
                        float scale = Math.max(scalew, scaleh);
                        mMatrix.set(mSavedMatrix);
                        mMidPoint.set(event.getRawX(), event.getRawY());
                        if (scale <= 1) {
                            scale = maxWidth / currentWidth;
                            if (!isScaling) doubleClickToMax(scale, mMidPoint);
                        } else if (scale > 1) {
                            scale = 1 / scale;
                            if (!isScaling) doubleClickRestore(scale, mMidPoint, ANIM_DURATION);
                        }
                    } else {//单击
                        doubleClick = false;
                    }
                }

                if (doubleClick) {
                    actionUpType = ACTION_UP_TYPE_DOUBLE;
                } else if (hasMove) {
                    actionUpType = ACTION_UP_TYPE_MOVE;
                } else {
                    actionUpType = ACTION_UP_TYPE_SINGLE;
                }


                if (isDragged) {
                 mFlingRunnable.computeCurrentVelocity();
                }

                if (currentWidth <= parentWidth && currentHeight <= parentHeight) {
                    mMode = NONE;
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                resetFlag();

                break;
        }
        setImageMatrix(mMatrix);
        mGestureDetector.onTouchEvent(event);
        return true;
    }




    //放大的时候拦截事件
    private void interceptEventIfIsZoomIn() {
        if (currentWidth > parentWidth || currentHeight > parentHeight) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    private void resetFlag() {
        doubleClick = false;
        doublePoint = false;
        isDragged = false;
        hasMove = false;
    }

    public boolean isOnDragMode() {
        return mMode == DRAG;
    }


    /**
     * 触摸放大或缩小
     *
     * @param event
     */
    private void onZoom(MotionEvent event) {
        float newDist = ViewTouchUtils.spacing(event);
        if (newDist > 10f) { // 若两指的距离大于10dp,则进行缩放
            float scaleRatio = newDist / mOldDist;//放大的倍数
            mMatrix.set(mSavedMatrix);
            if (scaleRatio < 1) {
                if (currentWidth * scaleRatio <= minWidth || currentHeight * scaleRatio <= minHeight) {
                    return;
                }
            }
            currentWidth *= scaleRatio;
            currentHeight *= scaleRatio;
            //缩小的时候边缩边居中
            mMatrix.postScale(scaleRatio, scaleRatio, mMidPoint.x, mMidPoint.y);
            MatrixUtils.centerInRange(mMatrix, mBitmap, parentWidth, parentHeight);
            mSavedMatrix.set(mMatrix);
        }
        mOldDist = newDist;
    }


    //放大状态下的拖动
    public void onDrag(MotionEvent event) {
        mEndPoint.set(event.getRawX(), event.getRawY());
        float moveX = (mEndPoint.x - mStartPoint.x);
        float moveY = (mEndPoint.y - mStartPoint.y);
        mMatrix.set(mSavedMatrix);
        moveX = checkDxBound(moveX);
        moveY = checkDyBound(moveY);
        mMatrix.postTranslate(moveX, moveY);
        mSavedMatrix.set(mMatrix);
        mStartPoint.set(event.getRawX(), event.getRawY());
    }


    /**
     * 拖动时左右边界控制
     *
     * @param dx
     * @return
     */
    private float checkDxBound(float dx) {
        float[] values = new float[9];
        mMatrix.getValues(values);
        float width = getWidth();
        float bmWidth = mBitmap.getWidth();
        if (bmWidth * values[Matrix.MSCALE_X] < width)
            return 0;
        if (values[Matrix.MTRANS_X] + dx > 0) {//滑到最左边
            if (values[Matrix.MTRANS_X] + dx > touchSlop / 4)
                getParent().requestDisallowInterceptTouchEvent(false);
            dx = -values[Matrix.MTRANS_X];
        } else if (values[Matrix.MTRANS_X] + dx < -(bmWidth * values[Matrix.MSCALE_X] - width)) {//滑到最右边
            if (values[Matrix.MTRANS_X] + dx < -(bmWidth * values[Matrix.MSCALE_X] - width) + touchSlop / 4)
                getParent().requestDisallowInterceptTouchEvent(false);
            dx = -(bmWidth * values[Matrix.MSCALE_X] - width) - values[Matrix.MTRANS_X];
        }
        return dx;
    }

    private float checkDyBound(float dy) {
        float[] values = new float[9];
        mMatrix.getValues(values);
        float height = getHeight();
        float bmHeight = mBitmap.getHeight();
        if (bmHeight * values[Matrix.MSCALE_Y] < height)
            return 0;
        if (values[Matrix.MTRANS_Y] + dy > 0)
            dy = -values[Matrix.MTRANS_Y];
        else if (values[Matrix.MTRANS_Y] + dy < -(bmHeight * values[Matrix.MSCALE_Y] - height))
            dy = -(bmHeight * values[Matrix.MSCALE_Y] - height) - values[Matrix.MTRANS_Y];
        return dy;
    }


    /**
     * 从最小回弹到原宽高或者回弹到最大宽高
     *
     * @param scale
     * @param midPint
     */
    public void scaleAnimator(final float scale, final PointF midPint, final float finalWidth, final float finalHeight) {
        mAnimator = ValueAnimator.ofFloat(1, scale);
        ScaleAnimatorListener listener = new ScaleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation, Matrix matrix) {
                currentWidth = finalWidth;
                currentHeight = finalHeight;
                mMatrix.set(matrix);
                mSavedMatrix.set(mMatrix);
                isScaling = false;
            }

            @Override
            public Matrix onAnimationUpdate(float scaleValue) {
                if (mBitmap == null) return mMatrix;
                Matrix matrix = new Matrix(mMatrix);
                matrix.postScale(scaleValue, scaleValue, midPint.x, midPint.y);
                MatrixUtils.centerInRange(matrix, mBitmap, parentWidth, parentHeight);
                setImageMatrix(matrix);
                return matrix;
            }
        };
        mAnimator.addUpdateListener(listener);
        mAnimator.addListener(listener);
        mAnimator.setDuration(200);
        mAnimator.start();
        isScaling = true;
    }


    /**
     * 双击最大化
     */
    public void doubleClickToMax(final float scale, final PointF midPint) {
        ValueAnimator mAnimator = ValueAnimator.ofFloat(1, scale);
        ScaleAnimatorListener mImpl = new ScaleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation, Matrix matrix) {
                currentWidth = maxWidth;
                currentHeight = maxHeight;
                mMatrix.set(matrix);
                mSavedMatrix.set(mMatrix);
                isScaling = false;
            }

            @Override
            public Matrix onAnimationUpdate(float scaleValue) {
                if (mBitmap == null) return mMatrix;
                Matrix matrix = new Matrix(mMatrix);
                matrix.postScale(scaleValue, scaleValue, midPint.x, mMidPoint.y);
                MatrixUtils.centerInRange(matrix, mBitmap, parentWidth, parentHeight);
                setImageMatrix(matrix);
                return matrix;
            }
        };
        mAnimator.addUpdateListener(mImpl);
        mAnimator.addListener(mImpl);
        mAnimator.setDuration(200);
        mAnimator.start();
        isScaling = true;

    }

    /**
     * 双击恢复最初大小
     */
    public void doubleClickRestore(final float scale, final PointF midPint, int duration) {
        ValueAnimator mAnimator = ValueAnimator.ofFloat(1, scale);
        ScaleAnimatorListener mImpl = new ScaleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation, Matrix matrix) {
                currentWidth = parentWidth;
                currentHeight = parentHeight;
                mMatrix.set(matrix);
                mSavedMatrix.set(mMatrix);
                isScaling = false;
            }

            @Override
            public Matrix onAnimationUpdate(float scaleValue) {
                if (mBitmap == null) return mMatrix;
                Matrix matrix = new Matrix(mMatrix);
                matrix.postScale(scaleValue, scaleValue, midPint.x, mMidPoint.y);
                MatrixUtils.centerInRange(matrix, mBitmap, parentWidth, parentHeight);
                setImageMatrix(matrix);
                return matrix;
            }
        };

        mAnimator.addUpdateListener(mImpl);
        mAnimator.addListener(mImpl);
        mAnimator.setDuration(duration);
        mAnimator.start();
        isScaling = true;
    }

    public void setScaling(boolean bl) {
        isScaling = bl;
    }


    public Bitmap getBitmap() {
        return mBitmap;
    }


    public PointF getOriginalPointF() {
        return originalPointF;
    }

    public PointF[] getOriginalMatrixPoints() {
        return originalMatrixPoints;
    }

    public float getParentWidth() {
        return parentWidth;
    }

    public float getParentHeight() {
        return parentHeight;
    }

    /**
     * 恢复最初状态，在ViewPager中滑动另一个页面则需要恢复这个状态
     */
    public boolean restore() {
        boolean needRestore = false;
        float scalew = currentWidth / parentWidth;
        float scaleh = currentHeight / parentHeight;
        float scale = Math.max(scalew, scaleh);
        if (scale > 1) {
            needRestore = true;
            mMatrix.set(mSavedMatrix);
            mMidPoint.set(getRight() / 2, getBottom() / 2);
            scale = 1 / scale;
            if (!isScaling) doubleClickRestore(scale, mMidPoint, RESTORE_DUTRATION);
            mMode = NONE;
            resetFlag();
            getParent().requestDisallowInterceptTouchEvent(false);
            setImageMatrix(mMatrix);
        }
        return needRestore;

    }


    /**
     * 松手后的惯性滑动
     */
    class FilingCallBackImpl implements FlingRunnable.OnFlingCallBack {
        @Override
        public ImageView getImageView() {
            return PhotoView.this;
        }

        @Override
        public void onFiling(int currentX, int currentY, float deltaX, float deltaY) {
            mEndPoint.set(currentX, currentY);
            mMatrix.set(mSavedMatrix);
            deltaX = checkDxBound(deltaX);
            deltaY = checkDyBound(deltaY);
            mMatrix.postTranslate(deltaX, deltaY);
            mSavedMatrix.set(mMatrix);
            mStartPoint.set(currentX, currentY);
            setImageMatrix(mMatrix);
        }


    }

    class GestureCallBack extends GestureDetector.SimpleOnGestureListener {
        private long gestureLastDownTime;
        private int gestureDoubleInterval = 300;

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            //防止同时调两次
            if (gestureLastDownTime == 0) {
                gestureLastDownTime = currentTimeMillis();
                if (touchCallBack != null) touchCallBack.singleActionUp();
            } else {
                long timevalue = System.currentTimeMillis() - gestureLastDownTime;
                gestureLastDownTime = System.currentTimeMillis();
                if (timevalue > gestureDoubleInterval) {
                    if (touchCallBack != null) touchCallBack.singleActionUp();
                }
            }
            return true;
        }

    }


    private OnFirstImageMatrixListener matrixListener;

    public interface OnFirstImageMatrixListener {
        boolean preMatrix(PhotoView photoView, Matrix matrix);

    }

    public void setOnFirstImageMatrixListener(OnFirstImageMatrixListener listener) {
        matrixListener = listener;
    }

    public void addOnTouchCallBack(OnTouchCallBack callBack) {
        touchCallBack = callBack;
    }


}
