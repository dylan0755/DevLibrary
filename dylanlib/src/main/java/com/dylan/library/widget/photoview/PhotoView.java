package com.dylan.library.widget.photoview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ImageView;


import com.dylan.library.utils.MatrixUtils;
import com.dylan.library.utils.ViewTouchUtils;
import com.dylan.library.widget.callback.AnimatorEndListener;
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
    private static final String TAG = "PhotoView";
    private long lastDownTime;
    private boolean doubleClick;
    private boolean hasMove;
    private boolean isDragged;
    private boolean hasImageMatrix;
    private OnTouchCallBack touchCallBack;
    public static final int RESTORE_DUTRATION = 50;
    private static final int ANIM_DURATION = 200;
    private static final int DOUBLE_INTERVAL = 250;//两次点击的间隔时间为多长会响应双击事件
    private static final int ACTION_UP_TYPE_SINGLE = 0;
    private static final int ACTION_UP_TYPE_DOUBLE = 1;
    private static final int ACTION_UP_TYPE_MOVE = 2;
    private int actionUpType;
    private PointF originalPointF;
    private float mOldDist;
    protected Matrix mMatrix;
    protected Matrix mSavedMatrix;
    private PointF mMidPoint;
    private PointF mStartPoint;
    private PointF mEndPoint;
    private float viewWidth;
    private float viewHeight;
    private float showRangeWidth;
    private float showRangeHeight;
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
    private boolean isLock;


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
            viewWidth = getMeasuredWidth();
            viewHeight = getMeasuredHeight();
            currentWidth = viewWidth;
            currentHeight = viewHeight;
            hasMeasure = true;
            //还没有进行Matrix，则开始Matrix缩放
            if (mBitmap != null && !isHasImageMatrix()) {
                prepareShowRange();
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
        if (viewWidth != 0) {
            hasImageMatrix = false;
            prepareShowRange();
        }
    }

    //提前获取范围,子类 可能重写 onMatrixWhileSettingBitmap
    private void prepareShowRange() {
        if (mBitmap == null) return;
        Rect rectF = MatrixUtils.getMatrixRectForOriginalShowRange(mBitmap, viewWidth, viewHeight);
        showRangeWidth = rectF.width();
        showRangeHeight = rectF.height();
        if (mBitmap.getWidth() > viewWidth) {
            maxWidth = mBitmap.getWidth() * 1.8f;
        } else {
            maxWidth = viewWidth * 1.8f;
        }
        if (mBitmap.getHeight() > viewHeight) {
            maxHeight = mBitmap.getHeight() * 1.8f;
        } else {
            maxHeight = viewHeight * 1.8f;
        }


        //记录矩阵后图片左上角的坐标
        originalPointF = new PointF();
        originalPointF.x = rectF.left;
        originalPointF.y = rectF.top;
        onMatrixWhileSettingBitmap(mBitmap);
        hasImageMatrix = true;
    }

    //Matrix缩放
    protected void onMatrixWhileSettingBitmap(final Bitmap bm) {
        if (bm == null) {
            super.setImageBitmap(bm);
        } else {
            mMatrix.reset();
            mSavedMatrix.reset();
            mMatrix.set(mSavedMatrix);
            //调整图片到中间铺满状态
            MatrixUtils.zoomToOriginalShowRange(mMatrix, bm, viewWidth, viewHeight);
            mSavedMatrix.set(mMatrix);
            setImageMatrix(mMatrix);
            super.setImageBitmap(bm);
        }

    }


    public boolean isHasImageMatrix() {
        return hasImageMatrix;
    }


    public void lock(){
          isLock=true;
    }

    public void unLokc(){
        isLock=false;
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

                RectF rectF = MatrixUtils.getMatrixRectF(this);
                currentWidth = rectF.width();
                currentHeight = rectF.height();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (isLock)return super.onTouchEvent(event);
                getParent().requestDisallowInterceptTouchEvent(true);
                //如果动画还在继续，则一步到位，结束动画，防止快速操作
                if (mAnimator != null && mAnimator.isRunning()) mAnimator.end();
                mOldDist = ViewTouchUtils.spacing(event);
                if (mOldDist > 10.0f) {
                    mMode = ZOOM;
                    ViewTouchUtils.midPoint(mMidPoint, event);
                    mSavedMatrix.set(mMatrix);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isLock)return super.onTouchEvent(event);
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
                    if (currentWidth > viewWidth || currentHeight > viewHeight) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        mMode = DRAG;
                        isDragged = true;
                        onDrag(event);
                    }

                }
                break;
            case MotionEvent.ACTION_POINTER_UP:  //图片的回弹
                if (isLock)return super.onTouchEvent(event);
                //双指抬起的时候若缩小至最小宽高，则恢复到最初大小
                if (currentWidth < showRangeWidth || currentHeight <= showRangeHeight) {
                    autoReboundWhilePointerUp(0, new PointF(viewWidth / 2, viewHeight / 2));
                }
                //双指抬起的时候若当前宽高都超过最大则恢复到最大
                if (currentWidth > maxWidth && currentHeight > maxHeight) {
                    autoReboundWhilePointerUp(1, mMidPoint);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mFlingRunnable.recycleVelocityTracker();
                break;
            case MotionEvent.ACTION_UP:

                if (lastDownTime == 0) {
                    lastDownTime = currentTimeMillis();
                } else {
                    long timevalue = System.currentTimeMillis() - lastDownTime;
                    lastDownTime = System.currentTimeMillis();
                    if (timevalue <= DOUBLE_INTERVAL) {//双击，放到最大
                        doubleClick(event);
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
                if (currentWidth <= viewWidth && currentHeight <= viewHeight) {
                    mMode = NONE;
                }
                resetFlag();
                break;
        }
        setImageMatrix(mMatrix);
        mGestureDetector.onTouchEvent(event);
        return true;
    }


    /**
     * 双击铺满屏
     */
    private void doubleClick(MotionEvent event) {
        //一开始就初始化满屏的情况不能放大，可以双击缩小
        if ((showRangeWidth >= viewWidth)
                && (showRangeHeight >= viewHeight)) {
            if (currentWidth > showRangeWidth && currentHeight > showRangeHeight) {
                if (!isScaling){
                    doubleClick=true;
                    doubleClickRestore(mMidPoint, ANIM_DURATION);
                }
            }else{
                doubleClick=true;
                mMidPoint.set(event.getX(), event.getY());
                doubleClickToMax(mMidPoint);
            }
        }else{
            if (currentWidth < viewWidth || currentHeight < viewHeight) {
                if (!isScaling){
                    doubleClick = true;
                    mMidPoint.set(event.getX(), event.getY());
                    doubleClickToFull(mMidPoint);//放大
                }
            } else {
                if (!isScaling) {
                    doubleClick = true;
                    mMatrix.set(mSavedMatrix);
                    mMidPoint.set(event.getX(), event.getY());
                    doubleClickRestore(mMidPoint, ANIM_DURATION);
                }

            }
        }


    }


    //放大的时候拦截事件
    private void interceptEventIfIsZoomIn() {
        if (currentWidth >= viewWidth && currentHeight >= viewHeight) {
            if (currentWidth != showRangeWidth) {
                //可能图片初始展示的位置 宽高就已经超过了控件宽高，所以要判断下初始位置
                getParent().requestDisallowInterceptTouchEvent(true);
            }

        }
    }

    private void resetFlag() {
        doubleClick = false;
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
                if (currentWidth * scaleRatio < showRangeWidth * 0.85 || currentHeight * scaleRatio < showRangeHeight * 0.85) {
                    return;
                }
            }
            currentWidth *= scaleRatio;
            currentHeight *= scaleRatio;
            //缩小的时候边缩边居中
            mMatrix.postScale(scaleRatio, scaleRatio, mMidPoint.x, mMidPoint.y);
            MatrixUtils.centerInRange(mMatrix, mBitmap, viewWidth, viewHeight);
            mSavedMatrix.set(mMatrix);
        }
        mOldDist = newDist;
    }


    //放大状态下的拖动
    private void onDrag(MotionEvent event) {
        mEndPoint.set(event.getRawX(), event.getRawY());
        float moveX = (mEndPoint.x - mStartPoint.x);
        float moveY = (mEndPoint.y - mStartPoint.y);
        mMatrix.set(mSavedMatrix);
        moveX = checkDxBound(moveX, true);
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
    private float checkDxBound(float dx, boolean isDragging) {
        float[] values = new float[9];
        mMatrix.getValues(values);
        float width = getWidth();
        float bmWidth = mBitmap.getWidth();
        if (Math.round(bmWidth * values[Matrix.MSCALE_X]) < width) {
            return 0;
        }

        if (values[Matrix.MTRANS_X] + dx > 0) {//滑到最左边
            if (isDragging) getParent().requestDisallowInterceptTouchEvent(false);
            dx = -values[Matrix.MTRANS_X];
        } else if (values[Matrix.MTRANS_X] + dx < -(bmWidth * values[Matrix.MSCALE_X] - width)) {//滑到最右边
            if (isDragging) getParent().requestDisallowInterceptTouchEvent(false);
            dx = -(bmWidth * values[Matrix.MSCALE_X] - width) - values[Matrix.MTRANS_X];
        }
        return dx;
    }

    private float checkDyBound(float dy) {
        float[] values = new float[9];
        mMatrix.getValues(values);
        float height = getHeight();
        float bmHeight = mBitmap.getHeight();
        if (Math.round(bmHeight * values[Matrix.MSCALE_Y]) < height)
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
     * @param midPint
     */
    private void autoReboundWhilePointerUp(final int mode, final PointF midPint) {
        ScaleValue startValue = new ScaleValue();
        ScaleValue endValue = new ScaleValue();
        startValue.setScaleX(1);
        startValue.setScaleY(1);
        if (mode == 0) {//回弹到最初大小
            float scaleW = currentWidth / showRangeWidth;
            float scaleH = currentHeight / showRangeHeight;
            float scale = 1 / Math.min(scaleW, scaleH);
            endValue.setScaleX(scale);
            endValue.setScaleY(scale);
        } else if (mode == 1) {//回弹到最大
            float scaleW = currentWidth / maxWidth;
            float scaleH = currentHeight / maxHeight;
            float scale = 1 / Math.min(scaleW, scaleH);
            endValue.setScaleX(scale);
            endValue.setScaleY(scale);

        }


        mAnimator = ValueAnimator.ofObject(new ScaleEvaluator(), startValue,endValue);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ScaleValue scaleValue= (ScaleValue) valueAnimator.getAnimatedValue();
                Matrix matrix = new Matrix(mMatrix);
                matrix.postScale(scaleValue.getScaleX(), scaleValue.getScaleY(), midPint.x, midPint.y);
                MatrixUtils.centerInRange(matrix, mBitmap, viewWidth, viewHeight);
                setImageMatrix(matrix);
            }
        });
        mAnimator.addListener(new AnimatorEndListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (mode==0){
                    currentWidth = showRangeWidth;
                    currentHeight = showRangeHeight;
                }else{
                    currentWidth = maxWidth;
                    currentHeight = maxHeight;
                }
                mMatrix=new Matrix(getImageMatrix());
                mSavedMatrix.set(mMatrix);
                isScaling = false;
            }
        });
        mAnimator.setDuration(200);
        mAnimator.start();
        isScaling = true;
    }




    /**
     * 双击最大化
     */
    private void doubleClickToFull(final PointF midPint) {
        ScaleValue startScaleVaue = new ScaleValue();
        startScaleVaue.setScaleX(1);
        startScaleVaue.setScaleY(1);
        ScaleValue endScaleValue = new ScaleValue();


        //计算放大 是放到 屏幕等宽还是  最大
        if (mBitmap.getHeight() > viewHeight) {
            float scaleW = viewWidth / currentWidth;
            float scaleH = mBitmap.getHeight() / currentHeight;
            float scale= Math.max(scaleW,scaleH);
            endScaleValue.setScaleX(scale);
            endScaleValue.setScaleY(scale);
        } else {
            //满屏
            float scaleW = viewWidth / currentWidth;
            float scaleH = viewHeight / currentHeight;
            float scale= Math.max(scaleW,scaleH);
            endScaleValue.setScaleX(scale);
            endScaleValue.setScaleY(scale);
        }


        ValueAnimator mAnimator = ValueAnimator.ofObject(new ScaleEvaluator(), startScaleVaue, endScaleValue);

        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ScaleValue endScaleValue = (ScaleValue) valueAnimator.getAnimatedValue();
                Matrix matrix = new Matrix(mMatrix);
                matrix.postScale(endScaleValue.getScaleX(), endScaleValue.getScaleY(), midPint.x, mMidPoint.y);
                MatrixUtils.centerInRange(matrix, mBitmap, viewWidth, viewHeight);
                setImageMatrix(matrix);
            }
        });
        mAnimator.addListener(new AnimatorEndListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                mMatrix = new Matrix(getImageMatrix());
                mSavedMatrix.set(mMatrix);
                RectF rectF = MatrixUtils.getMatrixRectF(PhotoView.this);
                currentWidth = rectF.width();
                currentHeight = rectF.height();
                isScaling = false;
            }
        });
        mAnimator.setDuration(200);
        mAnimator.start();
        isScaling = true;

    }

    private void doubleClickToMax(final PointF midPint) {
        ScaleValue startScaleVaue = new ScaleValue();
        startScaleVaue.setScaleX(1);
        startScaleVaue.setScaleY(1);
        ScaleValue endScaleValue = new ScaleValue();


        //计算放大 是放到 屏幕等宽还是  最大
        if (mBitmap.getHeight() > viewHeight) {
            float scaleW = viewWidth / currentWidth;
            float scaleH = mBitmap.getHeight() / currentHeight;
            float scale= Math.max(scaleW,scaleH);
            endScaleValue.setScaleX(scale);
            endScaleValue.setScaleY(scale);
        } else {
            //满屏
            float scaleW = maxWidth / currentWidth;
            float scaleH = maxHeight / currentHeight;
            float scale= Math.max(scaleW,scaleH);
            endScaleValue.setScaleX(scale);
            endScaleValue.setScaleY(scale);
        }


        ValueAnimator mAnimator = ValueAnimator.ofObject(new ScaleEvaluator(), startScaleVaue, endScaleValue);

        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ScaleValue endScaleValue = (ScaleValue) valueAnimator.getAnimatedValue();
                Matrix matrix = new Matrix(mMatrix);
                matrix.postScale(endScaleValue.getScaleX(), endScaleValue.getScaleY(), midPint.x, mMidPoint.y);
                MatrixUtils.centerInRange(matrix, mBitmap, viewWidth, viewHeight);
                setImageMatrix(matrix);
            }
        });
        mAnimator.addListener(new AnimatorEndListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                mMatrix = new Matrix(getImageMatrix());
                mSavedMatrix.set(mMatrix);
                RectF rectF = MatrixUtils.getMatrixRectF(PhotoView.this);
                currentWidth = rectF.width();
                currentHeight = rectF.height();
                isScaling = false;
            }
        });
        mAnimator.setDuration(200);
        mAnimator.start();
        isScaling = true;

    }

    /**
     * 双击恢复最初大小
     */
    private void doubleClickRestore(final PointF midPint, int duration) {
        final float scale = MatrixUtils.getZoomScaleToOriginalShowRange(currentWidth, currentHeight, viewWidth, viewHeight);

        ValueAnimator mAnimator = ValueAnimator.ofFloat(1, scale);
        ScaleAnimatorListener mImpl = new ScaleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation, Matrix matrix) {
                currentWidth = showRangeWidth;
                currentHeight = showRangeHeight;
                mMatrix.set(matrix);
                mSavedMatrix.set(mMatrix);
                isScaling = false;
            }

            @Override
            public Matrix onAnimationUpdate(float scaleValue) {
                if (mBitmap == null) return mMatrix;
                Matrix matrix = new Matrix(mMatrix);
                matrix.postScale(scaleValue, scaleValue, midPint.x, mMidPoint.y);
                MatrixUtils.centerInRange(matrix, mBitmap, viewWidth, viewHeight);
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


    /**
     * 恢复最初状态，在ViewPager中滑动另一个页面则需要恢复这个状态
     */
    public boolean restore() {
        boolean needRestore = false;
        if (currentWidth > viewWidth || currentHeight > viewHeight) {
            needRestore = true;
            mMode = NONE;
            resetFlag();
            if (!isScaling) doubleClickRestore(mMidPoint, 0);
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
            getParent().requestDisallowInterceptTouchEvent(true);
            mEndPoint.set(currentX, currentY);
            mMatrix.set(mSavedMatrix);
            deltaX = checkDxBound(deltaX, false);
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


    public void addOnTouchCallBack(OnTouchCallBack callBack) {
        touchCallBack = callBack;
    }


}
