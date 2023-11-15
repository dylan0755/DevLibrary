//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.dylan.library.widget.wheel;

import android.os.Handler;
import android.os.Message;

 class MessageHandler extends Handler {
    public static final int WHAT_INVALIDATE_LOOP_VIEW = 1000;
    public static final int WHAT_SMOOTH_SCROLL = 2000;
    public static final int WHAT_ITEM_SELECTED = 3000;
    final WheelView loopview;

    MessageHandler(WheelView loopview) {
        this.loopview = loopview;
    }

    public final void handleMessage(Message msg) {
        switch(msg.what) {
            case 1000:
                this.loopview.invalidate();
                break;
            case 2000:
                this.loopview.smoothScroll(WheelView.ACTION.FLING);
                break;
            case 3000:
                this.loopview.onItemSelected();
        }

    }
}
