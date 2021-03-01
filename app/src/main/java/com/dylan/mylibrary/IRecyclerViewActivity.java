package com.dylan.mylibrary;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.dylan.library.test.TestAdapter;
import com.dylan.library.widget.irecycler.IRecyclerHelper;
import com.dylan.library.widget.irecycler.IRecyclerView;
import com.dylan.library.widget.irecycler.OnLoadMoreListener;
import com.dylan.library.widget.irecycler.OnRefreshListener;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2019/9/20
 * Desc:
 */
public class IRecyclerViewActivity extends AppCompatActivity implements OnRefreshListener, OnLoadMoreListener {
    IRecyclerView recyclerView;
    TextView tvEmptyView;
    private TestAdapter mAdapter;
    private IRecyclerHelper recyclerHelper;
    private Handler handler = new Handler();
    private boolean isFirstRequest = true;
    private boolean hasError;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irecyclerview);
        tvEmptyView=findViewById(R.id.tvEmptyView);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLoadMoreEnabled(true);
        recyclerView.setRefreshEnabled(true);
        recyclerView.setOnRefreshListener(this);
        recyclerView.setOnLoadMoreListener(this);
        mAdapter = new TestAdapter();
        recyclerView.setIAdapter(mAdapter);


        recyclerHelper = new IRecyclerHelper();
        recyclerHelper.bind(recyclerView, mAdapter, tvEmptyView);
        getData(recyclerHelper.getPageNo());

        findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.clear();
            }
        });

    }

    @Override
    public void onLoadMore() {
        if (recyclerHelper.isCanLoadMore()) {
            getData(recyclerHelper.getPageNo());
        }
    }

    @Override
    public void onRefresh() {
        hasError = false;
        recyclerHelper.setRefreshStatus();
        getData(recyclerHelper.getPageNo());
    }





    //模拟请求
    private void getData(int pageNo) {
        final List<String> list = new ArrayList<>();
        if (pageNo == 1) {
            for (int i = 1; i <= 20; i++) {
                list.add(String.valueOf(i));
            }
        } else {
            if (pageNo <= 5) {
                if (pageNo == 4 && !hasError) {//模拟请求网络错误
                    recyclerHelper.afterGetData(new SocketTimeoutException("Test TimeOut"),false,null, list);
                    hasError = true;
                    return;
                }
                for (int i = 1; i <= 20; i++) {
                    list.add("page-" + pageNo + "     " + i);
                }
            }
        }

        if (isFirstRequest) {
            recyclerHelper.afterGetData(null,true,null, list);
            isFirstRequest = false;
            return;
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerHelper.afterGetData(null,true,null, list);

            }
        }, 2000);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) handler.removeCallbacksAndMessages(null);
    }
}
