package com.dylan.library.widget.photoview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;

import com.dylan.library.utils.MatrixUtils;
import com.dylan.library.widget.callback.AnimatorEndListener;
import com.dylan.library.widget.photoview.callback.OnScaleTransAnimListener;


/**
 * Author: Dylan
 * Date: 2020/2/28
 * Desc:
 */
public class ScaleUpPhotoView extends PhotoView {
    private String TAG = ScaleUpPhotoView.class.getSimpleName();
    private static final long ANIMATION_ENTER_DURATION = 100;
    private static final long ANIMATION_EXIT_DURATION = 200;
    private ViewLocation vLocation;
    private boolean canScaleAnim = true;
    private boolean haveAnim;
    private OnScaleTransAnimListener mAnimtorListener;

    public ScaleUpPhotoView(Context context) {
        this(context, null);
    }

    public ScaleUpPhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setCanScaleAnim(boolean bl) {
        canScaleAnim = bl;
    }


    public boolean  checkViewLocationVaid(ViewLocation vLocation){
      if (vLocation==null) return false;
      if (vLocation.getWidth()==0||vLocation.getHeight()==0){
          Log.e(TAG, "Location.getWidth()==0||Location.getHeight()==0 ");
          return false;
      }
      return true;
    }
    @Override
    protected void setMatrixBitmap(Bitmap bm) {
        if (checkViewLocationVaid(vLocation)&&bm!=null) {
            if (canScaleAnim && !haveAnim) {
                haveAnim = true;
                startEnterAnimation(bm, vLocation.getX(), vLocation.getY(),
                        vLocation.getWidth(), vLocation.getHeight());
            }else{
                super.setMatrixBitmap(bm);
            }
        }else{
            super.setMatrixBitmap(bm);
        }
    }




    protected void startEnterAnimation(final Bitmap bitmap, final float fromX, final float fromY, final float fromWidth, final float fromHeight) {
        //起始位置
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        final float wScale = fromWidth / width;
        final float hScale = fromHeight / height;
        mMatrix.postScale(wScale, hScale);
        mMatrix.postTranslate(fromX, fromY);


        //测量中央目的位置
        final Rect rect = MatrixUtils.getMatrixRectForOriginalShowRange(getBitmap(), getMeasuredWidth(), getMeasuredHeight());
        //最终目标位置
        final float toX = rect.left;
        final float toY = rect.top;
        final float toWidth = rect.width();
        final float toHeight = rect.height();

        //中心点坐标
        final float fromCenterX = fromX + fromWidth / 2;
        final float fromCenterY = fromY + fromHeight / 2;
        final float toCenterX = toX + toWidth / 2;
        final float toCenterY = toY + toHeight / 2;

        //平移量
        float tranX = toCenterX - fromCenterX;
        float tranY = toCenterY - fromCenterY;

        //缩放倍数
        float scaleX = toWidth / fromWidth;
        float scaleY = toHeight / fromHeight;


        TranslateScaleValue startValue = new TranslateScaleValue();
        startValue.setX(fromX);
        startValue.setY(fromY);
        startValue.setWidth(fromWidth);
        startValue.setHeight(fromHeight);
        startValue.setScaleX(1);
        startValue.setScaleY(1);
        startValue.setTranX(0);
        startValue.setTranY(0);


        final TranslateScaleValue endValue = new TranslateScaleValue();
        endValue.setX(toX);
        endValue.setY(toY);
        endValue.setWidth(toWidth);
        endValue.setHeight(toHeight);
        endValue.setScaleX(scaleX);
        endValue.setScaleY(scaleY);
        endValue.setTranX(tranX);
        endValue.setTranY(tranY);


        ValueAnimator animator = ValueAnimator.ofObject(new TranslateScaleEvaluator(), startValue, endValue);

        animator.setDuration(ANIMATION_ENTER_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setScaling(true);
                TranslateScaleValue currentLocation = (TranslateScaleValue) animation.getAnimatedValue();
                float tranX = currentLocation.getTranX();
                float tranY = currentLocation.getTranY();

                float scaleX = currentLocation.getScaleX();
                float scaleY = currentLocation.getScaleY();


                //当前的位置

                Matrix matrix = new Matrix(mMatrix);

                float updateX = currentLocation.getX();
                float updateY = currentLocation.getY();
                float updateWidth = currentLocation.getWidth();
                float updateHeight = currentLocation.getHeight();
                float centerX = updateX + updateWidth / 2;
                float centerY = updateY + updateHeight / 2;

                matrix.postTranslate(tranX, tranY);
                matrix.postScale(scaleX, scaleY, centerX, centerY);
                setImageMatrix(matrix);

                float progress = (scaleX / endValue.getScaleX());
                if (mAnimtorListener != null)
                    mAnimtorListener.onEnterAnimProgress((int) (progress*100));
            }
        });
        animator.addListener(new AnimatorEndListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                if (mAnimtorListener != null)
                    mAnimtorListener.onEnterAnimStart();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setScaling(false);
                ScaleUpPhotoView.super.setMatrixBitmap(bitmap);
                if (mAnimtorListener != null)
                    mAnimtorListener.onEnterAnimEnd();
            }
        });

        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }


    public boolean startExitAnim(ViewLocation vLocation){
        return startExitAnim(vLocation,null);
    }

    public boolean startExitAnim(ViewLocation vLocation, final AnimatorEndListener listener) {
        if (!checkViewLocationVaid(vLocation)){
            if (listener!=null)listener.onAnimationEnd(null);
            return false;
        }
        if (mMatrix==null){
            Log.e(TAG, "startExitAnim:  mMatrix is null" );
            if (listener!=null)listener.onAnimationEnd(null);
            return false;
        }
        //当前的位置信息
        PointF[] pointFS = MatrixUtils.getLocation(mMatrix, getBitmap());
        float fromX = pointFS[0].x;
        float fromY = pointFS[0].y;
        final float fromWidth = pointFS[1].x - pointFS[0].x;
        float fromHeight = pointFS[2].y - pointFS[0].y;


        TranslateScaleValue startValue = new TranslateScaleValue();
        startValue.setX(fromX);
        startValue.setY(fromY);
        startValue.setWidth(fromWidth);
        startValue.setHeight(fromHeight);

        final TranslateScaleValue endValue = new TranslateScaleValue();
        endValue.setX(vLocation.getX());
        endValue.setY(vLocation.getY());
        endValue.setWidth(vLocation.getWidth());
        endValue.setHeight(vLocation.getHeight());

        final float scaleRatio=fromWidth/vLocation.getWidth();
        ValueAnimator animator = ValueAnimator.ofObject(new TranslateScaleEvaluator(), startValue, endValue);
        animator.setDuration(ANIMATION_EXIT_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mMatrix == null || getBitmap() == null) {
                    animation.cancel();
                    return;
                }
                setScaling(true);
                TranslateScaleValue newLocation = (TranslateScaleValue) animation.getAnimatedValue();
                float newX = newLocation.getX();
                float newY = newLocation.getY();
                float newWidth = newLocation.getWidth();
                float newHeight = newLocation.getHeight();
                //当前的位置信息
                PointF[] pointFS1 = MatrixUtils.getLocation(mMatrix, getBitmap());
                float currentX = pointFS1[0].x;
                float currentY = pointFS1[0].y;
                float currentWidth = pointFS1[1].x - currentX;
                float currentHeight = pointFS1[2].y - currentY;
                float sw = newWidth / currentWidth;
                float sh = newHeight / currentHeight;
                //缩放
                mMatrix.postScale(sw, sh, currentX, currentY);
                //移动
                float dx = newX - currentX;
                float dy = newY - currentY;
                mMatrix.postTranslate(dx, dy);
                mSavedMatrix.set(mMatrix);
                setImageMatrix(mMatrix);


                //  当前缩放倍数/总缩放倍数=完成的百分比
                float progress =(fromWidth / newWidth)/scaleRatio;
                if (mAnimtorListener != null)
                    mAnimtorListener.onExitAnimProgress((int) (progress*100));

            }
        });

        animator.addListener(new AnimatorEndListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (mAnimtorListener!=null)mAnimtorListener.onExitAnimStart();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setScaling(false);
                if (mAnimtorListener != null)
                    mAnimtorListener.onExitAnimEnd();
                if (listener!=null)listener.onAnimationEnd(animation);
            }
        });
        animator.start();
        return true;
    }



    public void setLocationInfo(ViewLocation vLocation) {
        this.vLocation = vLocation;
    }


    public void setOnScaleTransAnimListener(OnScaleTransAnimListener listener) {
        mAnimtorListener = listener;
    }
}
