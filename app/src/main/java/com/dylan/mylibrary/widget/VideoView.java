package com.dylan.mylibrary.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.dylan.mylibrary.util.PlayerAdjustTool;

/**
 * Created by Dylan on 2016/11/12.
 */

public class VideoView extends LinearLayout {
    private Activity mActivity;
    private PlayerAdjustTool playerAdjustTool;
    public VideoView(Context context) {
        this(context,null);
    }

    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        playerAdjustTool=new PlayerAdjustTool(context,this);

    }

    public void attachActivity(Activity activity){
        mActivity=activity;
        playerAdjustTool.attachActivity(mActivity);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        playerAdjustTool.setMeasureWidth(getMeasuredWidth());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
       playerAdjustTool.onTouchEvent(event);
        return true;
    }
}
