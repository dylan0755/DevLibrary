package com.dylan.library.widget;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.Touch;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Author: Dylan
 * Date: 2019/9/25
 * Desc:
 */
public class ClickableLinkMovementMethod  extends LinkMovementMethod {
    private static ClickableLinkMovementMethod sInstance;

    public static ClickableLinkMovementMethod getInstance() {
        if (sInstance == null)
            sInstance = new ClickableLinkMovementMethod();
        return sInstance;
    }

    public boolean onTouchEvent(TextView widget, Spannable buffer,
                               MotionEvent event ) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    link[0].onClick(widget);
                } else if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(link[0]),
                            buffer.getSpanEnd(link[0]));
                }

                if (widget instanceof ClickableSpanTextView){
                    ((ClickableSpanTextView) widget).linkHit = true;
                }
                return true;
            } else {
                Selection.removeSelection(buffer);
                Touch.onTouchEvent(widget, buffer, event);
                return false;
            }
        }
        return Touch.onTouchEvent(widget, buffer, event);
    }


}
