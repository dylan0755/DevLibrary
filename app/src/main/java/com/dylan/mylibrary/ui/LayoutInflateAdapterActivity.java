package com.dylan.mylibrary.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dylan.library.adapter.LayoutInflaterAdapter;
import com.dylan.library.utils.DensityUtils;
import com.dylan.library.utils.thread.ThreadUtils;
import com.dylan.library.widget.FlowLayout;
import com.dylan.library.widget.SupportFlowLayout;
import com.dylan.mylibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2022/4/3
 * Desc:
 */

public class LayoutInflateAdapterActivity extends AppCompatActivity {
    LinearLayout llCategory;
    private FlowLayout flowLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layoutinflater_adapter);
        llCategory = findViewById(R.id.llCategory);
        flowLayout = findViewById(R.id.flowLayout);

        List<String> list1 = new ArrayList<>();
        list1.add("公告");
        list1.add("话题");
        list1.add("视频");
        list1.add("图片");
        flowLayout.setItemSpacing(DensityUtils.dp2px(this, 24));
       final Adapter adapter1=new Adapter(list1);
        flowLayout.setAdapter(adapter1);

        List<String> list2 = new ArrayList<>();
        list2.add("公告");
        list2.add("话题");
        list2.add("视频");
        list2.add("图片");
        final  Adapter adapter2=new Adapter(list2);
        adapter2.bindViewGroup(llCategory);

        ThreadUtils.runOnMainThreadDelay(new Runnable() {
            @Override
            public void run() {
                adapter1.getDataList().add(1,"55555");
                adapter2.getDataList().add(1,"55555");
                adapter1.notifyDataSetChanged();
                adapter2.notifyDataSetChanged();
                ThreadUtils.runOnMainThreadDelay(new Runnable() {
                    @Override
                    public void run() {
                        adapter1.getDataList().remove(2);
                        adapter2.clear();
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                },2000);
            }
        },3500);

    }


    class Adapter extends LayoutInflaterAdapter<String> {


        public Adapter(List<String> dataList) {
            super(dataList);
        }

        @Override
        public int getLayoutId() {
            return R.layout.view_live_widget_cate_select;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, String o, final int position) {
            TextView tvTagName=holder.findViewById(R.id.tvTagName);
            View indicator=holder.findViewById(R.id.indicator);
            tvTagName.setText(o);

            if (isItemSelected(position)) {
                tvTagName.setTextColor(Color.parseColor("#FADB22"));
                indicator.setVisibility(View.VISIBLE);
            } else {
                tvTagName.setTextColor(Color.parseColor("#999999"));
                indicator.setVisibility(View.INVISIBLE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isItemSelected(position))return;
                    setSelect(position);
                }
            });
        }
    };


}
