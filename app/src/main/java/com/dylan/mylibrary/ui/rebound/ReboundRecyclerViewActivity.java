package com.dylan.mylibrary.ui.rebound;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;

import com.dylan.library.callback.SingleClickListener;
import com.dylan.library.test.TestAdapter;
import com.dylan.mylibrary.R;
import com.dylan.library.widget.rebound.ReboundRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2020/2/17
 * Desc:
 */
public class ReboundRecyclerViewActivity extends AppCompatActivity {
    private ReboundRecyclerView rvVertical;
    private ReboundRecyclerView rvHorizontal;
    private Button btnToggle;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rebond_recyclerview);
        initRecyclerView();
        btnToggle=findViewById(R.id.btnToggle);
        btnToggle.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                 if (rvHorizontal.getVisibility()!=View.VISIBLE){
                     rvHorizontal.setVisibility(View.VISIBLE);
                     rvVertical.setVisibility(View.GONE);
                     btnToggle.setText("切换为竖屏演示");
                 }else{
                     rvHorizontal.setVisibility(View.GONE);
                     rvVertical.setVisibility(View.VISIBLE);
                     btnToggle.setText("切换为横屏演示");
                 }
            }
        });
    }

    private void initRecyclerView() {
        rvVertical=findViewById(R.id.rvVertical);
        rvHorizontal=findViewById(R.id.rvHorizontal);
        rvVertical.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rvHorizontal.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        TestAdapter testAdapter=new TestAdapter();
        rvVertical.setAdapter(testAdapter);
        rvHorizontal.setAdapter(testAdapter);

        List<String> list=new ArrayList<>();
        for (int i=0;i<15;i++){
            list.add("item "+(i+1));
        }

        rvHorizontal.setVisibility(View.GONE);
       testAdapter.bind(list);


    }
}
