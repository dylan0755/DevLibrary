package com.dylan.mylibrary.ui.lazyload;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dylan.library.fragment.LazyFragment;
import com.dylan.mylibrary.R;
import com.dylan.library.utils.HandlerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 2018/2/8.
 */

public class ClassFragment extends LazyFragment implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private NewDataAdapter mDataAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_lazyload;
    }

    @Override
    public void onFragmentCreate() {
        initView();
    }

    private void initView() {
        mRefreshLayout = findViewById(R.id.swiperefresh);
        mRefreshLayout.setOnRefreshListener(this);
        mRecyclerView =  findViewById(R.id.recycleView_class);
        mRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        mDataAdapter = new NewDataAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mDataAdapter);
    }

   

    @Override
    public void firstVisibleLoad() {
        final Bundle bundle = getArguments();
        if (bundle != null) {
            final int pageIndex = bundle.getInt("pageIndex");

            mRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mRefreshLayout.setRefreshing(true);
                }
            });
            HandlerUtils.getMainLooperHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRefreshLayout.setRefreshing(false);
                    List<NewItem> list = new ArrayList<>();
                    for (int i = 0; i < 20; i++) {
                        list.add(new NewItem());
                    }
                    mDataAdapter.bind(list);
                }
            },1500);

        }

    }


    @Override
    public void onRefresh() {
        HandlerUtils.getMainLooperHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }
}
