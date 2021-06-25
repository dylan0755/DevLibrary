package com.dylan.library.widget;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.dylan.library.media.PlayerGesture;

/**
 * Author: Dylan
 * Date: 2020/2/12
 * Desc:
 */
public class PlayerGestureView extends View {
    private PlayerGesture playerGesture;
    private GestureDetector gestureDetector;
    private boolean isLock;


    public PlayerGestureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        playerGesture = new PlayerGesture(context, this);
        playerGesture.attachActivity(getActivity(context));
        gestureDetector = new GestureDetector(context, new PlayerGestureListener());
        setClickable(true);
    }

    public void setAnchorView(View anchorView) {
        playerGesture.setAnchorView(anchorView);
    }

    //上锁
    public void lock(){
        isLock=true;
    }

    //解锁
    public void unLock(){
        isLock=false;
    }

    public boolean isLock(){
        return isLock;
    }


    public void setSoundOnLeft(boolean bl){
         playerGesture.setSoundOnLeft(bl);
    }

    public void setSoundMuteIconEnable(boolean bl){
        playerGesture.setSoundMuteIconEnable(bl);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        playerGesture.setMeasureWidth(getMeasuredWidth());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!isLock()) playerGesture.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    public static Activity getActivity(Context context) {
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }

        if (context instanceof Activity) {
            return (Activity) context;
        }
        return null;
    }

    class PlayerGestureListener extends GestureDetector.SimpleOnGestureListener implements GestureDetector.OnDoubleTapListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mCallBack != null) mCallBack.onSingleTapUp(e);
            return super.onSingleTapUp(e);
        }


        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            if (e.getAction()==MotionEvent.ACTION_UP){
                if (mCallBack!=null)mCallBack.onDoubleTapUp(e);
            }
            return super.onDoubleTapEvent(e);
        }


    }

    private TouchTapUpCallBack mCallBack;

    public interface TouchTapUpCallBack {
        void onSingleTapUp(MotionEvent e);
        void onDoubleTapUp(MotionEvent e);
    }

    public void addTouchTapUpCallBack(TouchTapUpCallBack callback) {
        mCallBack = callback;
    }



    public void setOnPlayerSpeedGestureListener(PlayerGesture.OnPlayerGestureListener listener) {
        playerGesture.setOnPlayerSpeedGestureListener(listener);
    }

}
