package com.dylan.mylibrary.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dylan.library.utils.Logger;
import com.dylan.library.widget.SwitchView;
import com.dylan.mylibrary.R;

/**
 * Author: Dylan
 * Date: 2021/1/25
 * Desc:
 */
public class SwitchViewActivity extends AppCompatActivity {
    private SwitchView switchView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switchview);
        switchView=findViewById(R.id.switchView);
        switchView.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {
                Logger.e("打开");

            }

            @Override
            public void toggleToOff(SwitchView view) {
                Logger.e("关闭");

            }
        });
        Logger.e(" is opened="+switchView.isOpened());
        switchView.setOpened(true);
        Logger.e(" is opened="+switchView.isOpened());
    }



}
