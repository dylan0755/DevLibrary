//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.dylan.library.widget.wheel;

import java.util.TimerTask;

 class InertiaTimerTask extends TimerTask {
    float a;
    final float velocityY;
    final WheelView loopView;

    InertiaTimerTask(WheelView loopview, float velocityY) {
        this.loopView = loopview;
        this.velocityY = velocityY;
        this.a = 2.14748365E9F;
    }

    public final void run() {
        if (this.a == 2.14748365E9F) {
            if (Math.abs(this.velocityY) > 2000.0F) {
                if (this.velocityY > 0.0F) {
                    this.a = 2000.0F;
                } else {
                    this.a = -2000.0F;
                }
            } else {
                this.a = this.velocityY;
            }
        }

        if (Math.abs(this.a) >= 0.0F && Math.abs(this.a) <= 20.0F) {
            this.loopView.cancelFuture();
            this.loopView.handler.sendEmptyMessage(2000);
        } else {
            int i = (int)(this.a * 10.0F / 1000.0F);
            this.loopView.totalScrollY -= (float)i;
            if (!this.loopView.isLoop) {
                float itemHeight = this.loopView.itemHeight;
                float top = (float)(-this.loopView.initPosition) * itemHeight;
                float bottom = (float)(this.loopView.getItemsCount() - 1 - this.loopView.initPosition) * itemHeight;
                if ((double)this.loopView.totalScrollY - (double)itemHeight * 0.25D < (double)top) {
                    top = this.loopView.totalScrollY + (float)i;
                } else if ((double)this.loopView.totalScrollY + (double)itemHeight * 0.25D > (double)bottom) {
                    bottom = this.loopView.totalScrollY + (float)i;
                }

                if (this.loopView.totalScrollY <= top) {
                    this.a = 40.0F;
                    this.loopView.totalScrollY = (float)((int)top);
                } else if (this.loopView.totalScrollY >= bottom) {
                    this.loopView.totalScrollY = (float)((int)bottom);
                    this.a = -40.0F;
                }
            }

            if (this.a < 0.0F) {
                this.a += 20.0F;
            } else {
                this.a -= 20.0F;
            }

            this.loopView.handler.sendEmptyMessage(1000);
        }
    }
}
