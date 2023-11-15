package com.dylan.mylibrary.ui;

import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dylan.mylibrary.R;
import com.dylan.mylibrary.adapter.AutoLiveItemAdapter;
import com.dylan.mylibrary.bean.AutoLiveItem;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewScrollIndicatorActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview_scroll_indicator);
        recyclerView=findViewById(R.id.rvAutoLive);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        //直播托管
        List<AutoLiveItem> autoLiveItems = new ArrayList<>();
        autoLiveItems.add(new AutoLiveItem(0, "弹幕回复", "真人语音+文字实时回复"));
        autoLiveItems.add(new AutoLiveItem(0, "视频自动播", "支持前景贴片+背景全屏"));
        autoLiveItems.add(new AutoLiveItem(0, "自动商品弹窗", "商品弹窗+语音直播+弹幕一致"));
        autoLiveItems.add(new AutoLiveItem(0, "真实场景", "随机过度声音和环境音"));
        autoLiveItems.add(new AutoLiveItem(0, "云端操作", "多账号云端操作"));
        AutoLiveItemAdapter autoLiveItemAdapter = new AutoLiveItemAdapter(autoLiveItems);
        recyclerView.setAdapter(autoLiveItemAdapter);
        recyclerView.addOnScrollListener(new RcvScrollListener(recyclerView,findViewById(R.id.indicatorAutoLive)));

    }


    class RcvScrollListener extends RecyclerView.OnScrollListener {
        RecyclerView recyclerView;
        ProgressBar indicatorView;
        public RcvScrollListener(RecyclerView recyclerView,ProgressBar progressBar){
            this.recyclerView=recyclerView;
            this.indicatorView=progressBar;
        }
        float percentage;
        float scrollX;

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            int xOffset = recyclerView.computeHorizontalScrollOffset();
            int range = recyclerView.computeHorizontalScrollRange();
            int extent = recyclerView.computeHorizontalScrollExtent();
            int totalDistanceX = range - extent;
            percentage = xOffset * 1f / totalDistanceX;
            scrollX = percentage * totalDistanceX + dx;
            if (xOffset == 0)
                scrollX = 0;
            if (xOffset == totalDistanceX)
                scrollX = xOffset;

            percentage = scrollX / totalDistanceX;
            indicatorView.setMax(totalDistanceX);
            indicatorView.setProgress((int) ((int) scrollX + totalDistanceX / 3));
            super.onScrolled(recyclerView, dx, dy);
        }
    }
}
