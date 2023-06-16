package com.dylan.library.widget.irecycler;

import android.content.Context;
import android.util.DisplayMetrics;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

public class SmoothScrollLayoutManager extends LinearLayoutManager {
    private float speed=800*1.5f;
    public SmoothScrollLayoutManager(Context context) {
        super(context);
    }

    public void smoothScrollToPosition(RecyclerView recyclerView,int position,float speed){
        this.speed=speed;
        smoothScrollToPosition( recyclerView, new RecyclerView.State(),position);
    }
    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        RecyclerView.SmoothScroller smoothScroller = new InnerSmoothScroller(recyclerView.getContext(),speed);
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }
    private  static class InnerSmoothScroller extends LinearSmoothScroller {
        private float speed;
        public InnerSmoothScroller(Context context,float speed) {
            super(context);
            this.speed=speed;
        }
        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2);
        }
        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            return speed/ displayMetrics.densityDpi;
        }
    }
}
