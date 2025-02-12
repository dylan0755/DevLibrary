package com.dylan.mylibrary.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.dylan.library.callback.SingleClickListener;
import com.dylan.library.widget.CountDownCircleView;
import com.dylan.mylibrary.R;
import com.hjq.toast.Toaster;

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
                    Toaster.show("跳转XXXActivity");
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
                Toaster.show("跳转XXXActivity");
            }
        });

        countDownCircleView.setCountDownDuration(5);
        countDownCircleView.startCountDown();
    }
}
