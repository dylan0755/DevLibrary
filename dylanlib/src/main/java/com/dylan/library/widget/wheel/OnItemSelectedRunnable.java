//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.dylan.library.widget.wheel;

 class OnItemSelectedRunnable implements Runnable {
    final WheelView loopView;

    OnItemSelectedRunnable(WheelView loopview) {
        this.loopView = loopview;
    }

    public final void run() {
        this.loopView.onItemSelectedListener.onItemSelected(this.loopView.getCurrentItem());
    }
}
