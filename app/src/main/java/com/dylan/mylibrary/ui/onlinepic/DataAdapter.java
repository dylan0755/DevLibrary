package com.dylan.mylibrary.ui.onlinepic;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


import com.dylan.library.utils.Logger;
import com.dylan.mylibrary.R;
import com.dylan.mylibrary.ui.onlinepic.glide.GlideImageLoader;
import com.dylan.mylibrary.ui.onlinepic.glide.progress.ProgressImageLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2019/7/24
 * Desc:
 */

public class DataAdapter extends PagerAdapter {
    private ProgressImageLayout mCurrentItemView;
    private LastItemRecord lastItemRecord;

    private List<String> picUrlList;
    private List<ProgressImageLayout> cacheImageList = new ArrayList<>();

    public DataAdapter(List<String> picUrlList) {
        this.picUrlList = picUrlList;
    }

    /**
     *  ViewPager 滑到每个页面都会调用该方法
     */
    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        mCurrentItemView = (ProgressImageLayout) object;
        if (lastItemRecord==null){
            lastItemRecord=new LastItemRecord();
            lastItemRecord.imageLayout=mCurrentItemView;
            lastItemRecord.position=position;
        }else{
            if (lastItemRecord.position!=position){
                lastItemRecord.imageLayout.getPhotoView().restore();
                lastItemRecord.position=position;
                lastItemRecord.imageLayout=mCurrentItemView;
            }

        }
    }



    public ProgressImageLayout getCurrentItemView() {
        return mCurrentItemView;
    }

    @Override
    public int getCount() {
        return picUrlList.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull  final ViewGroup container, int position) {
        ProgressImageLayout imageLayout;
        if (cacheImageList.size() == 0) {
            imageLayout = (ProgressImageLayout) LayoutInflater.from(container.getContext()).inflate(R.layout.layout_progress_image, null);
            imageLayout.setLayoutParams(getLayoutParam());
            if (container.getBackground()!=null)container.getBackground().setAlpha(255);
        } else {
            imageLayout = cacheImageList.remove(0);
            imageLayout.getPhotoView().setImageBitmap(null);
        }

        Log.e("instantiateItem: ", "实例化PhotoView");
        GlideImageLoader.load(imageLayout, picUrlList.get(position));
        container.addView(imageLayout);
        return imageLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ProgressImageLayout imageLayout = (ProgressImageLayout) object;
        container.removeView(imageLayout);
        cacheImageList.add(imageLayout);
    }

    public ViewPager.LayoutParams getLayoutParam() {
        ViewPager.LayoutParams lp = new ViewPager.LayoutParams();
        lp.width = ViewPager.LayoutParams.MATCH_PARENT;
        lp.height = ViewPager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        return lp;
    }

    class LastItemRecord{
        public int position;
        public ProgressImageLayout imageLayout;
    }
}
