package com.dylan.mylibrary.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.dylan.library.callback.SingleClickListener;
import com.dylan.mylibrary.R;
import com.dylan.mylibrary.dialog.PostBannerDialog;

/**
 * Author: Dylan
 * Date: 2022/03/25
 * Desc:
 */
public class PosterBannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster_banner);


        findViewById(R.id.btnShow).setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                PostBannerDialog dialog=new PostBannerDialog(v.getContext());
                dialog.show();
            }
        });
    }





}
