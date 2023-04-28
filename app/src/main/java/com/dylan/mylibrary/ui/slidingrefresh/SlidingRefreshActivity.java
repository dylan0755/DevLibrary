package com.dylan.mylibrary.ui.slidingrefresh;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.support.design.widget.TabLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.dylan.library.widget.pullrefresh.PullRefreshLayout;
import com.dylan.library.widget.sliding.OffsetChangedListener;
import com.dylan.library.widget.sliding.SlidingLayout;
import com.dylan.mylibrary.R;


/**
 * Author: Dylan
 * Date: 2019/8/9
 * Desc:
 */
public class SlidingRefreshActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TabLayout mTabLayout;
    private SlidingLayout slidingLayout;
    private PullRefreshLayout pullRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slingrefresh);

        //RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DataAdapter dataAdapter = new DataAdapter();
        recyclerView.setAdapter(dataAdapter);

        mTabLayout = findViewById(R.id.tabLayout);
        mTabLayout.addTab(mTabLayout.newTab().setText("Tab1"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Tab2"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Tab3"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Tab4"));
        initEvent();
        slidingLayout = findViewById(R.id.slidingLayout);
        slidingLayout.addOnOffsetChangedListener(new OffsetChangedListener() {
            @Override
            public void offsetChange(float offset, float fraction) {
                // Log.e("offsetChange: ", "fraction "+fraction);
            }
        });
        pullRefreshLayout=findViewById(R.id.pullRefreshLayout);

        pullRefreshLayout.setPullRefreshListener(new PullRefreshLayout.PullRefreshListener() {
            @Override
            public void onRefresh(final PullRefreshLayout refreshLayout) {
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishRefreshing();
                    }
                },3000);
            }
        });


        pullRefreshLayout.setOnMaxBackListener(new PullRefreshLayout.OnMaxBackListener() {
            @Override
            public void onBack(PullRefreshLayout refreshLayout) {
                slidingLayout.slideDown();
            }
        });
//        pullRefreshLayout.setTipTextColor(Color.RED);
//        pullRefreshLayout.setCircleOutRingColor(Color.RED);
        pullRefreshLayout.setMaxBackEnable(true);


    }

    private void initEvent() {
        findViewById(R.id.btnClick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SlidingRefreshActivity.this, "事件可以穿透", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (slidingLayout.isOnTop()) {
                slidingLayout.slideDown();
                return true;
            }


        }
        return super.onKeyDown(keyCode, event);
    }
}
