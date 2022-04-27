package com.dylan.mylibrary.ui;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.dylan.library.utils.Logger;
import com.dylan.library.widget.TouchDispatchLayout;
import com.dylan.mylibrary.R;

/**
 * Author: Dylan
 * Date: 2022/04/27
 * Desc:
 */
public class TouchDispatchLayoutActivity extends AppCompatActivity {
    private TouchDispatchLayout dispatchLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatchlayout);
        dispatchLayout=findViewById(R.id.dispatchLayout);
        dispatchLayout.setDispatchCallBack(new TouchDispatchLayout.DispatchCallBack() {
            @Override
            public void dispatchTouchEvent(MotionEvent ev) {
                if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                    int height=dispatchLayout.getMeasuredHeight();
                    float downY=ev.getY();
                    if (downY>=height*0.9f){//按住悬浮窗底部进行拖动
                        Logger.e("downY "+downY+" height="+height);
                        dispatchLayout.setLock(true);
                    }
                }else if (ev.getAction()==MotionEvent.ACTION_UP){
                    dispatchLayout.setLock(false);
                }
            }

            @Override
            public void onDrag(int offsetX, int offsetY) {
                Logger.e("offsetX="+offsetX+" offsetY="+offsetY);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) dispatchLayout.getLayoutParams();
                layoutParams.leftMargin += offsetX;
                layoutParams.topMargin += offsetY;
                dispatchLayout.setLayoutParams(layoutParams);
            }

            @Override
            public void onScale(float xRatio, float yRatio, PointF midPoint) {

            }
        });
    }





}
