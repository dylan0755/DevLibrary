package com.dylan.library.utils;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2020/2/24
 * Desc:
 */
public class ArrayUtils {
   private static final String TAG=ArrayUtils.class.getSimpleName();

   public static String getStringByArray(Object o){
       if (o==null|| !o .getClass().isArray())return "invalid Array";
       StringBuilder builder=new StringBuilder();
       builder.append("[");
       int len= Array.getLength(o);
       for (int i=0;i<len;i++){
           builder.append(Array.get(o,i));
           if (i!=len-1)builder.append(",");
       }
       builder.append("]");
       return builder.toString();
   }
    public static void printArray(Object o){
       String arrayString=getStringByArray(o);
        Log.e(TAG, arrayString );
    }

    public static void printArray(String tag,Object o){
        String arrayString=getStringByArray(o);
        Log.e(tag, arrayString );
    }


    public static List<Object> asList(Object o){
       //Arrays.asList()
       if (o!=null&&o.getClass().isArray()){
           int len= Array.getLength(o);
           List<Object> list=new ArrayList<>();
           for (int i=0;i<len;i++){
               list.add(Array.get(o,i));
           }
           return list;
       }else{
           return null;
       }
    }

    public static int findMax(int[] array) {
        int max = array[0];
        for (int value : array) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    /**
     * 从数组中随即抽出若干项非重复的元素
     */
    public static List<Integer> getRandomDistinctElementsFromArray(int[] srcArrays, int elementCount) {
        return  RandomUtils.getRandomDistinctElementsFromArray(srcArrays,elementCount);
    }

    public static <T> List<T> getRandomDistinctElementsFromList(List<T> list, int elementCount) {
        return  RandomUtils.getRandomDistinctElementsFromList(list,elementCount);
    }
}
