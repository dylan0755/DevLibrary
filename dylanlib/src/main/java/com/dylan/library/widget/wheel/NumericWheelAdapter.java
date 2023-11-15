package com.dylan.library.widget.wheel;


public class NumericWheelAdapter implements WheelAdapter {
    public static final int DEFAULT_MAX_VALUE = 9;
    private static final int DEFAULT_MIN_VALUE = 0;
    private int minValue;
    private int maxValue;

    public NumericWheelAdapter() {
        this(0, 9);
    }

    public NumericWheelAdapter(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public Object getItem(int index) {
        if (index >= 0 && index < this.getItemsCount()) {
            int value = this.minValue + index;
            return value;
        } else {
            return 0;
        }
    }

    public int getItemsCount() {
        return this.maxValue - this.minValue + 1;
    }

    public int indexOf(Object o) {
        try {
            return (Integer)o - this.minValue;
        } catch (Exception var3) {
            return -1;
        }
    }
}

