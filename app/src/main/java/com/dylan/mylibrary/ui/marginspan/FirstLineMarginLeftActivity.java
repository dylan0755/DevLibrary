package com.dylan.mylibrary.ui.marginspan;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dylan.mylibrary.R;

/**
 * Author: Dylan
 * Date: 2019/8/10
 * Desc:
 */
public class FirstLineMarginLeftActivity extends AppCompatActivity {
     private RecyclerView recyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstline_margin);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DataAdapter madapter=new DataAdapter();
        recyclerView.setAdapter(madapter);
    }
}
