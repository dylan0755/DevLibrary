package com.dylan.mylibrary.ui.snaphelper;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;

import com.dylan.mylibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2019/8/1
 * Desc:
 */

public class RecyclerSnapHelperActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclersnaphelper);

        RecyclerView recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        PagerSnapHelper snapHelper=new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

       RecyclerSnapDataAdapter adapter=new RecyclerSnapDataAdapter();
        List<String> list=new ArrayList<>();
        list.add("1");
        list.add("1");
        list.add("1");
        adapter.bind(list);
       recyclerView.setAdapter(adapter);

    }
}
