package com.dylan.mylibrary.ui;

/**
 * Author: Dylan
 * Date: 2019/9/23
 * Desc:
 */
public class GridDemoItem {
    private String itemName;
    private Class clzz;

    public GridDemoItem(String itemName,Class clzz){
        this.itemName=itemName;
        this.clzz=clzz;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Class getClzz() {
        return clzz;
    }

    public void setClzz(Class clzz) {
        this.clzz = clzz;
    }
}
