package com.dylan.mylibrary.ui;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.dylan.library.widget.progressbar.ProgressBarDrawableDecorator;
import com.dylan.mylibrary.R;

/**
 * Author: Administrator
 * Date: 2020/8/22
 * Desc:
 */
public class ProgressBarActivity extends AppCompatActivity {
    private ProgressBar progressBar1;
    private ProgressBar progressBar2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progressbar);
        progressBar1 =findViewById(R.id.progressBar1);
        progressBar2 =findViewById(R.id.progressBar2);
        progressBar1.setMax(100);
        progressBar2.setMax(100);
        progressBar1.setProgress(25);
        progressBar2.setProgress(45);
        progressBar2.setSecondaryProgress(80);

        ProgressBarDrawableDecorator decorator1=new ProgressBarDrawableDecorator(progressBar1);
        decorator1
                .setBGStroke(1,"#ffb5da4f")
                .setBGSolidColor(Color.TRANSPARENT)
                .setProgressColor("#ffb5da4f")
                .setCornerRadius(6)
                .decorate();



        ProgressBarDrawableDecorator decorator2=new ProgressBarDrawableDecorator(progressBar2);
        decorator2.setBGSolidColor(Color.TRANSPARENT)
                .setBGStroke(1,"#b0000000")
                .setCornerRadius(6)
                .setSecondaryProgressColor("#c4c4c4")
                .setProgressColor(Color.RED)
                .decorate();


    }
}
