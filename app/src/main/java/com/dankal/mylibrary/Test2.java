package com.dankal.mylibrary;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dankal.mylibrary.bean.CarouselData;
import com.dankal.mylibrary.bean.NavigateTag;
import com.dankal.mylibrary.bean.Tag;
import com.dankal.mylibrary.domain.RestApi;
import com.dankal.mylibrary.ui.OriginalSubFragment;
import com.dankal.mylibrary.util.IResponBodyImpl;
import com.dankal.mylibrary.util.ResponseBodyParser;
import com.dylan.library.tab.TabIndicatorLayout;
import com.dylan.library.widget.CarouselView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Dylan on 2016/11/10.
 */

public class Test2 extends Fragment {
    private View contentView;
    protected TabIndicatorLayout mTabLayout;
    protected CarouselView mCarousel;//轮播
    private ViewPager mViewPager;
    private BasePageAdapter adapter;
    private RestApi mRestApi;
    protected String TAG;
    List<Fragment> fragmentList = new ArrayList<Fragment>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.original, container, false);
        mRestApi = RestApi.Factory.getInstance(RestApi.Factory.STRING_CONVERTER);
        initView();
        return contentView;
    }


    protected void initView() {
        mCarousel = (CarouselView) findViewById(R.id.cv_top);
        mTabLayout = (TabIndicatorLayout) findViewById(R.id.tl_common_top);
        mViewPager = (ViewPager) findViewById(R.id.vp_containner);
        initTab();
        loadCarousel("vrOriginal");
        loadBarData();
    }


    private void initTab() {
        int selectColor = Color.parseColor("#57ccbc");
        int normalColor = Color.parseColor("#a0a0a0");
        mTabLayout.setTabTextSize(16);
        mTabLayout.setTabColor(normalColor);
        mTabLayout.setTabSelectColor(selectColor);
        mTabLayout.setIndicatorColor(selectColor);
        mTabLayout.setMaxVisiableCount(6);
        mTabLayout.setIndicatorShap(TabIndicatorLayout.SHAPE_TRIANGLE);
    }

    public View findViewById(int resId) {
        return contentView.findViewById(resId);
    }


    private void loadCarousel(final String suffix) {
        Call<String> call = mRestApi.loadVRCarousel();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String jsonData=response.body();
                try {
                    jsonData = new JSONObject(jsonData).getString(suffix);
                    Gson gson=new Gson();
                    final List<CarouselData> list = gson.fromJson(jsonData, new TypeToken<List<CarouselData>>() { }.getType());
                    setCarouselData(list);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {

            }
        });

    }



    /**
     * 加载导航栏
     */
    private void loadBarData() {
        Call<String> call = mRestApi.loadVRNavigateBarData();
        ResponseBodyParser.parse(call, new IResponBodyImpl() {
            @Override
            public void onSucess(String jsonData, Gson gson) {
                NavigateTag mtag = gson.fromJson(jsonData, NavigateTag.class);
                if (mtag == null) return;
                List<Tag> list = mtag.getTag();
                initTagAndFragment(list);

            }
        });
    }


    private void initTagAndFragment(List<Tag> list) {
        if (fragmentList.size() == 0) {
            if (list != null && list.size() > 0) {
                List<String> titleList = new ArrayList<String>();
                for (Tag tag : list) {
                    String name = tag.getName();
                    int tag_id = tag.getTag_id();
                    titleList.add(name);
                    OriginalSubFragment fragment = new OriginalSubFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(OriginalSubFragment.TAG_ID, tag_id);
                    fragment.setArguments(bundle);
                    fragmentList.add(fragment);
                }
                //添加推荐
                titleList.add(0, "推荐");
                OriginalSubFragment fragment = new OriginalSubFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean(OriginalSubFragment.RECORMMEND, true);
                fragment.setArguments(bundle);
                fragmentList.add(0, fragment);
                initTitles(titleList);
                initFragment(fragmentList);
            } else {
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < fragmentList.size(); i++){
                        OriginalSubFragment originalSubFragment=(OriginalSubFragment)fragmentList.get(i);
                        Tag tag=list.get(i);
                        if (tag!=null){
                            int tagid=list.get(i).getTag_id();
                            originalSubFragment.reLoad(tagid);
                        }

                    }
                }
            }


        }
    }


    protected void initTitles(List<String> list){
        if (list==null||list.size()==0)return;
        mTabLayout.addTabs(list);
        if (list.size()>2){
            mViewPager.setOffscreenPageLimit(list.size()-1);
        }
        mTabLayout.setUpWidthViewPager(mViewPager);
    }




    /**
     * 实例化fragments
     */
    public   void initFragment(List<Fragment> list){
        List<Fragment> fragList =list;
        if (fragList==null||fragList.size()==0)return;
        initAdapter(fragList);

    }




    private void initAdapter(List<Fragment> fragList){
        if (adapter==null){
            adapter=new BasePageAdapter(getChildFragmentManager(), fragList);
            mViewPager.setAdapter(adapter);
        }
    }



















    class BasePageAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList;

        public BasePageAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            fragmentList = list;
        }


        public void update(List<Fragment> list) {
            fragmentList.clear();
            fragmentList.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
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



    private void setCarouselData(final List<CarouselData> list) {
        if (list == null || list.size() == 0) return;

        List<String> urlList = new ArrayList<String>();
        for (CarouselData data : list) {
            urlList.add(MyApplication.setURL(data.getImg_url()));
        }
        mCarousel.setCarouselResource(urlList, ImageView.ScaleType.FIT_XY);  //图片url，ImageView的缩放形式
        mCarousel.setOnItemClickListener(new CarouselView.OnItemClickListener() {
            @Override
            public void onClick(String url, int position) {

            }
        });
    }
}
