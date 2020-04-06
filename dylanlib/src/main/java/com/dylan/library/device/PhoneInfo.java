package com.dylan.library.device;

/**
 * Created by Dylan on 2017/1/2.
 */

public class PhoneInfo {
    private String phoneNumber;
    private String providerName;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }



    @Override
    public String toString() {
        return "PhoneInfo{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", providerName='" + providerName + '\'' +
                '}';
    }
}
