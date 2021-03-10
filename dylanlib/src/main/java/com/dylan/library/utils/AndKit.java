package com.dylan.library.utils;

import android.app.Application;

import com.dylan.library.manager.AppSpManager;

/**
 * Author: Dylan
 * Date: 2020/4/20
 * Desc:
 */
public class AndKit {
    public static void init(Application application){
        ToastUtils.initToast(application);
        HJQToastUtils.init(application);
        CompatUtils.initContext(application);
        AppSpManager.init(application);
    }
}
