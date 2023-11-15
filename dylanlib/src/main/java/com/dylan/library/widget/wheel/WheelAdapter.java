package com.dylan.library.widget.wheel;

public interface WheelAdapter<T> {
    int getItemsCount();

    T getItem(int var1);

    int indexOf(T var1);
}
