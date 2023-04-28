package com.dylan.mylibrary.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.dylan.library.callback.SingleClickListener;
import com.dylan.library.utils.ToastUtils;
import com.dylan.mylibrary.R;


/**
 * Author: Dylan
 * Date: 2021/03/10
 * Desc:
 */
public class ToastUtilsActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast);
        findViewById(R.id.btnShort).setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                ToastUtils.show("短Toast");
            }
        });
        findViewById(R.id.btnShortCenter).setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                ToastUtils.showCenterShort("centerToast");
            }
        });

        findViewById(R.id.btnLongToast).setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                ToastUtils.showLong("longToast");
            }
        });

        findViewById(R.id.btnSubThread).setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showLong("子线程中弹出");
                    }
                }).start();

            }
        });
    }



}