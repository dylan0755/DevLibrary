package com.dylan.mylibrary.bean;

/**
 * Created by Dylan on 2016/9/27.
 */
public class Tag {
    private int tag_id;

    @Override
    public String toString() {
        return "Tag{" +
                "tag_id=" + tag_id +
                ", name='" + name + '\'' +
                '}';
    }

    private String name;

    public void setTag_id(int tag_id){
        this.tag_id = tag_id;
    }
    public int getTag_id(){
        return this.tag_id;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }

}
