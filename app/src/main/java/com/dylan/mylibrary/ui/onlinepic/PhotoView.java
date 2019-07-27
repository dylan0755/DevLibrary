package com.dylan.mylibrary.ui.onlinepic;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;



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
    private boolean hasMatrix;
    private OnTouchCallBack touchCallBack;
    private static final int ANIM_DURATION=200;
    private static final int DOUBLE_INTERVAL = 300;//两次点击的间隔时间为多长会响应双击事件
    private static final int MESSAGE_DELAY = DOUBLE_INTERVAL + 50; //自定义的ACTION_UP的响应，非onTouchEvent中的ACTION_UP
    private static final int ACTION_UP_MESSAGE = 10010;
    private static final int ACTION_UP_TYPE_SINGLE = 0;
    private static final int ACTION_UP_TYPE_DOUBLE = 1;
    private static final int ACTION_UP_TYPE_MOVE=2;
    private int actionUpType;
    private float[] originMatrixLocation;
    private float mOldDist;
    private Matrix mMatrix;
    private Matrix savedMatrix;
    private PointF mMidPoint;
    private PointF mStartPoint;
    private PointF mEndPoint;
    private float parentWidth;
    private float parentHeight;
    private float minWidth;
    private float maxWidth;
    private float minHeight;
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

    public PhotoView(Context context) {
       this(context,null);
    }

    public PhotoView(Context context, AttributeSet attrs) {
        super(context,attrs);
        init(context);
    }

    private void init(Context context) {
        setScaleType(ScaleType.MATRIX);
        GestureCallBack callBack=new GestureCallBack();
        mGestureDetector=new GestureDetector(getContext(),callBack);
        mGestureDetector.setOnDoubleTapListener(callBack);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        touchSlop = configuration.getScaledTouchSlop();
        mMatrix = new Matrix();
        savedMatrix = new Matrix();
        savedMatrix.set(mMatrix);
        mMidPoint = new PointF();
        mStartPoint = new PointF();
        mEndPoint = new PointF();

    }



    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!hasMeasure&&getMeasuredWidth()!=0) {
            parentWidth = getMeasuredWidth();
            parentHeight = getMeasuredHeight();
            currentWidth = parentWidth;
            currentHeight = parentHeight;
            minWidth = parentWidth * 0.8f;
            minHeight = parentHeight * 0.8f;
            maxWidth = parentWidth * 2.0f;
            maxHeight = parentHeight * 2.0f;
            hasMeasure = true;
            //还没有进行Matrix，则开始Matrix缩放
            if (mBitmap != null && !hasMatrix) {
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
        mBitmap=bm;
        //宽高已经不为0，则直接进行矩阵缩放，如果为0则会在OnLayout 得到宽高之后再缩放
        if (parentWidth!=0){
            hasMatrix =false;
            setMatrixBitmap(mBitmap);
        }
    }

    //Matrix缩放
    private void setMatrixBitmap(Bitmap bm) {
        mMatrix.reset();
        savedMatrix.reset();
        mMatrix.set(savedMatrix);
        if (bm == null) {
            super.setImageBitmap(bm);
        }else{
            //调整图片位置
            mMatrix=ViewTouchUtils.initBitmapMatrix( mMatrix, bm,parentWidth, parentHeight);
            //记录矩阵后图片左上角的坐标
            float[] original= new float[9];
            mMatrix.getValues(original);
            originMatrixLocation=new float[2];
            originMatrixLocation[0]=original[2];
            originMatrixLocation[1]=original[5];

            savedMatrix.set(mMatrix);
            setImageMatrix(mMatrix);
            super.setImageBitmap(bm);
        }
        hasMatrix =true;
    }





    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mMode = NONE;
                mStartPoint.set(event.getX(), event.getY());
                savedMatrix.set(mMatrix);
                hasMove=false;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                //如果动画还在继续，则一步到位，结束动画，防止快速操作
                if (mAnimator!=null&&mAnimator.isRunning())mAnimator.end();
                doublePoint = true;
                mOldDist = ViewTouchUtils.spacing(event);
                if (mOldDist > 10.0f) {
                    mMode = ZOOM;
                    ViewTouchUtils.midPoint(mMidPoint, event);
                    savedMatrix.set(mMatrix);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mBitmap == null) return true;
                if (event.getPointerCount() > 2) return true;
                if (mMode == ZOOM) {
                    onZoom(event);
                } else {
                    mEndPoint.set(event.getX(), event.getY());
                    float distance = ViewTouchUtils.spacing(mEndPoint, mStartPoint);
                    if (distance < touchSlop / 8) {//滑动距离过小，相当于点击事件
                        break;
                    }
                    hasMove=true;
                    if (currentWidth > parentWidth || currentHeight > parentHeight) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        mMode = DRAG;
                        onDrag(event);
                    }

                }
                break;
            case MotionEvent.ACTION_POINTER_UP:  //图片的回弹
                if (currentWidth < parentWidth||currentHeight<parentHeight) {
                    float scaleW = currentWidth / parentWidth;
                    float scaleH=currentHeight/parentHeight;
                    float scale = 1 / Math.min(scaleW,scaleH);
                    scaleAnimator(scale, new PointF(parentWidth / 2, parentHeight / 2), parentWidth, parentHeight);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
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
                        mMatrix.set(savedMatrix);
                        mMidPoint.set(event.getX(), event.getY());
                        if (scale <= 1) {
                            scale = maxWidth / currentWidth;
                            if (!isScaling) doubleClickToMax(scale, mMidPoint);
                        } else if (scale > 1) {
                            scale = 1 / scale;
                            if (!isScaling) doubleClickRestore(scale, mMidPoint,ANIM_DURATION);
                        }
                    } else {//单击
                        doubleClick = false;
                    }
                }
                if (doubleClick) {
                    actionUpType = ACTION_UP_TYPE_DOUBLE;
                }else if (hasMove){
                    actionUpType=ACTION_UP_TYPE_MOVE;
                } else {
                    actionUpType = ACTION_UP_TYPE_SINGLE;
                }

                mMode = NONE;
                resetFlag();
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        setImageMatrix(mMatrix);
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    private void resetFlag() {
        doubleClick = false;
        doublePoint = false;
        isDragged = false;
        hasMove=false;
    }

    public boolean isOnDragMode(){
        return mMode==DRAG;
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
            mMatrix.set(savedMatrix);
           if (scaleRatio < 1) {
                if (currentWidth * scaleRatio <=minWidth || currentHeight * scaleRatio <= minHeight) {
                    return;
                }
            }
            currentWidth *= scaleRatio;
            currentHeight *= scaleRatio;
            //缩小的时候边缩边居中
            mMatrix.postScale(scaleRatio, scaleRatio, mMidPoint.x, mMidPoint.y);
            ViewTouchUtils.center(mMatrix, mBitmap, parentWidth, parentHeight);
            savedMatrix.set(mMatrix);
        }
        mOldDist = newDist;
    }


    //放大状态下的拖动
    public void onDrag(MotionEvent event) {
        Log.e( "onDrag: ","res" );
        mEndPoint.set(event.getX(), event.getY());
        float moveX = (mEndPoint.x - mStartPoint.x);
        float moveY = (mEndPoint.y - mStartPoint.y);
        isDragged = true;
        mMatrix.set(savedMatrix);
        moveX = checkDxBound(moveX);
        moveY = checkDyBound(moveY);
        mMatrix.postTranslate(moveX, moveY);
        savedMatrix.set(mMatrix);
        mStartPoint.set(event.getX(), event.getY());
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
    public  void scaleAnimator(final float scale, final PointF midPint, final float finalWidth, final float finalHeight) {
        mAnimator= ValueAnimator.ofFloat(1, scale);
        ScaleAnimatorListener listener = new ScaleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation, Matrix matrix) {
                currentWidth = finalWidth;
                currentHeight = finalHeight;
                mMatrix.set(matrix);
                savedMatrix.set(mMatrix);
                isScaling=false;
            }

            @Override
            public Matrix onAnimationUpdate(float scaleValue) {
                Matrix matrix = new Matrix(mMatrix);
                matrix.postScale(scaleValue, scaleValue, midPint.x, midPint.y);
                ViewTouchUtils.center(matrix, mBitmap, parentWidth, parentHeight);
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
                savedMatrix.set(mMatrix);
                isScaling = false;
            }

            @Override
            public Matrix onAnimationUpdate(float scaleValue) {
                Matrix matrix = new Matrix(mMatrix);
                matrix.postScale(scaleValue, scaleValue, midPint.x, mMidPoint.y);
                ViewTouchUtils.center(matrix, mBitmap, parentWidth, parentHeight);
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
    public void doubleClickRestore(final float scale, final PointF midPint,int duration) {
        ValueAnimator mAnimator = ValueAnimator.ofFloat(1, scale);
        ScaleAnimatorListener mImpl = new ScaleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation, Matrix matrix) {
                currentWidth = parentWidth;
                currentHeight = parentHeight;
                mMatrix.set(matrix);
                savedMatrix.set(mMatrix);
                isScaling = false;
            }

            @Override
            public Matrix onAnimationUpdate(float scaleValue) {
                Matrix matrix = new Matrix(mMatrix);
                matrix.postScale(scaleValue, scaleValue, midPint.x, mMidPoint.y);
                ViewTouchUtils.center(matrix, mBitmap, parentWidth, parentHeight);
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


    public float[] getOriginMatrixLocation(){
        return originMatrixLocation;
    }

    /**
     * 恢复最初状态，在ViewPager中滑动另一个页面则需要恢复这个状态
     */
    public void restore(){
        float scalew = currentWidth / parentWidth;
        float scaleh = currentHeight / parentHeight;
        float scale = Math.max(scalew, scaleh);
        if (scale > 1) {
            mMatrix.set(savedMatrix);
            mMidPoint.set(getRight()/2, getBottom()/2);
            scale = 1 / scale;
            if (!isScaling) doubleClickRestore(scale, mMidPoint,50);
        }
        mMode = NONE;
        resetFlag();
        getParent().requestDisallowInterceptTouchEvent(false);
        setImageMatrix(mMatrix);
    }


    public void addOnTouchCallBack(OnTouchCallBack callBack) {
        touchCallBack = callBack;
    }


    class GestureCallBack extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (currentWidth<=parentWidth&&currentHeight<=parentHeight){
                if (touchCallBack!=null)touchCallBack.singleActionUp();
            }

            return true;
        }

    }
}
