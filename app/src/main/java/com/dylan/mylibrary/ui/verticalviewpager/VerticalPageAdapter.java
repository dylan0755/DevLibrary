package com.dylan.mylibrary.ui.verticalviewpager;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2019/8/1
 * Desc:
 */

public class VerticalPageAdapter extends PagerAdapter{
    private List<ImageView> viewList=new ArrayList<>();
    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view==o;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView;
        if (viewList.isEmpty()){
            imageView=new ImageView(container.getContext());
            ViewPager.LayoutParams layoutParams=new ViewPager.LayoutParams();
            layoutParams.width= ViewPager.LayoutParams.MATCH_PARENT;
            layoutParams.height=ViewPager.LayoutParams.MATCH_PARENT;
            imageView.setLayoutParams(layoutParams);
        }else{
             imageView=viewList.remove(0);
        }

        if (position==0){
            imageView.setBackgroundColor(Color.GRAY);
        }else if (position==1){
            imageView.setBackgroundColor(Color.GREEN);
        }else if (position==2){
            imageView.setBackgroundColor(Color.BLUE);
        }else if (position==3){
            imageView.setBackgroundColor(Color.YELLOW);
        }else if (position==4){
            imageView.setBackgroundColor(Color.BLACK);
        }

        container.addView(imageView);
        return imageView;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView) object);
        viewList.add((ImageView) object);
    }
}
