package com.dylan.mylibrary.bean;

/**
 * Author: Administrator
 * Date: 2020/10/13
 * Desc:
 */
public class Fish {

    private int health;//健康值
    private int nutrient;//养分
    private int oxygen;//氧气需求度

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

    public int getOxygen() {
        return oxygen;
    }

    public void setOxygen(int oxygen) {
        this.oxygen = oxygen;
    }
}
