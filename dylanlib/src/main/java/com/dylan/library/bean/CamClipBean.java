package com.dylan.library.bean;

public class CamClipBean {
    public String camId;
    private float leftRatio=-1f;
    private float topRatio=-1f;
    private float rightRatio=-1f;
    private float bottomRatio=-1f;

    public CamClipBean(String camId){
      this.camId=camId;
    }

    public CamClipBean(String camId, float leftRatio, float topRatio, float rightRatio, float bottomRatio) {
        this.camId=camId;
        this.leftRatio = leftRatio;
        this.topRatio = topRatio;
        this.rightRatio = rightRatio;
        this.bottomRatio = bottomRatio;
    }

    public float getLeftRatio() {
        return leftRatio;
    }

    public float getTopRatio() {
        return topRatio;
    }

    public float getRightRatio() {
        return rightRatio;
    }

    public float getBottomRatio() {
        return bottomRatio;
    }
}
