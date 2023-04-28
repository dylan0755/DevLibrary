package com.dylan.mylibrary.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.dylan.library.widget.DragSelectRangeView;
import com.dylan.mylibrary.R;

/**
 * Author: Dylan
 * Date: 2022/04/27
 * Desc:
 */
public class DragSelectRangeViewActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dragselectrange);

        final DragSelectRangeView dragSelectRangeView = findViewById(R.id.dragView);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(200, 100);
        lp.setMargins(50, 50, 0, 0);
        dragSelectRangeView.setLayoutParams(lp);
        dragSelectRangeView.setClickable(true);
        dragSelectRangeView.setSelected(true);
        dragSelectRangeView.setMyTouchListener(new DragSelectRangeView.OnMyTouchListener() {
            @Override
            public void onClick() {
                dragSelectRangeView.setSelected(true);
            }
        });

        findViewById(R.id.rootView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dragSelectRangeView.setSelected(false);
            }
        });
    }


}
