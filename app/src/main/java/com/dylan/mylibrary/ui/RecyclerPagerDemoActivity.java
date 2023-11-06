package com.dylan.mylibrary.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dylan.library.test.TestAdapter;
import com.dylan.library.widget.IndicatorView;
import com.dylan.library.widget.gridpager.PagerGridLayoutManager;
import com.dylan.library.widget.gridpager.PagerGridSnapHelper;
import com.dylan.mylibrary.R;
import com.dylan.mylibrary.adapter.AutoLiveItemAdapter;
import com.dylan.mylibrary.adapter.GridTestAdapter;
import com.dylan.mylibrary.bean.AutoLiveItem;

import java.util.ArrayList;
import java.util.List;

public class RecyclerPagerDemoActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    LinearLayout indicatorWrapLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerpager_demo);
        recyclerView=findViewById(R.id.recyclerView);
        indicatorWrapLayout=findViewById(R.id.llIndicator);
        initRecyclerView();
    }

    private void initRecyclerView() {


        PagerGridLayoutManager layoutManager = new PagerGridLayoutManager(
                2, 4, PagerGridLayoutManager.HORIZONTAL);
        layoutManager.setPageListener(new PagerGridLayoutManager.PageListener() {
            IndicatorView indicatorView;
            @Override
            public void onPageSizeChanged(int pageSize) {
                if (pageSize<=1)return;
                indicatorWrapLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        indicatorWrapLayout.removeAllViews();
                        indicatorView= new IndicatorView.Builder().setContext(indicatorWrapLayout.getContext())
                                .setSelectColor(ContextCompat.getDrawable(indicatorWrapLayout.getContext(),R.drawable.shape_indicator_select))
                                .setNormalColor(ContextCompat.getDrawable(indicatorWrapLayout.getContext(),R.drawable.shape_indicator_unselect))
                                .setNormalAlpha(1)
                                .setIndicatorCount(pageSize)
                                .setSelectPosition(0)
                                .builder();
                        indicatorWrapLayout.addView(indicatorView);
                    }
                });

            }

            @Override
            public void onPageSelect(int pageIndex) {
                indicatorWrapLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        if (indicatorView==null)return;
                        indicatorView.changeIndicator(pageIndex);
                    }
                });
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        // 2.设置滚动辅助工具
        PagerGridSnapHelper pageSnapHelper = new PagerGridSnapHelper();
        pageSnapHelper.attachToRecyclerView(recyclerView);

        //直播托管
        GridTestAdapter adapter=new GridTestAdapter();
        List<String> list=new ArrayList<>();
        for (int i=0;i<60;i++){
            list.add(""+(i+1));
        }
        adapter.bind(list);
        recyclerView.setAdapter(adapter);

    }

}
