package com.hjq.toast;

/**
 * Author: Dylan
 * Date: 2021/03/10
 * Desc:
 */
public class ToastMsg {
    private String text;
    private int duration;

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
}
