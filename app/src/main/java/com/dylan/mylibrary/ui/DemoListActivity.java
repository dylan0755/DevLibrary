package com.dylan.mylibrary.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;

import com.dylan.library.screen.ScreenUtils;
import com.dylan.library.utils.DensityUtils;

import com.dylan.library.utils.Logger;
import com.dylan.library.utils.ParamMapBuilder;
import com.dylan.library.widget.GridViewPager;
import com.dylan.mylibrary.HorizontalScrollBackActivity;
import com.dylan.mylibrary.IRecyclerViewActivity;
import com.dylan.mylibrary.R;
import com.dylan.mylibrary.ui.edittext.EditNumberActivity;
import com.dylan.mylibrary.ui.filedownloader.FileDownLoaderActivity;
import com.dylan.mylibrary.ui.gridviewpager.GridViewPagerActivity;
import com.dylan.mylibrary.ui.lazyload.LazyFragmentActivity;
import com.dylan.mylibrary.ui.loadingdialog.LoadingDialogActivity;
import com.dylan.mylibrary.ui.marginspan.FirstLineMarginLeftActivity;
import com.dylan.mylibrary.ui.opengl.OpenglDemoActivity;
import com.dylan.mylibrary.ui.popwindow.PopWindowDemoActivity;
import com.dylan.mylibrary.ui.rebound.ReboundActivity;
import com.dylan.mylibrary.ui.screenshoot.ScreenShootActivity;
import com.dylan.mylibrary.ui.slidingrefresh.SlidingRefreshActivity;
import com.dylan.mylibrary.ui.snaphelper.RecyclerSnapHelperActivity;
import com.dylan.mylibrary.ui.unscollviewpager.UnScrollViewPagerActivity;
import com.dylan.mylibrary.ui.wraplayoutmanager.WrapLayoutActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 2016/12/16.
 */

public class DemoListActivity extends AppCompatActivity {
    private GridViewPager mGridPager;
    private String[] demoNames = {"RecyclerViewSnapHelper", "ScaleUpPhotoView", "IRecyclerView",
            "EditnnumberHelper", "LayoutCircleAnimation", "ScreenShootUtils",
            "GridViewPager", "WrapLayoutManager", "LoadingDialog", "listview侧滑删除",
            "BulletinBoard", "PhotoSelector", "SlidingRefresh",
            "侧滑销毁Activity", "语音发送", "FileDownLoader", "HorizontalScrollTabLayout", "CheckBoxListAdapter",
            "LazyFragment", "RedPointTextView", "UnScrollViewPagerActivity",
            "TextSwitchActivity", "全局更换字体", "FirstLineMargin",
            "贝塞尔曲线", "DashLineView", "InputPasswordDialog", "CountDownCircleView",
            "ProgressWebActivity", "VideoPlayerGesture", "拖拽回弹组件", "BitmapHelper",
            "ShadowLayout", "ShapeView", "ArcMenu", "PopWindow", "ProgressBarActivity",
            "NoExtraUiEditDemo","singleClickDemo","DragMapView","SmsCodeCounter",
            "Camera+OpenGl","IOSSwitchView"};
    private Class[] classes = {RecyclerSnapHelperActivity.class, ScaleUpPhotoViewActivity.class, IRecyclerViewActivity.class,
            EditNumberActivity.class, CircleAnimationActivity.class, ScreenShootActivity.class,
            GridViewPagerActivity.class, WrapLayoutActivity.class, LoadingDialogActivity.class,
            ExpandableListItemActivity.class, BulletinBoardActivity.class, LocalPhotoSelectActivity.class,
            SlidingRefreshActivity.class, HorizontalScrollBackActivity.class,
            VoiceRecordActivity.class, FileDownLoaderActivity.class, HorizontalScrollTabLayoutActivity.class,
            CheckBoxListAdapterActivity.class, LazyFragmentActivity.class, RedPointTextViewActivity.class,
            UnScrollViewPagerActivity.class, TextSwitchActivity.class,
            ModifyFontActivity.class, FirstLineMarginLeftActivity.class, BezierCurveActivity.class,
            DashLineViewActivity.class, InputPasswordActivity.class, CountDownCircleViewActivity.class,
            ProgressWebViewActivity.class, VideoPlayerGestureActivity.class, ReboundActivity.class,
            BitmapHelperActivity.class, ShadowLayoutActivity.class, CustomShapeViewActivity.class,
            ArcMenuActivity.class, PopWindowDemoActivity.class, ProgressBarActivity.class,
            NoExtraUiEditTextDemoActivity.class, SingleClickTestActivity.class,DragMapViewDemoActivity.class,
            SmsCodeCounterActivity.class, OpenglDemoActivity.class,SwitchViewActivity.class};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DensityUtils.setCustomApplicationDensityInWidth(360, this, getApplication());
        setContentView(R.layout.activity_demolist);
        initEvent();
        ScreenUtils.setStatusBarLightMode(getWindow(), Color.WHITE);



    }


    private void initEvent() {
        mGridPager = findViewById(R.id.gridPager);


        List<GridDemoItem> list = new ArrayList<>();
        for (int i = 0; i < demoNames.length; i++) {
            String name = demoNames[i];
            Class clazz = classes[i];
            GridDemoItem item = new GridDemoItem(name, clazz);
            list.add(item);
        }

        mGridPager.setAttachAdapterListener(new GridViewPager.AttachAdapterListener() {
            @Override
            public void attachAdapter(GridView gridView, List datasPerPage) {
                GridDemoItemAdapter adapter = new GridDemoItemAdapter();
                adapter.bind(datasPerPage);
                gridView.setAdapter(adapter);
                adapter.setOnItemClick(new GridDemoItemAdapter.OnItemClick() {
                    @Override
                    public void onItemClick(GridDemoItem item) {
                        Intent intent = new Intent(DemoListActivity.this, item.getClzz());
                        startActivity(intent);
                    }
                });
            }
        });
        mGridPager.setDataList(list, 3, 30);
        Logger.e("测试堆栈输出");
    }


}
