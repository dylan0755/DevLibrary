package com.dylan.mylibrary.ui.rebound;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dylan.library.utils.ToastUtils;
import com.dylan.library.widget.rebound.ReboundListView;
import com.dylan.mylibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2020/2/18
 * Desc:
 */
public class ReboundListViewActivity extends AppCompatActivity {
    private ReboundListView reboundListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reboundlistview);
        reboundListView = findViewById(R.id.reboundListView);
        //新建一个list存放数据
       final List<String> listdata = new ArrayList<String>();
        for (char a='A';a<='Z';a++){
            listdata.add(String.valueOf(a));
        }

        //列表
        final ArrayAdapter<String> adp2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listdata);
        reboundListView.setAdapter(adp2);
        reboundListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtils.show(listdata.get(position));
            }
        });
    }
}
