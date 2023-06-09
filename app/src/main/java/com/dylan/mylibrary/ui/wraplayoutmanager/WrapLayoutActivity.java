package com.dylan.mylibrary.ui.wraplayoutmanager;

import android.app.Activity;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import com.dylan.mylibrary.R;
import com.dylan.mylibrary.ui.testdata.TestDatas;
import com.dylan.library.widget.LinearWrapLayoutManager;

/**
 * Created by Dylan on 2017/1/15.
 */

public class WrapLayoutActivity extends Activity {
    RecyclerView mRecyclerView;
    private WrapRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wraplayout);
        mRecyclerView= (RecyclerView) findViewById(R.id.rv_wraplayout);
        mRecyclerView.setLayoutManager(new LinearWrapLayoutManager(this));
        mAdapter=new WrapRecyclerAdapter();
        mAdapter.bind(TestDatas.getGridItemData());
        mRecyclerView.setAdapter(mAdapter);
    }








}
