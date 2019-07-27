package com.dylan.mylibrary.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;

import com.dylan.library.screen.ScreenUtils;
import com.dylan.mylibrary.R;
import com.dylan.mylibrary.adapter.PictureDataRecyclerAdapter;
import com.dylan.mylibrary.ui.onlinepic.ClickViewPoint;
import com.dylan.mylibrary.ui.onlinepic.OnLinePreviewActivity;
import com.gyf.barlibrary.ImmersionBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2019/7/27
 * Desc:
 */

public class PicturePreViewActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prictire_preview);
        ImmersionBar.with(this)
                .fitsSystemWindows(true)//保留状态栏View
                .statusBarDarkFont(true)
                .transparentStatusBar()
                .init();


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        PictureDataRecyclerAdapter recyclerAdapter = new PictureDataRecyclerAdapter();
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setOnChildItemClickListener(new PictureDataRecyclerAdapter.OnChildItemClickListener() {
            @Override
            public void click(AdapterView<?> parent, ArrayList<String> picUrlList, int position) {
                ArrayList<ClickViewPoint.Point> pointList = new ArrayList<>();
                int count = parent.getChildCount();
                for (int i = 0; i < count; i++) {
                    int[] p = new int[2];
                    parent.getChildAt(i).getLocationInWindow(p);
                    ClickViewPoint.Point point = new ClickViewPoint.Point();
                    point.x = p[0] * 1.0f;
                    point.y = p[1] * 1.0f;
                    pointList.add(point);
                }
                Intent intent = new Intent(PicturePreViewActivity.this, OnLinePreviewActivity.class);
                intent.putExtra(OnLinePreviewActivity.EXTRA_LOCATION_WITHOUT_STATUS, pointList);
                intent.putExtra(OnLinePreviewActivity.EXTRA_PICTURE_URLS, picUrlList);
                intent.putExtra(OnLinePreviewActivity.EXTRA_PICTURE_POSTION, position);
                startActivity(intent);
                OnLinePreviewActivity.overridePendingTransition(PicturePreViewActivity.this);
            }

        });
    }

    public void onBack(View v) {
        onBackPressed();
    }
}
