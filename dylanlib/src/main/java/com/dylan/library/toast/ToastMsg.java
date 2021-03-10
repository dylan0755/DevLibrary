package com.dylan.library.toast;

/**
 * Author: Dylan
 * Date: 2021/03/10
 * Desc:
 */
public class ToastMsg {
    private String text;
    private int duration;
    private int gravity;
    private int offsetX;
    private int offsetY;

    public String getText() {
        if (text ==null) text ="";
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }


    public int getGravity() {
        return gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }


    public void setGravity(int gravity,int offsetX,int offsetY){
         setGravity(gravity);
         setOffsetX(offsetX);
         setOffsetY(offsetY);
    }
}
