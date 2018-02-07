package com.dankal.mylibrary.bean;

/**
 * Created by Dylan on 2018/2/6.
 */

public class UserBean  {
    private String name;

    public UserBean(String name){
        this.name=name;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
