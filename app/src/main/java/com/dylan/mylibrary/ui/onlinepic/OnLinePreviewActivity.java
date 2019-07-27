package com.dylan.mylibrary.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import com.dylan.library.screen.ScreenUtils;
import com.dylan.library.utils.Logger;
import com.dylan.mylibrary.R;
import com.dylan.mylibrary.ui.onlinepic.ClickViewPoint;
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

public class OnLinePreviewActivity extends AppCompatActivity implements PreViewPager.onFinishActivityListener {
   public static final String EXTRA_LOCATION_WITHOUT_STATUS ="location";
    private PreViewPager viewPager;
    private RecyclerView rvSelector;
    private SelectorAdapter selectorAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_preview);
        ScreenUtils.removeStatuBar(this);
        viewPager=findViewById(R.id.viewPager);
        rvSelector=findViewById(R.id.rvSelector);

        List<String> picUrls=new ArrayList<>();
        picUrls.add("http://img4.imgtn.bdimg.com/it/u=2958967064,2714608608&fm=26&gp=0.jpg");
        picUrls.add("http://img4.imgtn.bdimg.com/it/u=2170225557,758070829&fm=26&gp=0.jpg");
       // picUrls.add("http://img5.imgtn.bdimg.com/it/u=3304966988,1696890235&fm=26&gp=0.jpg");
        picUrls.add("http://sjbz.fd.zol-img.com.cn/t_s640x1136c/g2/M00/01/04/ChMlWl01HCGIfugDABVvrwnLHUkAAL-dgA9wLsAFW_H618.jpg");
        picUrls.add("http://img4.imgtn.bdimg.com/it/u=3999606577,2503585579&fm=26&gp=0.jpg");
        picUrls.add("http://img4.imgtn.bdimg.com/it/u=3077036428,433114230&fm=26&gp=0.jpg");
        picUrls.add("http://img3.imgtn.bdimg.com/it/u=332908813,1086226178&fm=26&gp=0.jpg");
        //初始化指示器
        selectorAdapter=new SelectorAdapter();
        selectorAdapter.setIndicatorCount(picUrls.size(),0);
        rvSelector.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        rvSelector.setAdapter(selectorAdapter);

        //图片ViewPager
        DataAdapter adapter=new DataAdapter(picUrls);
        viewPager.setAdapter(adapter);
        viewPager.setOnFinishActivityListener(this);
        try{
            ArrayList<ClickViewPoint.Point> list =(ArrayList<ClickViewPoint.Point>) getIntent().getSerializableExtra(EXTRA_LOCATION_WITHOUT_STATUS);
            viewPager.setSourceViewLocation(list);
        }catch (Exception e){
            Logger.e(e);
        }
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
