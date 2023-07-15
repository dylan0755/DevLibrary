//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.dylan.library.widget.wheel;

import java.util.TimerTask;

final class SmoothScrollTimerTask extends TimerTask {
    int realTotalOffset;
    int realOffset;
    int offset;
    final WheelView loopView;

    SmoothScrollTimerTask(WheelView loopview, int offset) {
        this.loopView = loopview;
        this.offset = offset;
        this.realTotalOffset = 2147483647;
        this.realOffset = 0;
    }

    public final void run() {
        if (this.realTotalOffset == 2147483647) {
            this.realTotalOffset = this.offset;
        }

        this.realOffset = (int)((float)this.realTotalOffset * 0.1F);
        if (this.realOffset == 0) {
            if (this.realTotalOffset < 0) {
                this.realOffset = -1;
            } else {
                this.realOffset = 1;
            }
        }

        if (Math.abs(this.realTotalOffset) <= 1) {
            this.loopView.cancelFuture();
            this.loopView.handler.sendEmptyMessage(3000);
        } else {
            this.loopView.totalScrollY += (float)this.realOffset;
            if (!this.loopView.isLoop) {
                float itemHeight = this.loopView.itemHeight;
                float top = (float)(-this.loopView.initPosition) * itemHeight;
                float bottom = (float)(this.loopView.getItemsCount() - 1 - this.loopView.initPosition) * itemHeight;
                if (this.loopView.totalScrollY <= top || this.loopView.totalScrollY >= bottom) {
                    this.loopView.totalScrollY -= (float)this.realOffset;
                    this.loopView.cancelFuture();
                    this.loopView.handler.sendEmptyMessage(3000);
                    return;
                }
            }

            this.loopView.handler.sendEmptyMessage(1000);
            this.realTotalOffset -= this.realOffset;
        }

    }
}
