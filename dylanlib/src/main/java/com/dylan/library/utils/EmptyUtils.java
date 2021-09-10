package com.dylan.library.utils;

import android.os.Build;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * Created by Dylan on 2016/12/29.
 */

public class EmptyUtils {

    private EmptyUtils() {

    }

    public static boolean isEmpty(Object object) {
        if (object == null) {
            return true;
        }


        if (object instanceof String && ((String) object).isEmpty()) return true;
        else if (object instanceof CharSequence&&((CharSequence)object).length()==0)return true;
        else if (object.getClass().isArray() && Array.getLength(object) == 0) return true;
        else if (object instanceof Map && ((Map) object).isEmpty()) return true;
            //ArrayList,LinkList,HashSet即其父类
        else if (object instanceof Collection && ((Collection) object).isEmpty()) return true;

        else if (object instanceof SparseArray && ((SparseArray) object).size() == 0) return true;

        else if (object instanceof SparseBooleanArray && ((SparseBooleanArray) object).size() == 0)
            return true;

        else if (object instanceof SparseIntArray && ((SparseIntArray) object).size() == 0)
            return true;
        //TextView ,EditText
        else if (object instanceof TextView && ((TextView) object).getText().toString().isEmpty())
            return true;

        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (object instanceof SparseLongArray && ((SparseLongArray) object).size() == 0)
                return true;
        }


        return false;

    }


    public static boolean isNotEmpty(Object object) {
        return !isEmpty(object);
    }


    public static boolean isNotBlank(String str) {
        return str != null && str.length() > 0 && str.trim().length() > 0;
    }
}
