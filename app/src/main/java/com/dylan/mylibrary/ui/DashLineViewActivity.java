package com.dylan.mylibrary.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dylan.library.widget.DashLineView;
import com.dylan.mylibrary.R;

/**
 * Author: Dylan
 * Date: 2019/9/10
 * Desc: 虚线
 */
public class DashLineViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashline);
        DashLineView dashLine=findViewById(R.id.dashLine);
        dashLine.setDashLineColor(Color.BLUE);
        dashLine.setDashGap(40);
        dashLine.setDashWith(80);
        dashLine.setDashPaddingLeft(60);
        dashLine.setDashPaddingRight(60);
    }
}
