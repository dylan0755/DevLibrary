package com.dylan.mylibrary.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.TextView;

import com.dylan.library.exception.ELog;
import com.dylan.library.media.MediaTools;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.utils.TimerClock;
import com.dylan.library.widget.SquareProgressView;
import com.dylan.mylibrary.R;
import com.gyf.barlibrary.ImmersionBar;

import butterknife.BindView;

/**
 * Author: Dylan
 * Date: 2021/07/19
 * Desc:
 */
public class SquareProgressViewActivity extends AppCompatActivity {

    TextView tvTitle;
    SquareProgressView squareProgressView;
    TextView tvProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distinct_doing);
        squareProgressView=findViewById(R.id.squareView);
        tvTitle=findViewById(R.id.tv_title);
        tvProgress=findViewById(R.id.tvProgress);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initImmersionBar();
        tvTitle.setText("视频消重");
        TimerClock timerClock=new TimerClock();
        timerClock.start(new TimerClock.CountDownTimerCallBack() {
            @Override
            public void onTick(long l, String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int progress=squareProgressView.getCurProgress()+2;
                        squareProgressView.setCurProgress(progress);
                        if (progress>100)progress=100;
                        tvProgress.setText(progress+"%");
                    }
                });

            }
        });




    }


    private void initImmersionBar() {
        try {
            ImmersionBar.with(this)
                    .statusBarDarkFont(false)   //状态栏字体是深色，不写默认为亮色
                    .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                    .statusBarColorInt(Color.parseColor("#FF181818"))
                    .keyboardEnable(true)
                    .init();
        } catch (Exception e) {
            ELog.e(e);
        }

    }




}
