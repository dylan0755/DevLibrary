package com.dylan.library.utils;

import android.app.Application;

import com.dylan.library.manager.AppSpManager;
import com.hjq.toast.Toaster;

/**
 * Author: Dylan
 * Date: 2020/4/20
 * Desc:
 */
public class AndKit {
    public static void init(Application application){
        Toaster.init(application);
        CompatUtils.initContext(application);
        AppSpManager.init(application);
    }
}
