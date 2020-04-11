package com.dylan.mylibrary.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;

import com.dylan.library.widget.tab.HorizontalScrollTabLayout;
import com.dylan.library.widget.tab.HorizontalScrollTabLayout2;
import com.dylan.library.widget.tab.TabItem;
import com.dylan.mylibrary.R;
import com.dylan.mylibrary.ui.tab.TabLayoutActivity;
import com.dylan.mylibrary.ui.tab.TabLayoutFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2020/4/11
 * Desc:
 */
public class HorizontalScrollTabLayoutActivity extends AppCompatActivity {
    HorizontalScrollTabLayout2 mTabLayout;
    ViewPager mViewPager;
    public static final String TAB_INDEX="tabIndex";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontalscrolltab);
        mTabLayout= findViewById(R.id.tabLayout);
        mViewPager=  findViewById(R.id.vpager);
        initTabLayout();
    }



    private void initTabLayout() {
        int selectColor = Color.parseColor("#333333");
        int normalColor = Color.parseColor("#666666");
        mViewPager.setOffscreenPageLimit(2);
        mTabLayout.setTabTextSize(16);
        mTabLayout.setIndicatorShap(HorizontalScrollTabLayout.SHAPE_TRIANGLE);
        mTabLayout.setTabColor(normalColor);
        mTabLayout.setTabSelectColor(selectColor);
        mTabLayout.setIndicatorColor(Color.BLACK);
        mTabLayout.setMaxVisiableCount(5);
        mTabLayout.setUpWidthViewPager(mViewPager);
        // mTabLayout.syncTextIconColor(false);
        mTabLayout.addTab(mTabLayout.newTab().setTabTitle("粤菜"))
                .addTab(mTabLayout.newTab().setTabTitle("川菜"))
                .addTab(mTabLayout.newTab().setTabTitle("家常菜")
                        .setTabIconRight(R.mipmap.ic_arrow_down))
                .addTab(mTabLayout.newTab().setTabTitle("卤菜"))
                .addTab(mTabLayout.newTab().setTabTitle("湘菜"))
                .addTab(mTabLayout.newTab().setTabTitle("测试"))
                .addTab(mTabLayout.newTab().setTabTitle("测试"))
                .addTab(mTabLayout.newTab().setTabTitle("测试"))
                .create();
        List<Fragment> fragmentList = new ArrayList<Fragment>();
        for (int i = 0; i < 8; i++) {
            TabLayoutFragment fragment = new TabLayoutFragment();
            Bundle bundle = new Bundle();
            bundle.putString("page", "" + i);
            fragment.setArguments(bundle);
            fragmentList.add(fragment);
        }
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), fragmentList);
        mViewPager.setAdapter(adapter);
        mTabLayout.setTabSelectListener(new TabSelectListenerImpl());

        Intent intent=getIntent();
        int index=intent.getIntExtra(TAB_INDEX,0);
        mViewPager.setCurrentItem(index);
        mTabLayout.setSelect(index);
    }




    class MyPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public MyPagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            fragments = list;
        }
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }
        @Override
        public int getCount() {
            return fragments.size();
        }
    }


    class TabSelectListenerImpl implements HorizontalScrollTabLayout2.TabSelectListener{

        @Override
        public void onSelect(int position, TabItem tabItem) {

        }

        @Override
        public void unSelect(int position, TabItem tabItem) {

        }

        @Override
        public void reSelected(int position, final TabItem tabItem) {
            if (position==2){
                PopupMenu popup = new PopupMenu(HorizontalScrollTabLayoutActivity.this, tabItem);
                popup.getMenuInflater().inflate(R.menu.accessoryapply_all_type, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        String title= (String) item.getTitle();
                        tabItem.setTabTitle(title);
                        return true;
                    }
                });
                popup.show();
            }
        }
    }
}
