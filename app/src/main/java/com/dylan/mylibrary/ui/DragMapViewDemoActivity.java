package com.dylan.mylibrary.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dylan.library.screen.ScreenUtils;
import com.dylan.library.utils.ToastUtils;
import com.dylan.mylibrary.R;
import com.dylan.mylibrary.bean.Chicken;
import com.dylan.mylibrary.bean.Fish;
import com.dylan.mylibrary.bean.Plaint;
import com.dylan.mylibrary.util.SimpleSceneClickListener;
import com.dylan.mylibrary.widget.FarmMapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Administrator
 * Date: 2020/10/15
 * Desc:
 */
public class DragMapViewDemoActivity extends AppCompatActivity {

    FarmMapView farmMapView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dragmapview);
        ScreenUtils.removeStatuBar(this);
        farmMapView=findViewById(R.id.dragView);


        try {
            farmMapView.setMapDataInputStream(getAssets().open("scene-empty.webp"));
            farmMapView.setOnSceneClickListener(new MapSceneClickListener());
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Chicken> chickens = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            chickens.add(new Chicken());
        }

        List<Fish> fishes=new ArrayList<>();
        for (int i = 0; i <3; i++) {
            fishes.add(new Fish());
        }
        farmMapView.updateChickenAndFishes(chickens,fishes);

    }


    /**
     * 场景点击监听
     */
    class MapSceneClickListener extends SimpleSceneClickListener {

        //点击批发市场
        @Override
        public void onClickMarket() {
            ToastUtils.show("点击批发市场");
        }

        //点击商店
        @Override
        public void onClickShop() {
            ToastUtils.show("点击商店");
        }

        //点击仓库
        @Override
        public void onClickWareHouse() {
            ToastUtils.show("点击仓库");
        }

        //点击鱼塘
        @Override
        public void onClickFishPond() {
            ToastUtils.show("点击鱼塘");
        }

        //点击鸡圈
        @Override
        public void onClickChickenZone() {
            ToastUtils.show("点击鸡圈");
        }

        //点击树桩占位图或者果树
        @Override
        public void onClickPlaint(Plaint plaint) {

        }

        //点击小鸡
        @Override
        public void onClickChicken(Chicken chicken) {
            ToastUtils.show("点击了小鸡");
        }


        @Override
        public void onClickFish(Fish fish) {
            ToastUtils.show("点击了鱼");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        farmMapView.destroy();
    }
}
