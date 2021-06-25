package com.dylan.mylibrary.ui;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.dylan.library.callback.SingleClickListener;
import com.dylan.library.utils.CompatUtils;
import com.dylan.library.utils.SmsCodeCounter;
import com.dylan.library.widget.shape.GradientDrawableBuilder;
import com.dylan.mylibrary.R;

/**
 * Author: Dylan
 * Date: 2020/10/29
 * Desc:
 */
public class SmsCodeCounterActivity extends AppCompatActivity {
    private SmsCodeCounter smsCodeCounter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smscode_counter);
        TextView tvGetCode=findViewById(R.id.tvGetCode);
        tvGetCode.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                smsCodeCounter.start();
            }
        });


        smsCodeCounter=new SmsCodeCounter(this,tvGetCode);
        GradientDrawable normalDrawable= new GradientDrawableBuilder(this)
                .setSolidColor(Color.RED).setCornerRadius(24).build();
        smsCodeCounter.setNormalDrawable(normalDrawable);

        GradientDrawable countDownDrawable=new GradientDrawableBuilder(this)
                .setSolidColor(Color.parseColor("#999999"))
//                .setStroke(1,Color.RED)
//                .setStroke(1,Color.RED,10,8)
//                .setCornerRadius(24)
                .build();
        smsCodeCounter.setCountDownDrawable(countDownDrawable);

        smsCodeCounter.checkRecord();

        smsCodeCounter.setOnCountDownListener(new SmsCodeCounter.OnCountDownListener() {
            @Override
            public void onStart(int duration) {
               // Logger.e("开始");
            }

            @Override
            public void onTick(int duration) {
               // Logger.e("duration="+duration);
            }

            @Override
            public void onFinish() {
               // Logger.e("结束");
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        smsCodeCounter.destroyAndRecordExitTime(true);
    }
}
