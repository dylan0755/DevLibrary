package com.dylan.mylibrary.bean;

import android.graphics.Point;

/**
 * Author: Administrator
 * Date: 2020/10/13
 * Desc: 植物
 */
public class Plaint {
    private int bitmapType;//app本地声明
    public static final int TYPE_PLACE_HOLDER = 0;//占位图
    public static final int TYPE_PLACE_TREE = 1;//树

    public static final int STATUS_SPROUT = 0;//发芽
    public static final int STATUS_FLOWER = 1;//开花
    public static final int STATUS_FRUIT = 2;//结果

    private int no; //当前是第几棵树
    private int status = STATUS_SPROUT;
    private int fruitNumber = 1;//果实的数量
    private Point point; //当前的坐标点
    //农作物的健康值 、 养分(肥料) 和清洁度（虫害）
    private int health;//健康值
    private int nutrient;//养分
    private int cleaness;//清洁度


    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public int getBitmapType() {
        return bitmapType;
    }

    public void setBitmapType(int bitmapType) {
        this.bitmapType = bitmapType;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public int getFruitNumber() {
        return fruitNumber;
    }

    public void setFruitNumber(int fruitNumber) {
        this.fruitNumber = fruitNumber;
    }


    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getNutrient() {
        return nutrient;
    }

    public void setNutrient(int nutrient) {
        this.nutrient = nutrient;
    }

    public int getCleaness() {
        return cleaness;
    }

    public void setCleaness(int cleaness) {
        this.cleaness = cleaness;
    }
}
