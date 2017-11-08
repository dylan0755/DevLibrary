package com.dylan.library.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.dylan.library.R;
import com.dylan.library.screen.ScreenUtils;

/**
 * Created by Dylan on 2017/11/8.
 */

public abstract class SlideFromBottomDialog extends DLTransParentDialog{

    public SlideFromBottomDialog(Context context) {
        super(context);
        Window window=getWindow();
        WindowManager.LayoutParams lp= window.getAttributes();
        lp.width= ScreenUtils.getScreenWidth(context);
        lp.height= WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity= Gravity.BOTTOM;
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.anim_slie_from_bottom);
    }

}
