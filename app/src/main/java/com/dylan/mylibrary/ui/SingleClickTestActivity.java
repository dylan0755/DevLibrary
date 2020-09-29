package com.dylan.mylibrary.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.dylan.library.activity.SingleClickAppCompatActivity;
import com.dylan.mylibrary.R;

/**
 * Author: Administrator
 * Date: 2020/9/29
 * Desc:
 */
public class SingleClickTestActivity extends SingleClickAppCompatActivity { //实现单击控制


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singletestclick);


        findViewById(R.id.tvClickTwo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(), SingleClickResult2Activity.class);
                startActivity(intent);
            }
        });
    }
}
