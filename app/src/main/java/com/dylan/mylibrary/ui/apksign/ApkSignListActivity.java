package com.dylan.mylibrary.ui.apksign;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ListView;

import com.dylan.mylibrary.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ApkSignListActivity extends AppCompatActivity {
    ListView mListView;
    private AppAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apksign_list);
        mListView = (ListView) findViewById(R.id.listview);
        HashMap<Integer, List<PackageInfo>> map = getPackageInfosMap();
        List<PackageInfo> dataList=new ArrayList<>();
        dataList.add(new PackInfoDivider(true));
        dataList.addAll(map.get(1));
        dataList.add(new PackInfoDivider(false));
        dataList.addAll(map.get(0));

        mAdapter=new AppAdapter();
        mListView.setAdapter(mAdapter);
        mAdapter.bind(dataList);
    }


    public HashMap<Integer, List<PackageInfo>> getPackageInfosMap() {
        HashMap<Integer, List<PackageInfo>> map = new HashMap<>();
        PackageManager packageManager = getPackageManager();
        List<PackageInfo> list = packageManager.getInstalledPackages(0);
        List<PackageInfo> systemAppList = new ArrayList<>();
        List<PackageInfo> appList = new ArrayList<>();
        for (PackageInfo packageInfo : list) {
            if ((packageInfo.applicationInfo.flags & packageInfo.applicationInfo.FLAG_SYSTEM) <= 0) {//第三方应用
                appList.add(packageInfo);
            } else {
                systemAppList.add(packageInfo);
            }
        }
        map.put(0, systemAppList);
        map.put(1, appList);
        return map;
    }


}
