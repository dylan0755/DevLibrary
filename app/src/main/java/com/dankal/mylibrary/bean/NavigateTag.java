package com.dankal.mylibrary.bean;

import java.util.List;

/**
 * Created by Dylan on 2016/9/27.
 */
public class NavigateTag {
    private List<Tag> tag ;

    public void setTag(List<Tag> tag){
        this.tag = tag;
    }
    public List<Tag> getTag(){
        return this.tag;
    }
}
