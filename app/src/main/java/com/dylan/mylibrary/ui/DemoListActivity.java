package com.dylan.mylibrary.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.dylan.mylibrary.HorizontalScrollBackActivity;
import com.dylan.mylibrary.R;
import com.dylan.mylibrary.adapter.DemoListAdapter;
import com.dylan.mylibrary.ui.customtitle.CustomTittleUitlActivity;
import com.dylan.mylibrary.ui.edittext.EditNumberActivity;
import com.dylan.mylibrary.ui.filedownloader.FileDownLoaderActivity;
import com.dylan.mylibrary.ui.gridviewpager.GridViewPagerActivity;
import com.dylan.mylibrary.ui.install.AutoInstallActivity;
import com.dylan.mylibrary.ui.lazyload.LazyFragmentActivity;
import com.dylan.mylibrary.ui.loadingdialog.LoadingDialogActivity;
import com.dylan.mylibrary.ui.screenshoot.ScreenShootActivity;
import com.dylan.mylibrary.ui.slidingrefresh.SlidingRefreshActivity;
import com.dylan.mylibrary.ui.snaphelper.RecyclerSnapHelperActivity;
import com.dylan.mylibrary.ui.tab.TabActivity;
import com.dylan.mylibrary.ui.tab.TabLayoutActivity;
import com.dylan.mylibrary.ui.unscollviewpager.UnScrollViewPagerActivity;
import com.dylan.mylibrary.ui.wraplayoutmanager.WrapLayoutActivity;
import com.dylan.library.screen.ScreenUtils;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.utils.Logger;
import com.dylan.library.utils.SignatureUtils;
import com.dylan.library.widget.DLAlertDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Dylan on 2016/12/16.
 */

public class DemoListActivity extends AppCompatActivity {
    private GridView mGridView;
    private DemoListAdapter mAdapter;
    private String[] demoNames = {"RecyclerViewSnapHelper", "PhotoView", "VerticalPager",
            "EditnnumberHelper", "LayoutCircleAnimation", "ScreenShoot",
            "GridViewPager", "WrapLayoutManager", "LoadingDialog", "listview侧滑删除",
            "BulletinBoard", "PhotoSelector", "SlidingRefresh", "WebViewActvity",
            "侧滑销毁Activity", "语音发送", "FileDownLoader", "TabLayout", "CheckBoxListAdapter",
            "LazyFragment", "RedPointTextView", "UnScrollViewPagerActivity",
            "PullToRefreshScrollViewActivity","TextSwitchActivity","全局更换字体"};
    private Class[] classes = {RecyclerSnapHelperActivity.class, PhotoViewActivity.class, VerticalViewPagerActivity.class,
            EditNumberActivity.class, CircleAnimationActivity.class, ScreenShootActivity.class,
            GridViewPagerActivity.class, WrapLayoutActivity.class, LoadingDialogActivity.class,
            ExpandableListItemActivity.class, BulletinBoardActivity.class, PhotoPickerActivity.class,
            SlidingRefreshActivity.class, WebViewActivity.class, HorizontalScrollBackActivity.class,
            VoiceRecordActivity.class, FileDownLoaderActivity.class, TabLayoutActivity.class,
            CheckBoxListAdapterActivity.class, LazyFragmentActivity.class, RedPointTextViewActivity.class,
            UnScrollViewPagerActivity.class,PullToRefreshScrollViewActivity.class,TextSwitchActivity.class,
            ModifyFontActivity.class};

    private DLAlertDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demolist);
        initEvent();
        ScreenUtils.setStatusBarLightMode(getWindow(), Color.WHITE);
        mDialog = new DLAlertDialog(this);
        String signature = SignatureUtils.getMD5Signature(this, "com.wxhkj.weixiuhui");
        if (EmptyUtils.isNotEmpty(signature)) {
            Logger.e("signature " + signature);
        }


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
            Intent intent = new Intent(DemoListActivity.this,classes[position]);
            startActivity(intent);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
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
