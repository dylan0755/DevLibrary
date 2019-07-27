package com.dylan.mylibrary.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import com.dylan.library.screen.ScreenUtils;
import com.dylan.mylibrary.R;
import com.dylan.mylibrary.ui.onlinepic.DataAdapter;
import com.dylan.mylibrary.ui.onlinepic.PreViewPager;
import com.dylan.mylibrary.ui.onlinepic.SelectorAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2019/7/24
 * Desc:
 */

public class OnLinePhotoPreviewActivity extends AppCompatActivity implements PreViewPager.onFinishActivityListener {
   public static final String EXTRA_LOCATION="location";
    public static final String EXTRA_STATUSBAR_HEIGHT="statusBarHeight";
    private PreViewPager viewPager;
    private RecyclerView rvSelector;
    private SelectorAdapter selectorAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_photo_preview);
        ScreenUtils.removeStatuBar(this);
        viewPager=findViewById(R.id.viewPager);
        rvSelector=findViewById(R.id.rvSelector);

        List<String> picUrls=new ArrayList<>();
        picUrls.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=291378222,233871465&fm=26&gp=0.jpg");
        picUrls.add("http://sjbz.fd.zol-img.com.cn/t_s640x1136c/g2/M00/01/04/ChMlWl01HCGIfugDABVvrwnLHUkAAL-dgA9wLsAFW_H618.jpg");
        picUrls.add("http://sjbz.fd.zol-img.com.cn/t_s640x960c/g2/M00/01/04/ChMlWV01HCiILuSNABr2gfseMgMAAL-dgH49K4AGvaZ315.jpg");

        //初始化指示器
        selectorAdapter=new SelectorAdapter();
        rvSelector.setAdapter(selectorAdapter);
        selectorAdapter.setIndicatorCount(picUrls.size(),0);

        //图片ViewPager
        DataAdapter adapter=new DataAdapter(picUrls);
        viewPager.setAdapter(adapter);
        if (getIntent()!=null){
            viewPager.setSourceViewLocation(getIntent().getIntArrayExtra(EXTRA_LOCATION));
        }
        viewPager.setOnFinishActivityListener(this);
        rvSelector.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageScrollStateChanged(int state) {}
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

}
