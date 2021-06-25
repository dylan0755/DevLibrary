package com.dylan.library.bean;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.io.Serializable;

/**
 * Author: Administrator
 * Date: 2020/6/23
 * Desc:
 */
public class BundleBuilder {




    public static Bundle putInt(@Nullable String key, int value) {
        Bundle bundle = new Bundle();
        bundle.putInt(key, value);
        return bundle;
    }

    public static Bundle putBoolean(@Nullable String key, boolean value) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(key, value);
        return bundle;
    }

    public static Bundle putString(@Nullable String key, String value) {
        Bundle bundle = new Bundle();
        bundle.putString(key, value);
        return bundle;
    }

    public static Bundle putSerializable(@Nullable String key, Serializable value) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(key, value);
        return bundle;
    }


    public static Bundle putDouble(@Nullable String key, double value) {
        Bundle bundle = new Bundle();
        bundle.putDouble(key, value);
        return bundle;
    }



    public static class Builder {
        private Bundle bundle;

        public Builder() {
            bundle = new Bundle();
        }


        public Builder putInt(@Nullable String key, int value) {
            bundle.putInt(key, value);
            return this;
        }

        public Builder putBoolean(@Nullable String key, boolean value) {
            bundle.putBoolean(key, value);
            return this;
        }

        public Builder putString(@Nullable String key, String value) {
            bundle.putString(key, value);
            return this;
        }

        public Builder putSerializable(@Nullable String key, Serializable value) {
            bundle.putSerializable(key, value);
            return this;
        }


        public Builder putDouble(@Nullable String key, double value) {
            bundle.putDouble(key, value);
            return this;
        }

        public Bundle build(){
            return bundle;
        }


    }


}
