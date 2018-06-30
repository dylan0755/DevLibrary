package com.dankal.mylibrary.ui.unscollviewpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.dankal.mylibrary.R;
import com.dankal.mylibrary.ui.tab.Test1;
import com.dankal.mylibrary.ui.tab.Test2;
import com.dankal.mylibrary.ui.tab.Test3;
import com.dylan.library.widget.UnScrollViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 2018/6/23.
 */

public class UnScrollViewPagerActivity extends AppCompatActivity{
    UnScrollViewPager viewPager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unscrollviewpager);
        viewPager= (UnScrollViewPager) findViewById(R.id.unscrollviewpager);
        viewPager.setCanScroll(false);
        List<Fragment> fragments=new ArrayList<>();
        fragments.add(new FragmentTest1());
        fragments.add(new FragmentTest2());
        fragments.add(new FragmentTest3());
        FragmentDataAdapter adapter=new FragmentDataAdapter(getSupportFragmentManager(),fragments);
        viewPager.setAdapter(adapter);
    }


    static class FragmentDataAdapter extends FragmentPagerAdapter{
        private List<Fragment> fragmentList;
        public FragmentDataAdapter(FragmentManager fm,List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList=fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    public  void toToggle(View view){
        Button button= (Button) view;
        String tag= (String) button.getTag();
        if ("Y".equals(tag)){
            button.setTag("N");
            viewPager.setCanScroll(false);
            button.setText("恢复左右滑动");
        }else{
            button.setTag("Y");
            viewPager.setCanScroll(true);
            button.setText("禁止左右滑动");
        }

    }
}
