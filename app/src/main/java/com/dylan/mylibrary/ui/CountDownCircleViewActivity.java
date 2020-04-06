package com.dylan.mylibrary.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dylan.library.callback.SingleClickListener;
import com.dylan.library.utils.ToastUtils;
import com.dylan.library.widget.CountDownCircleView;
import com.dylan.mylibrary.R;

/**
 * Author: Dylan
 * Date: 2019/9/23
 * Desc:
 */
public class CountDownCircleViewActivity extends AppCompatActivity {
    private CountDownCircleView countDownCircleView;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown_circleview);
        countDownCircleView=findViewById(R.id.cdCircleView);
        //countDownCircleView.setTextType(CountDownCircleView.TYPE_SECOND_TEXT);
        //countDownCircleView.setTextColor(Color.BLUE);
        countDownCircleView.setAddCountDownListener(new CountDownCircleView.OnCountDownFinishListener() {
            @Override
            public void countDownFinished() {
                countDownCircleView.setVisibility(View.GONE);
                if (!isFinishing()) {
                    ToastUtils.show("跳转XXXActivity");
                }

            }
        });
        countDownCircleView.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                countDownCircleView.stopCountDown();
                countDownCircleView.setVisibility(View.GONE);
//                Intent intent=new Intent(CountDownCircleViewActivity.this,DemoListActivity.class);
//                startActivity(intent);
                ToastUtils.show("跳转XXXActivity");
            }
        });

        countDownCircleView.setCountDownDuration(5);
        countDownCircleView.startCountDown();
    }
}
