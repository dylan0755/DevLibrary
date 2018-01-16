package com.wxhkj.weixiuhui.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * Created by Dylan on 2018/1/16.
 */

public class ChannelUtils {
        public static String getChannelName(Context context) {
            if (context == null) {
                return null;
            }
            String channelName = null;
            try {
                PackageManager packageManager = context.getPackageManager();
                if (packageManager != null) {
                    //注意此处为ApplicationInfo，因为友盟设置的meta-data是在application标签中
                    ApplicationInfo applicationInfo = packageManager.
                            getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                    if (applicationInfo != null) {
                        if (applicationInfo.metaData != null) {
                            //这里的UMENG_CHANNEL要与manifest中的配置文件标识一致
                            channelName = String.valueOf(applicationInfo.metaData.get("UMENG_CHANNEL"));
                        }
                    }

                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return channelName;
    }
}
