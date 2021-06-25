package com.dylan.mylibrary.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dylan.library.graphics.BitmapHelper;
import com.dylan.library.utils.DensityUtils;

/**
 * Author: Dylan
 * Date: 2021/1/30
 * Desc: 启动页适配
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashActivity.this,DemoListActivity.class);
                startActivity(intent);
                onBackPressed();
            }
        },2000);

    }
}
