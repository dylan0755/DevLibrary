package com.dankal.mylibrary.bean;

/**
 * Created by Dylan on 2016/10/12.
 */
public class QiNiuDomain {
    private String AccessKey;

    private String SecretKey;

    private String BucketName;

    @Override
    public String toString() {
        return "QiNiuDomain{" +
                "AccessKey='" + AccessKey + '\'' +
                ", SecretKey='" + SecretKey + '\'' +
                ", BucketName='" + BucketName + '\'' +
                ", BucketDomain='" + BucketDomain + '\'' +
                '}';
    }

    private  String BucketDomain;

    public void setAccessKey(String AccessKey){
        this.AccessKey = AccessKey;
    }
    public String getAccessKey(){
        return this.AccessKey;
    }
    public void setSecretKey(String SecretKey){
        this.SecretKey = SecretKey;
    }
    public String getSecretKey(){
        return this.SecretKey;
    }
    public void setBucketName(String BucketName){
        this.BucketName = BucketName;
    }
    public String getBucketName(){
        return this.BucketName;
    }
    public  void setBucketDomain(String BucketDomain){
        this.BucketDomain = BucketDomain;
    }
    public String getBucketDomain(){
        return this.BucketDomain;
    }
}
