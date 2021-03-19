package com.dylan.mylibrary.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.dylan.library.adapter.CommonFragmentPagerAdapter;
import com.dylan.library.graphics.ColorShades;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.mylibrary.R;
import com.flyco.tablayout.SlidingScaleTabLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Author: Dylan
 * Date: 2021/03/19
 * Desc:
 */
public class ColorShadesActivity extends AppCompatActivity {
    LinearLayout llTabLayoutBackground;
    SlidingScaleTabLayout tabLayout;
    ViewPager viewPager;
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    String[] backgroundColors = new String[]{"#323443", "#FFF9E4CF", "#FFAA2423"};
    String[] tabColors = new String[]{"#FFFDE4BB", "#FF532622", "#FFF7D7A6"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colorshades);
        llTabLayoutBackground = findViewById(R.id.llTabLayoutBackground);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        initView();
    }


    private void initView() {
        fragments.add(new EmptyTestFragment());
        fragments.add(new EmptyTestFragment());
        fragments.add(new EmptyTestFragment());
        titles.add("VIP会员");
        titles.add("SVIP会员");
        titles.add("合伙人");

        CommonFragmentPagerAdapter commonFragmentPagerAdapter = new CommonFragmentPagerAdapter(getSupportFragmentManager());
        commonFragmentPagerAdapter.addTitles(titles);
        commonFragmentPagerAdapter.addFragments(fragments);
        viewPager.setAdapter(commonFragmentPagerAdapter);
        tabLayout.setViewPager(viewPager);
        llTabLayoutBackground.setBackgroundColor(Color.parseColor(backgroundColors[0]));
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            ColorShades backgroundShades = new ColorShades();
            ColorShades tabTextColorShades = new ColorShades();

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                String frontColorStr = backgroundColors[position % backgroundColors.length];
                if (EmptyUtils.isEmpty(frontColorStr)) frontColorStr = "#ffffff";
                String toColorStr = backgroundColors[(position + 1) % backgroundColors.length];
                if (EmptyUtils.isEmpty(toColorStr)) toColorStr = "#ffffff";
                backgroundShades.setFromColor(frontColorStr)
                        .setToColor(toColorStr)
                        .setShade(positionOffset);
                llTabLayoutBackground.setBackgroundColor(backgroundShades.generate());


                frontColorStr = tabColors[position % tabColors.length];
                if (EmptyUtils.isEmpty(frontColorStr)) frontColorStr = "#ffffff";
                toColorStr = tabColors[(position + 1) % tabColors.length];
                if (EmptyUtils.isEmpty(toColorStr)) toColorStr = "#ffffff";
                tabTextColorShades.setFromColor(frontColorStr)
                        .setToColor(toColorStr)
                        .setShade(positionOffset);
                int tabColor = tabTextColorShades.generate();
                tabLayout.setTextSelectColor(tabColor);
                tabLayout.setTextUnselectColor(tabColor);
            }


        });


    }


}
