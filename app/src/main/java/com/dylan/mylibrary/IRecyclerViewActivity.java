package com.dylan.mylibrary;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

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
    private TestAdapter mAdapter;
    private IRecyclerHelper iRecyclerHelper;
    private Handler handler = new Handler();
    private boolean isFirstRequest = true;
    private boolean hasError;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irecyclerview);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setOnRefreshListener(this);
        recyclerView.setOnLoadMoreListener(this);
        mAdapter = new TestAdapter();
        recyclerView.setIAdapter(mAdapter);


        iRecyclerHelper = new IRecyclerHelper();
        iRecyclerHelper.bind(recyclerView, mAdapter, null);
        getData(iRecyclerHelper.getPageNo());

//        iRecyclerHelper.setLoadingTextColor(Color.BLUE);
//        iRecyclerHelper.setNoMoreTextColor(Color.BLUE);
//        iRecyclerHelper.setErrorTextColor(Color.BLUE);
//        iRecyclerHelper.tintLoadingProgressBar(Color.BLUE);
//        iRecyclerHelper.setRefreshTextViewColor(Color.BLUE);
//        iRecyclerHelper.setRefreshCircleIndicatorViewColor(Color.TRANSPARENT,Color.BLUE);

//        iRecyclerHelper.setLoadingTextSize(18);
//        iRecyclerHelper.setRefreshTextSize(18);
//        iRecyclerHelper.setErrorTextSize(18);
//        iRecyclerHelper.setNoMoreTextSize(18);
    }

    @Override
    public void onLoadMore() {
        if (iRecyclerHelper.isCanLoadMore()) {
            getData(iRecyclerHelper.getPageNo());
        }
    }

    @Override
    public void onRefresh() {
        hasError = false;
        iRecyclerHelper.setRefreshStatus();
        getData(iRecyclerHelper.getPageNo());
    }


    //模拟请求结果
    public void afterGetData(boolean isSucceed, Object o, List<String> list) {
        iRecyclerHelper.afterGetData(isSucceed, o, list);
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
                    afterGetData(false, new SocketTimeoutException("Test TimeOut"), list);
                    hasError = true;
                    return;
                }
                for (int i = 1; i <= 20; i++) {
                    list.add("page-" + pageNo + "     " + i);
                }
            }
        }

        if (isFirstRequest) {
            afterGetData(true, "", list);
            isFirstRequest = false;
            return;
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                afterGetData(true, "", list);
            }
        }, 2000);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) handler.removeCallbacksAndMessages(null);
    }
}
