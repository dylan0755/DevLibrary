package com.dylan.mylibrary.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dylan.mylibrary.R;
import com.dylan.mylibrary.widget.BezierView;

/**
 * Author: Dylan
 * Date: 2019/8/15
 * Desc:
 */
public class BezierCurveActivity extends AppCompatActivity {
    private BezierView bezierView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beizier_curve);
        bezierView =findViewById(R.id.drawQuadToView);

    }


}
