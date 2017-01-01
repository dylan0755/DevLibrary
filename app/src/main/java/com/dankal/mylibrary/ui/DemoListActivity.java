package com.dankal.mylibrary.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.dankal.mylibrary.R;
import com.dankal.mylibrary.adapter.DemoListAdapter;
import com.dankal.mylibrary.ui.customtitle.CustomTittleUitlActivity;
import com.dankal.mylibrary.ui.date.DateTestActivity;
import com.dankal.mylibrary.ui.edittext.EditNumberActivity;
import com.dankal.mylibrary.ui.install.AutoInstallActivity;
import com.dankal.mylibrary.ui.screenshoot.ScreenShootActivity;
import com.dankal.mylibrary.ui.tab.TabActivity;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Dylan on 2016/12/16.
 */

public class DemoListActivity extends Activity {
    private GridView mGridView;
    private DemoListAdapter mAdapter;
    private String[] demoNames = {"tabActivity", "CustomTitleUtil", "DateUtils",
            "EditnnumberHelper", "autoInstall","ScreenShoot"};
    private Class[] classes = {TabActivity.class, CustomTittleUitlActivity.class, DateTestActivity.class,
            EditNumberActivity.class, AutoInstallActivity.class, ScreenShootActivity.class};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demolist);
        initEvent();
    }

    private void initEvent() {
        mGridView = (GridView) findViewById(R.id.gv_demolist);
        mAdapter = new DemoListAdapter(this);
        mGridView.setOnItemClickListener(new ItemClickListener());
        mGridView.setAdapter(mAdapter);
        List<String> list = Arrays.asList(demoNames);
        mAdapter.bind(list);
    }



    class ItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(DemoListActivity.this, classes[position]);
            startActivity(intent);
        }
    }
}
