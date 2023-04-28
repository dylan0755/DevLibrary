package com.dylan.mylibrary.ui;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.widget.ListView;

import com.dylan.mylibrary.R;
import com.dylan.mylibrary.adapter.UserDataAdapter;
import com.dylan.mylibrary.bean.UserBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 2018/2/6.
 */

public class CheckBoxListAdapterActivity extends Activity {
    private ListView mListView;
    private UserDataAdapter mAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkboxactivity);
        mListView= (ListView) findViewById(R.id.listview);
        mAdapter=new UserDataAdapter();
        mListView.setAdapter(mAdapter);

        List<UserBean> list=new ArrayList<>();
        for (int i=0;i<25;i++){
            UserBean userBean=new UserBean(""+i);
            list.add(userBean);
        }
        mAdapter.bind(list);
    }
}
