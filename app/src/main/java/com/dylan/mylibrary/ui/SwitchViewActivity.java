package com.dylan.mylibrary.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dylan.library.widget.SwitchView;
import com.dylan.mylibrary.R;
import com.hjq.toast.Toaster;

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
                Toaster.show("打开");

            }

            @Override
            public void toggleToOff(SwitchView view) {
                Toaster.show("关闭");

            }
        });
        switchView.setOpened(false);
    }



}
