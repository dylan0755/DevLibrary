package com.dylan.mylibrary.bean;

/**
 * Created by Dylan on 2016/9/30.
 */
public class CarouselData {
    private int video_id;

    public int getVideo_id() {
        return video_id;
    }

    public void setVideo_id(int video_id) {
        this.video_id = video_id;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    private String img_url;
}
