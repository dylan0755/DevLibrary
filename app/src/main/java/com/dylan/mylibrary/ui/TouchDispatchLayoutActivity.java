package com.dylan.mylibrary.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.dylan.library.callback.SingleClickListener;
import com.dylan.library.utils.Logger;
import com.dylan.library.widget.DragScaleLayout;
import com.dylan.mylibrary.R;
import com.hjq.toast.Toaster;

/**
 * Author: Dylan
 * Date: 2022/04/27
 * Desc:
 */
public class TouchDispatchLayoutActivity extends AppCompatActivity {
    private DragScaleLayout dispatchLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatchlayout);
        dispatchLayout = findViewById(R.id.dispatchLayout);
        dispatchLayout.setOnDispatchTouchEventListener(new DragScaleLayout.OnDispatchTouchEventListener() {
            @Override
            public void dispatchTouchEvent(MotionEvent ev, int action) {
                if (action== MotionEvent.ACTION_DOWN) {
                    dispatchLayout.setClickable(true);
                    int height = dispatchLayout.getMeasuredHeight();
                    float downY = ev.getY();
                    if (downY >= height * 0.9f) {//按住悬浮窗底部进行拖动
                        Logger.e("ACTION_DOWN  lock");
                        dispatchLayout.setAllowDragAndScale(true);
                    }
                } else if (action== MotionEvent.ACTION_POINTER_DOWN) {
                    dispatchLayout.setAllowDragAndScale(true);
                } else if (action == MotionEvent.ACTION_UP) {
                    dispatchLayout.setAllowDragAndScale(false);
                }else if (action==MotionEvent.ACTION_POINTER_UP){
                    dispatchLayout.setAllowDragAndScale(false);
                }
            }



        });

        dispatchLayout.findViewById(R.id.ivClose).setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Toaster.show("click close");
            }
        });



    }


}
