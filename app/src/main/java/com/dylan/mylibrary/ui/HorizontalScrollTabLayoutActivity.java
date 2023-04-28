package com.dylan.mylibrary.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.MenuItem;

import com.dylan.library.widget.tab.HorizontalScrollTabLayout;
import com.dylan.library.widget.tab.SlidingTabLayout;
import com.dylan.library.widget.tab.TabItem;
import com.dylan.mylibrary.R;
import com.dylan.mylibrary.ui.tab.TabLayoutFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2020/4/11
 * Desc:
 */
public class HorizontalScrollTabLayoutActivity extends AppCompatActivity {
    SlidingTabLayout mSlidingTabLayout;
    ViewPager mViewPager;
    public static final String TAB_INDEX="tabIndex";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontalscrolltab);
        mSlidingTabLayout = findViewById(R.id.tabLayout);
        mViewPager=  findViewById(R.id.vpager);
        initTabLayout();
    }



    private void initTabLayout() {
        int selectColor = Color.parseColor("#333333");
        int normalColor = Color.parseColor("#666666");
        mViewPager.setOffscreenPageLimit(2);
        mSlidingTabLayout.setTabTextSize(16);
        mSlidingTabLayout.setIndicatorShap(HorizontalScrollTabLayout.SHAPE_RECTANGLE);
        mSlidingTabLayout.setTabColor(normalColor);
        mSlidingTabLayout.setTabSelectColor(selectColor);
        mSlidingTabLayout.setIndicatorColor(Color.BLACK);
        mSlidingTabLayout.setIndicatorHeight(4);
        mSlidingTabLayout.setUpWidthViewPager(mViewPager);
        // mTabLayout.syncTextIconColor(false);
        mSlidingTabLayout.addTab(mSlidingTabLayout.newTab().setTabTitle("粤菜"))
                .addTab(mSlidingTabLayout.newTab().setTabTitle("川菜"))
                .addTab(mSlidingTabLayout.newTab().setTabTitle("卤菜"))
                .addTab(mSlidingTabLayout.newTab().setTabTitle("湘菜"))
                .addTab(mSlidingTabLayout.newTab().setTabTitle("桂林米粉"))
                .addTab(mSlidingTabLayout.newTab().setTabTitle("测试1"))
                .addTab(mSlidingTabLayout.newTab().setTabTitle("测试"))
                .addTab(mSlidingTabLayout.newTab().setTabTitle("测试2"))
                .addTab(mSlidingTabLayout.newTab().setTabTitle("测试"))
                .addTab(mSlidingTabLayout.newTab().setTabTitle("测试4"))
                .addTab(mSlidingTabLayout.newTab().setTabTitle("测试5"))
                .addTab(mSlidingTabLayout.newTab().setTabTitle("测试"))
                .addTab(mSlidingTabLayout.newTab().setTabTitle("测试"))
                .create();
        List<Fragment> fragmentList = new ArrayList<Fragment>();
        for (int i = 0; i < mSlidingTabLayout.getTabCount(); i++) {
            TabLayoutFragment fragment = new TabLayoutFragment();
            Bundle bundle = new Bundle();
            bundle.putString("page", "" + i);
            fragment.setArguments(bundle);
            fragmentList.add(fragment);
        }
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), fragmentList);
        mViewPager.setAdapter(adapter);
        mSlidingTabLayout.setTabSelectListener(new TabSelectListenerImpl());

        Intent intent=getIntent();
        int index=intent.getIntExtra(TAB_INDEX,0);
        mViewPager.setCurrentItem(index);
        mSlidingTabLayout.setSelect(index);
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


    class TabSelectListenerImpl implements SlidingTabLayout.TabSelectListener{

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
