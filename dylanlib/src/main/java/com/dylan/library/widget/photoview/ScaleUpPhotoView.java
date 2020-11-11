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


public class ScaleUpPhotoView  extends PhotoView {
    private String TAG;
    public static final long ANIMATION_ENTER_DURATION = 100L;
    public static final long ANIMATION_EXIT_DURATION = 200L;
    private ViewLocation vLocation;
    private boolean canScaleAnim;
    private boolean haveAnim;
    private OnScaleTransAnimListener mAnimtorListener;
    private ScaleUpPhotoView.OnPrepareMatrixListener onPrepareMatrixListener;

    public ScaleUpPhotoView(Context context) {
        this(context, (AttributeSet)null);
    }

    public ScaleUpPhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = ScaleUpPhotoView.class.getSimpleName();
        this.canScaleAnim = true;
    }

    public void setCanScaleAnim(boolean bl) {
        this.canScaleAnim = bl;
    }

    public static boolean checkViewLocationVaid(ViewLocation vLocation) {
        if (vLocation == null) {
            return false;
        } else if (vLocation.getWidth() != 0.0F && vLocation.getHeight() != 0.0F) {
            return true;
        } else {
            Log.e(ScaleUpPhotoView.class.getSimpleName(), "Location.getWidth()==0||Location.getHeight()==0 ");
            return false;
        }
    }

    protected void onMatrixWhileSettingBitmap(Bitmap bm) {
        if (this.onPrepareMatrixListener != null && bm != null) {
            this.onPrepareMatrixListener.prepareMatrix(this, bm);
        }

        if (checkViewLocationVaid(this.vLocation) && bm != null) {
            if (this.canScaleAnim && !this.haveAnim) {
                this.haveAnim = true;
                this.startEnterAnimation(bm, this.vLocation);
            } else {
                super.onMatrixWhileSettingBitmap(bm);
            }
        } else {
            super.onMatrixWhileSettingBitmap(bm);
        }

    }

    protected void startEnterAnimation(Bitmap bitmap, ViewLocation viewLocation) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float wScale = viewLocation.getWidth() / (float)width;
        float hScale = viewLocation.getHeight() / (float)height;
        this.mMatrix.postScale(wScale, hScale);
        this.mMatrix.postTranslate(viewLocation.getX(), viewLocation.getY());
        Rect rect = MatrixUtils.getMatrixRectForOriginalShowRange(this.getBitmap(), (float)this.getMeasuredWidth(), (float)this.getMeasuredHeight());
        float toX = (float)rect.left;
        float toY = (float)rect.top;
        float toWidth = (float)rect.width();
        float toHeight = (float)rect.height();
        float fromCenterX = viewLocation.getX() + viewLocation.getWidth() / 2.0F;
        float fromCenterY = viewLocation.getY() + viewLocation.getHeight() / 2.0F;
        float toCenterX = toX + toWidth / 2.0F;
        float toCenterY = toY + toHeight / 2.0F;
        float tranX = toCenterX - fromCenterX;
        float tranY = toCenterY - fromCenterY;
        float scaleX = toWidth / viewLocation.getWidth();
        float scaleY = toHeight / viewLocation.getHeight();
        TranslateScaleValue startValue = new TranslateScaleValue();
        startValue.setX(viewLocation.getX());
        startValue.setY(viewLocation.getY());
        startValue.setWidth(viewLocation.getWidth());
        startValue.setHeight(viewLocation.getHeight());
        startValue.setScaleX(1.0F);
        startValue.setScaleY(1.0F);
        startValue.setTranX(0.0F);
        startValue.setTranY(0.0F);
        final TranslateScaleValue endValue = new TranslateScaleValue();
        endValue.setX(toX);
        endValue.setY(toY);
        endValue.setWidth(toWidth);
        endValue.setHeight(toHeight);
        endValue.setScaleX(scaleX);
        endValue.setScaleY(scaleY);
        endValue.setTranX(tranX);
        endValue.setTranY(tranY);
        ValueAnimator animator = ValueAnimator.ofObject(new TranslateScaleEvaluator(), new Object[]{startValue, endValue});
        animator.setDuration(100L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                ScaleUpPhotoView.this.setScaling(true);
                TranslateScaleValue currentLocation = (TranslateScaleValue)animation.getAnimatedValue();
                float tranX = currentLocation.getTranX();
                float tranY = currentLocation.getTranY();
                float scaleX = currentLocation.getScaleX();
                float scaleY = currentLocation.getScaleY();
                Matrix matrix = new Matrix(ScaleUpPhotoView.this.mMatrix);
                float updateX = currentLocation.getX();
                float updateY = currentLocation.getY();
                float updateWidth = currentLocation.getWidth();
                float updateHeight = currentLocation.getHeight();
                float centerX = updateX + updateWidth / 2.0F;
                float centerY = updateY + updateHeight / 2.0F;
                matrix.postTranslate(tranX, tranY);
                matrix.postScale(scaleX, scaleY, centerX, centerY);
                ScaleUpPhotoView.this.setImageMatrix(matrix);
                float progress = scaleX / endValue.getScaleX();
                if (ScaleUpPhotoView.this.mAnimtorListener != null) {
                    ScaleUpPhotoView.this.mAnimtorListener.onEnterAnimProgress((int)(progress * 100.0F));
                }

            }
        });
        animator.addListener(new AnimatorEndListener() {
            public void onAnimationStart(Animator animation) {
                if (ScaleUpPhotoView.this.mAnimtorListener != null) {
                    ScaleUpPhotoView.this.mAnimtorListener.onEnterAnimStart();
                }

            }

            public void onAnimationEnd(Animator animation) {
                ScaleUpPhotoView.this.setScaling(false);
                ScaleUpPhotoView.this.mMatrix = new Matrix(ScaleUpPhotoView.this.getImageMatrix());
                ScaleUpPhotoView.this.mSavedMatrix.set(ScaleUpPhotoView.this.mMatrix);
                if (ScaleUpPhotoView.this.mAnimtorListener != null) {
                    ScaleUpPhotoView.this.mAnimtorListener.onEnterAnimEnd();
                }

            }
        });
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    public boolean startExitAnim(ViewLocation vLocation) {
        return this.startExitAnim(vLocation, (AnimatorEndListener)null);
    }

    public boolean startExitAnim(ViewLocation vLocation, final AnimatorEndListener listener) {
        if (!checkViewLocationVaid(vLocation)) {
            if (listener != null) {
                listener.onAnimationEnd((Animator)null);
            }

            return false;
        } else if (this.mMatrix == null) {
            Log.e(this.TAG, "startExitAnim:  mMatrix is null");
            if (listener != null) {
                listener.onAnimationEnd((Animator)null);
            }

            return false;
        } else {
            PointF[] pointFS = MatrixUtils.getLocation(this.mMatrix, this.getBitmap());
            if (pointFS == null) {
                if (listener != null) {
                    listener.onAnimationEnd((Animator)null);
                }

                return false;
            } else {
                float fromX = pointFS[0].x;
                float fromY = pointFS[0].y;
                final float fromWidth = pointFS[1].x - pointFS[0].x;
                float fromHeight = pointFS[2].y - pointFS[0].y;
                TranslateScaleValue startValue = new TranslateScaleValue();
                startValue.setX(fromX);
                startValue.setY(fromY);
                startValue.setWidth(fromWidth);
                startValue.setHeight(fromHeight);
                TranslateScaleValue endValue = new TranslateScaleValue();
                endValue.setX(vLocation.getX());
                endValue.setY(vLocation.getY());
                endValue.setWidth(vLocation.getWidth());
                endValue.setHeight(vLocation.getHeight());
                final float scaleRatio = fromWidth / vLocation.getWidth();
                ValueAnimator animator = ValueAnimator.ofObject(new TranslateScaleEvaluator(), new Object[]{startValue, endValue});
                animator.setDuration(200L);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        if (ScaleUpPhotoView.this.mMatrix != null && ScaleUpPhotoView.this.getBitmap() != null) {
                            ScaleUpPhotoView.this.setScaling(true);
                            TranslateScaleValue newLocation = (TranslateScaleValue)animation.getAnimatedValue();
                            float newX = newLocation.getX();
                            float newY = newLocation.getY();
                            float newWidth = newLocation.getWidth();
                            float newHeight = newLocation.getHeight();
                            PointF[] pointFS1 = MatrixUtils.getLocation(ScaleUpPhotoView.this.mMatrix, ScaleUpPhotoView.this.getBitmap());
                            float currentX = pointFS1[0].x;
                            float currentY = pointFS1[0].y;
                            float currentWidth = pointFS1[1].x - currentX;
                            float currentHeight = pointFS1[2].y - currentY;
                            float sw = newWidth / currentWidth;
                            float sh = newHeight / currentHeight;
                            ScaleUpPhotoView.this.mMatrix.postScale(sw, sh, currentX, currentY);
                            float dx = newX - currentX;
                            float dy = newY - currentY;
                            ScaleUpPhotoView.this.mMatrix.postTranslate(dx, dy);
                            ScaleUpPhotoView.this.mSavedMatrix.set(ScaleUpPhotoView.this.mMatrix);
                            ScaleUpPhotoView.this.setImageMatrix(ScaleUpPhotoView.this.mMatrix);
                            float progress = fromWidth / newWidth / scaleRatio;
                            if (ScaleUpPhotoView.this.mAnimtorListener != null) {
                                ScaleUpPhotoView.this.mAnimtorListener.onExitAnimProgress((int)(progress * 100.0F));
                            }

                        } else {
                            animation.cancel();
                        }
                    }
                });
                animator.addListener(new AnimatorEndListener() {
                    public void onAnimationStart(Animator animation) {
                        if (ScaleUpPhotoView.this.mAnimtorListener != null) {
                            ScaleUpPhotoView.this.mAnimtorListener.onExitAnimStart();
                        }

                    }

                    public void onAnimationEnd(Animator animation) {
                        ScaleUpPhotoView.this.setScaling(false);
                        ScaleUpPhotoView.this.mMatrix = new Matrix(ScaleUpPhotoView.this.getImageMatrix());
                        ScaleUpPhotoView.this.mSavedMatrix.set(ScaleUpPhotoView.this.mMatrix);
                        if (ScaleUpPhotoView.this.mAnimtorListener != null) {
                            ScaleUpPhotoView.this.mAnimtorListener.onExitAnimEnd();
                        }

                        if (listener != null) {
                            listener.onAnimationEnd(animation);
                        }

                    }
                });
                animator.start();
                return true;
            }
        }
    }

    public void setLocationInfo(ViewLocation vLocation) {
        this.vLocation = vLocation;
    }

    public void setOnScaleTransAnimListener(OnScaleTransAnimListener listener) {
        this.mAnimtorListener = listener;
    }

    public void setOnPrepareMatrixListener(ScaleUpPhotoView.OnPrepareMatrixListener listener) {
        this.onPrepareMatrixListener = listener;
    }

    public interface OnPrepareMatrixListener {
        void prepareMatrix(ScaleUpPhotoView var1, Bitmap var2);
    }
}
