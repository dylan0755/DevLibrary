package com.dankal.mylibrary.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.dankal.mylibrary.R;
import com.dankal.mylibrary.widget.PullToRefreshScrollView;

/**
 * Author: Dylan
 * Date: 2018/9/4
 * Desc:
 */

public class PullToRefreshScrollViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulltorefreshscrollview);
        final PullToRefreshScrollView pullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pullRefreshScrollView);
        View refreshView = pullRefreshScrollView.getRefreshView();
        final TextView tvRefresh = (TextView) refreshView.findViewById(R.id.tvRefresh);
        pullRefreshScrollView.setOnPullRefreshListener(new PullToRefreshScrollView.OnPullRefreshListener() {
            @Override
            public void onPullRefresh() {
                tvRefresh.setText("下拉刷新");
            }

            @Override
            public void onRefreshing() {
                tvRefresh.setText("正在刷新");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullRefreshScrollView.setRefresh(false);
                    }
                },3000);
            }

            @Override
            public void onToRefresh() {
                tvRefresh.setText("松开刷新");
            }
        });
    }
}
