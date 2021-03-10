package com.dylan.library.callback;

import java.util.List;

/**
 * Author: Dylan
 * Date: 2020/4/23
 * Desc:
 */
public interface IRecyclerAdapterDataBinder {
    void hookBind(List list);
    void hookAddAllAndNotifyDataChanged(List list);
    int hookGetItemCount();
    void hookClear();
    boolean isEmpty();

}
