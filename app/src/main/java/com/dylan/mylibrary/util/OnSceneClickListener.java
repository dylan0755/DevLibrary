package com.dylan.mylibrary.util;

import com.dylan.mylibrary.bean.Chicken;
import com.dylan.mylibrary.bean.Fish;
import com.dylan.mylibrary.bean.Plaint;

/**
 * Author: Administrator
 * Date: 2020/10/13
 * Desc: 场景点击接口
 */
public interface OnSceneClickListener {
    void onClickMarket();//点击批发市场

    void onClickShop();//点击商店

    void onClickWareHouse();//点击仓库

    void onClickChickenZone();//点击鸡圈

    void onClickChicken(Chicken chicken);//点击某只鸡

    void onClickFishPond();//点击鱼塘

    void onClickFish(Fish fish);//几点某只鱼

    void onClickPlaint(Plaint plaint);//点击植物或占位图
}
