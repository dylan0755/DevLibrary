package com.dankal.mylibrary.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.dankal.mylibrary.R;
import com.dankal.mylibrary.adapter.DemoListAdapter;
import com.dankal.mylibrary.ui.customtitle.CustomTittleUitlActivity;
import com.dankal.mylibrary.ui.date.DateTestActivity;
import com.dankal.mylibrary.ui.edittext.EditNumberActivity;
import com.dankal.mylibrary.ui.gridviewpager.GridViewPagerActivity;
import com.dankal.mylibrary.ui.install.AutoInstallActivity;
import com.dankal.mylibrary.ui.loadingdialog.LoadingDialogActivity;
import com.dankal.mylibrary.ui.screenshoot.ScreenShootActivity;
import com.dankal.mylibrary.ui.tab.TabActivity;
import com.dankal.mylibrary.ui.wraplayoutmanager.WrapLayoutActivity;
import com.dylan.library.screen.ScreenUtils;
import com.dylan.library.widget.DLAlertDialog;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Dylan on 2016/12/16.
 */

public class DemoListActivity extends Activity {
    private GridView mGridView;
    private DemoListAdapter mAdapter;
    private String[] demoNames = {"tabActivity", "CustomTitleUtil", "DateUtils",
            "EditnnumberHelper", "autoInstall","ScreenShoot",
            "GridViewPager","WrapLayoutManager","LoadingDialog","listview侧滑删除",
            "BulletinBoard","PhotoSelector","PermissionSetting","WebViewActvity"};
    private Class[] classes = {TabActivity.class, CustomTittleUitlActivity.class, DateTestActivity.class,
            EditNumberActivity.class, AutoInstallActivity.class, ScreenShootActivity.class,
            GridViewPagerActivity.class, WrapLayoutActivity.class, LoadingDialogActivity.class,
            ExpandableListItemActivity.class, BulletinBoardActivity.class,PhotoPickerActivity.class,
            PermissionSettingActivity.class,WebViewActivity.class};

    private DLAlertDialog mDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demolist);
        initEvent();
        ScreenUtils.setStatusBarLightMode(getWindow(), Color.WHITE);
        mDialog=new DLAlertDialog(this);
    }

    private void initEvent() {
        mGridView = (GridView) findViewById(R.id.gv_demolist);
        mAdapter = new DemoListAdapter();
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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            mDialog.show("提示", "要退出应用吗", new DLAlertDialog.CallBack() {
                @Override
                public void onCancel() {

                }

                @Override
                public void onSure() {
                     finish();
                }
            });
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



}
