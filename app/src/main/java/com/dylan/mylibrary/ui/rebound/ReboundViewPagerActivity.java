package com.dylan.mylibrary.ui.rebound;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dylan.library.widget.rebound.ReboundViewPager;
import com.dylan.mylibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2020/2/26
 * Desc:
 */
public class ReboundViewPagerActivity extends AppCompatActivity {
    ReboundViewPager viewPager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rebound_viewpager);
        viewPager=findViewById(R.id.viewPager);
        ViewPagerAdapter adapter=new ViewPagerAdapter();
        viewPager.setAdapter(adapter);
    }



    class ViewPagerAdapter extends PagerAdapter{

        private List<View> viewList=new ArrayList<>();

        public ViewPagerAdapter (){
            for (int i=0;i<3;i++){
                ImageView iv=new ImageView(ReboundViewPagerActivity.this);
                iv.setLayoutParams(new ViewPager.LayoutParams());
                if (i==0){
                    iv.setBackgroundColor(Color.parseColor("#a0a0a0"));
                }else if (i==1){
                    iv.setBackgroundColor(Color.BLUE);
                }else if (i==2){
                    iv.setBackgroundColor(Color.GREEN);
                }

                viewList.add(iv);
            }
        }
        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view==o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(viewList.get(position));
        }
    }




}
