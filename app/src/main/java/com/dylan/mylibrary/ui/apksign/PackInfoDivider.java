package com.dylan.mylibrary.ui.apksign;

import android.content.pm.PackageInfo;

/**
 * Created by Dylan on 2017/12/9.
 */

public class PackInfoDivider extends PackageInfo {
    public  boolean isApp;
    public PackInfoDivider(boolean isApp){
       this.isApp=isApp;
    }
}
