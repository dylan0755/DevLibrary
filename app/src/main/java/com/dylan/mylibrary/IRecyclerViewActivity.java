package com.dylan.mylibrary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.dl.recyclerview.IRecyclerView;
import com.dl.recyclerview.IRecyclerViewHelper;
import com.dl.recyclerview.OnLoadMoreListener;
import com.dl.recyclerview.OnRefreshListener;
import com.dl.recyclerview.footer.LoadMoreFooterView;
import com.dylan.library.test.TestAdapter;
import com.dylan.library.utils.EmptyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2019/9/20
 * Desc:
 */
public class IRecyclerViewActivity extends AppCompatActivity implements OnRefreshListener, OnLoadMoreListener {
    IRecyclerView recyclerView;
    private int pageNo=1;
    private TestAdapter mAdapter;
    private LoadMoreFooterView footerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irecyclerview);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setOnRefreshListener(this);
        recyclerView.setOnLoadMoreListener(this);
        footerView= (LoadMoreFooterView) recyclerView.getLoadMoreFooterView();
        mAdapter=new TestAdapter();
        recyclerView.setIAdapter(mAdapter);

        List<String> list=new ArrayList<>();
        for (int i=1;i<=15;i++){
            list.add(String.valueOf(i));
        }
        mAdapter.bind(list);

    }

    @Override
    public void onLoadMore() {
      if (IRecyclerViewHelper.isCanLoadMore(recyclerView,footerView,mAdapter)){
          pageNo++;
          recyclerView.postDelayed(new Runnable() {
              @Override
              public void run() {
                  List<String> list=new ArrayList<>();
                  if (pageNo<=5){
                      for (int i=1;i<=15;i++){
                          list.add(i+"  fromLoadMore");
                      }

                  }
                  if (EmptyUtils.isNotEmpty(list)){
                      mAdapter.getDataList().addAll(list);
                      mAdapter.notifyDataSetChanged();
                      IRecyclerViewHelper.loadMoreComplete(footerView);
                  }else{
                      IRecyclerViewHelper.setNoMore(footerView);
                  }


              }
          },2000);

      }
    }

    @Override
    public void onRefresh() {
        IRecyclerViewHelper.setRefreshStatus(footerView);
        pageNo=1;
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<String> list=new ArrayList<>();
                for (int i=1;i<=15;i++){
                    list.add(String.valueOf(i));
                }
                mAdapter.bind(list);
                IRecyclerViewHelper.refreshComplete(recyclerView);
            }
        },2000);

    }
}
