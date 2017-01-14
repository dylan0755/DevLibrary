package com.dylan.library.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 2017/1/14.
 */

public  class GridViewPager extends ViewPager {

    public GridViewPager(Context context) {
        super(context);
    }

    public GridViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 指定列数和每页的个数，将集合中的数据分成若干个页面。
     * @param list
     * @param column
     * @param countPerPage
     * @param <T>
     */
    public <T> void setDataList(List<T> list, int column, int countPerPage) {
        if (column == 0) return;
        if (countPerPage == 0) return;
        if (list == null || list.size() == 0) return;
        int pageCount = list.size() / countPerPage;  //计算整页数据的有几页。
        int remain = list.size() % countPerPage;               //剩余不够一页的作为1页
        if (remain != 0) pageCount += 1;

        List<GridView> gridViews = new ArrayList<>();
        for (int i = 0; i < pageCount; i++) {
            List<T> subList;
            if (pageCount == 1) {//只有一页的情况
                subList = list;
            } else {//多页
                if (i != 0 && i == pageCount - 1) {//多页且是最后一页
                    subList = new ArrayList<>();
                    for (int k = i * countPerPage; k < list.size(); k++) {
                        subList.add(list.get(k));
                    }
                } else {//第1页到  倒数第2页
                    subList = new ArrayList<>();
                    if (i == 0) {//第1页前面的单位个数
                        for (int j = 0; j < countPerPage; j++) {
                            subList.add(list.get(j));
                        }
                    } else {
                        int start = i * countPerPage;
                        int end = start + countPerPage;
                        for (int k = start; k < end; k++) {
                            subList.add(list.get(k));
                        }
                    }
                }
            }
            GridView gridView = new GridView(getContext());
            gridView.setNumColumns(column);
            if (mListener!=null)mListener.attachAdapter(gridView,subList);
            gridViews.add(gridView);
        }
        GridViewPageAdapter adapter = new GridViewPageAdapter();
        adapter.setGridViewList(gridViews);
        setAdapter(adapter);
    }


    static class GridViewPageAdapter extends PagerAdapter {
        private List<GridView> gridViewList;

        public GridViewPageAdapter() {

        }

        public void setGridViewList(List<GridView> gridViewList) {
            this.gridViewList = gridViewList;
        }

        @Override
        public int getCount() {
            return (gridViewList == null || gridViewList.size() == 0) ? 0 : gridViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            GridView gridView = gridViewList.get(position);
            container.addView(gridView);
            return gridView;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            GridView gridView = gridViewList.get(position);
            container.removeView(gridView);
        }
    }



    private AttachAdapterListener mListener;
    public interface AttachAdapterListener{
        void attachAdapter(GridView gridView, List datasPerPage);//每页数据
    }

    public  void setAttachAdapterListener(AttachAdapterListener listener){
        mListener=listener;
    }
}
