package com.dankal.mylibrary.ui.lazyload;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.dankal.mylibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 2018/2/8.
 */

public class LazyFragmentActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lazyfragment);
        mViewPager= (ViewPager) findViewById(R.id.vpager);
        mViewPager.setOffscreenPageLimit(4);
        initFragment();
    }

     private void initFragment(){
         List<Fragment> list=new ArrayList<>();
       for (int i=0;i<8;i++){
           ClassFragment fragment=new ClassFragment();
           Bundle bundle=new Bundle();
           bundle.putInt("pageIndex",(i+1));
           fragment.setArguments(bundle);
           list.add(fragment);
       }
         FragmentAdapter adapter=new FragmentAdapter(getSupportFragmentManager(),list);
         mViewPager.setAdapter(adapter);
     }



     class FragmentAdapter extends FragmentPagerAdapter{
         private List<Fragment> dataList;

         public FragmentAdapter(FragmentManager fm,List<Fragment> fragmentList) {
             super(fm);
             dataList=fragmentList;
         }

         @Override
         public Fragment getItem(int position) {
             return dataList!=null?dataList.get(position):null;
         }

         @Override
         public int getCount() {
             return dataList!=null?dataList.size():0;
         }
     }


}
