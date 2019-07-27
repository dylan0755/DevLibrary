package com.dylan.mylibrary.ui.tab;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dylan.mylibrary.R;
import com.dylan.library.fragment.FragmentSwitcher;
import com.dylan.library.tab.TabGroup;

public class TabActivity extends AppCompatActivity {
    private TabGroup  tabGroup;
    private  FragmentSwitcher fragmentSwitcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        initView();
        initFragment(savedInstanceState);
    }

    private void initView() {
        tabGroup = (TabGroup) findViewById(R.id.tg_main);
        tabGroup.setTabSelectColor(Color.parseColor("#57ccbc"));
        initTabData();
        tabGroup.setTabGroupSelectListener(new TabGroup.TabGroupSelectListener() {
            @Override
            public void select(int position) {
                fragmentSwitcher.select(position);
            }
        });

    }


    private void initFragment(Bundle savedInstanceState) {
        fragmentSwitcher = new FragmentSwitcher(getSupportFragmentManager(), R.id.fl_home_fragment_container);
        fragmentSwitcher.attachFragmentClass(Test1.class, Test2.class, Test3.class);
        if (savedInstanceState==null){
            fragmentSwitcher.select(0);
            tabGroup.setSelect(0);
        }else{
            int currentIndex=fragmentSwitcher.restoreFragments(savedInstanceState);
            tabGroup.setSelect(currentIndex);
        }
    }




    private void initTabData() {
        tabGroup.addItem("首页",R.mipmap.tab_ic_home_normal,R.mipmap.tab_ic_home_pressed,false);
        tabGroup.addItem("VR原创",R.mipmap.tab_ic_creative_normal, R.mipmap.tab_ic_original_pressed,true,true);
        tabGroup.addItem("我的",R.mipmap.tab_ic_personal_normal,R.mipmap.tab_ic_personal_pressed,false);
        tabGroup.setBadgeViewText(1,12);//超过3位数默认显示99+

    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (fragmentSwitcher != null) {
            fragmentSwitcher.saveFragmentInstanceSate(outState);
        }
        super.onSaveInstanceState(outState);
    }


}
