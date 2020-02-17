package com.dylan.mylibrary.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dylan.library.test.TestAdapter;
import com.dylan.mylibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2020/2/17
 * Desc:
 */
public class RebondLayoutActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rebondlayout);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TestAdapter testAdapter=new TestAdapter();
        recyclerView.setAdapter(testAdapter);

        List<String> list=new ArrayList<>();
        for (int i=0;i<16;i++){
            list.add("item "+(i+1));
        }
        testAdapter.bind(list);
    }
}
