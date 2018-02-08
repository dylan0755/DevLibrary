package com.dankal.mylibrary.ui.lazyload;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dankal.mylibrary.R;
import com.dylan.library.fragment.LazyFragment;
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_lazyload, container, false);
        initView(contentView);
        return contentView;
    }

    private void initView(View contentView) {
        mRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.swiperefresh);
        mRefreshLayout.setOnRefreshListener(this);
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.recycleView_class);
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
