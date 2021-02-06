package com.dylan.mylibrary.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dylan.library.utils.DensityUtils;
import com.dylan.library.widget.CircleRingProgressView;
import com.dylan.mylibrary.R;

/**
 * Author: Dylan
 * Date: 2021/02/06
 * Desc:
 */
public class CircleRingProgressViewActivity extends AppCompatActivity {
    private CircleRingProgressView crProgress1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circlering_progress);
        crProgress1=findViewById(R.id.crProgressView1);
        crProgress1.setCenterBackgroundColor(Color.WHITE);
        crProgress1.setProgress(30);
        crProgress1.setRingStrokeWidth(DensityUtils.dp2px(this,8));
        crProgress1.setRingProgressColor(Color.parseColor("#FFFADB22"));
        crProgress1.setRingBackgroundColor(Color.parseColor("#FF525252"));
        crProgress1.setCenterText("视频\n下载中");
        crProgress1.setCenterTextBold(0.5f);
        crProgress1.setCenterTextColor(Color.BLACK);
        crProgress1.setCenterTextSize(18);
    }
}
