package com.dylan.mylibrary.ui.onlinepic;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;


import com.dylan.library.utils.Logger;
import com.dylan.mylibrary.R;
import com.dylan.mylibrary.ui.onlinepic.glide.progress.ProgressImageLayout;

import java.util.ArrayList;

/**
 * Author: Dylan
 * Date: 2019/7/24
 * Desc:
 */

public class OnLinePreviewActivity extends AppCompatActivity implements PreViewPager.onFinishActivityListener {
    public static final String EXTRA_PICTURE_URLS = "picUrlArrayList";
    public static final String EXTRA_LOCATION_WITHOUT_STATUS = "location";
    public static final String EXTRA_PICTURE_POSTION = "position";
    private PreViewPager viewPager;
    private RecyclerView rvSelector;
    private SelectorAdapter selectorAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_preview);
        viewPager = findViewById(R.id.viewPager);
        rvSelector = findViewById(R.id.rvSelector);
        ArrayList<String> picUrls;
        try {
            picUrls = (ArrayList<String>) getIntent().getSerializableExtra(EXTRA_PICTURE_URLS);
            if (picUrls == null || picUrls.isEmpty()) {
                Toast.makeText(this, "Empty data!!!", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            Toast.makeText(this, "need ArrayList<String> Data", Toast.LENGTH_SHORT).show();
            return;
        }

        int currentPostion = getIntent().getIntExtra(EXTRA_PICTURE_POSTION, 0);
        //初始化指示器
        selectorAdapter = new SelectorAdapter();
        selectorAdapter.setIndicatorCount(picUrls.size(), currentPostion);
        rvSelector.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvSelector.setAdapter(selectorAdapter);

        //图片ViewPager
        DataAdapter adapter = new DataAdapter(picUrls);
        viewPager.setAdapter(adapter);
        viewPager.setOnFinishActivityListener(this);
        viewPager.setCurrentItem(currentPostion);
        try {
            ArrayList<ClickViewPoint.Point> list = (ArrayList<ClickViewPoint.Point>) getIntent().getSerializableExtra(EXTRA_LOCATION_WITHOUT_STATUS);
            viewPager.setSourceViewLocation(list);
        } catch (Exception e) {
            Logger.e(e);
        }
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageSelected(int position) {
                selectorAdapter.setCurrentPosition(position);
            }
        });


    }


    @Override
    public void toFinishActivity() {
         finish();
    }

    @Override
    public void hideIndicator() {
        rvSelector.setVisibility(View.GONE);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.preview_activity_exit);
    }

    public static void overridePendingTransition(Activity activity){
        activity.overridePendingTransition(R.anim.anim_enable_false, 0);

    }

}
