package com.dylan.mylibrary.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dylan.mylibrary.R;
import com.dylan.mylibrary.ui.verticalviewpager.VerticalPageAdapter;
import com.dylan.mylibrary.ui.verticalviewpager.VerticalViewPager;

/**
 * Author: Dylan
 * Date: 2019/8/1
 * Desc:
 */

public class VerticalViewPagerActivity extends AppCompatActivity{
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verticalviewpager);
        recyclerView=findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
