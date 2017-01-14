package com.dankal.mylibrary.ui.wraplayoutmanager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.dankal.mylibrary.R;
import com.dankal.mylibrary.ui.testdata.TestDatas;
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
