package com.dylan.mylibrary.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.dylan.library.utils.DensityUtils;
import com.dylan.library.widget.shape.ShadowDrawable;
import com.dylan.mylibrary.R;

/**
 * Author: Dylan
 * Date: 2020/4/19
 * Desc:
 */
public class ShadowLayoutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shadowlayout);
        LinearLayout layout3=findViewById(R.id.layout3);


        // 实例：设置背景为颜色为#3D5AFE，圆角为8dp, 阴影颜色为#66000000，宽度为10dp的背景
        ShadowDrawable.setShadowDrawable(layout3, Color.WHITE, DensityUtils.dp2px(this,8),
                Color.parseColor("#66000000"), DensityUtils.dp2px(this,10), 0, 0);
    }
}
