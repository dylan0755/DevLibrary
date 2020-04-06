package com.dylan.mylibrary.bean;

/**
 * Created by Dylan on 2016/9/23.
 */

/**
 * 首页和VR原创页面的视频封装类
 */
public class VideoData {
    private int video_id;

    private int user_id;

    private String name;

    private String img_key;

    private String introduction;

    private int play_num;

    private String mode;

    public void setVideo_id(int video_id){
        this.video_id = video_id;
    }
    public int getVideo_id(){
        return this.video_id;
    }
    public void setUser_id(int user_id){
        this.user_id = user_id;
    }
    public int getUser_id(){
        return this.user_id;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setImg_key(String img_key){
        this.img_key = img_key;
    }
    public String getImg_key(){
        return this.img_key;
    }
    public void setIntroduction(String introduction){
        this.introduction = introduction;
    }
    public String getIntroduction(){
        return this.introduction;
    }
    public void setPlay_num(int play_num){
        this.play_num = play_num;
    }



    public int getPlay_num(){
        return this.play_num;
    }
    public void setMode(String mode){
        this.mode = mode;
    }
    public String getMode(){
        return this.mode;
    }


    @Override
    public String toString() {
        return "VideoData{" +
                "video_id=" + video_id +
                ", user_id=" + user_id +
                ", name='" + name + '\'' +
                ", img_key='" + img_key + '\'' +
                ", introduction='" + introduction + '\'' +
                ", play_num=" + play_num +
                ", mode='" + mode + '\'' +
                '}';
    }
}
