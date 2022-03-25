package com.dylan.library.widget.banner;

import android.content.Context;
import android.view.View;

/**
 * Author: Dylan
 * Date: 2022/03/25
 * Desc:
 */
public interface HolderCreator {
    View createView(Context context, int index, Object o);
}