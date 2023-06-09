package com.dylan.mylibrary.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dylan.mylibrary.R;

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
