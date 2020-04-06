package com.dylan.library.preferecen;

import android.content.SharedPreferences;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Dylan
 *
 * 支持基本数据类型，字符串的bean类
 */
public class PreferenceUtil {
    private static String type_string = "class java.lang.String";
    private static String type_int = "int";
    private static String type_double = "double";
    private static String type_long = "long";
    private static String type_float = "float";


    public static void saveBeanValue(SharedPreferences sp, Object model) {

        SharedPreferences.Editor editor = sp.edit();
        Field[] fields = model.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String name = field.getName();
            String upperName = name.substring(0, 1).toUpperCase() + name.substring(1);
            try {
                Method method = model.getClass().getDeclaredMethod("get" + upperName);
                String type = field.getGenericType().toString();
                if (type.equals(type_string)) {
                    String value = (String) method.invoke(model);
                    if (name.equals("token")){
                        Log.e("Sptoken",value);
                    }
                    editor.putString(name, value);
                } else if (type.equals(type_int)) {
                    Integer value = (Integer) method.invoke(model);
                    editor.putInt(name, value);
                } else if (type.equals(type_long)) {
                    Long value = (Long) method.invoke(model);
                    editor.putLong(name, value);
                    System.out.println(type + "  value " + value);
                } else if (type.equals(type_double)) {  //sharedpreference没有保存double类型，所以先保存String，获取的时候再转成double
                    Double bvalue = (Double) method.invoke(model);
                    String value = String.valueOf(bvalue);
                    editor.putString(name, value);
                } else if (type.equals(type_float)) {
                    Float value = (Float) method.invoke(model);
                    editor.putFloat(name, value);
                } else {
                    System.out.println(type);
                }

                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public static <T> T getBeanValue(SharedPreferences sp, Class<T>  classType) {


        Object model = null;
        try {
            model = classType.newInstance();
            Field[] fields = classType.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                String name = field.getName();
                String upperName = name.substring(0, 1).toUpperCase() + name.substring(1);
                try {
                    String type = field.getGenericType().toString();
                    if (type.equals(type_string)) {
                        Method method = classType.getMethod("set" + upperName, String.class);
                        String value = sp.getString(name, "");
                        method.invoke(model, value);
                    } else if (type.equals(type_int)) {
                        Method method = classType.getMethod("set" + upperName, int.class);
                        int value = sp.getInt(name, 0);
                        method.invoke(model, value);
                    } else if (type.equals(type_long)) {
                        Method method = classType.getMethod("set" + upperName, long.class);
                        Long value = sp.getLong(name, 0);
                        method.invoke(model, value);
                    } else if (type.equals(type_double)) {
                        Method method = classType.getMethod("set" + upperName, double.class);
                        String value = sp.getString(name, "");
                        if (value != null && !value.isEmpty()) {
                            double bvalue = Double.parseDouble(value);
                            method.invoke(model, bvalue);
                        }
                    } else if (type.equals(type_float)) {
                        Method method = classType.getMethod("set" + upperName, float.class);
                        Float value = sp.getFloat(name, 0);
                        method.invoke(model, value);
                    } else {
                        Log.e(type, "  value ==null");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return classType.cast(model);
    }





    public static void updateBean(SharedPreferences sp, Object oldBean, Object newBean) {
        if (oldBean.getClass() != newBean.getClass()) {
            throw new IllegalArgumentException("o1 and o2 belongs different classes");
        }
        SharedPreferences.Editor editor = sp.edit();
        Class classType = oldBean.getClass();
        Field[] fields = classType.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String name = field.getName();
            String upperName = name.substring(0, 1).toUpperCase() + name.substring(1);
            try {
                String type = field.getGenericType().toString();
                Method methodO2 = newBean.getClass().getMethod("get" + upperName);
                if (type.equals(type_string)) {
                    String value = (String) methodO2.invoke(newBean);
                    if (value!=null&&!value.isEmpty()){
                        Method methodO1 = classType.getMethod("set" + upperName, String.class);
                        methodO1.invoke(oldBean, value);
                        editor.putString(name,value);
                    }
                } else if (type.equals(type_int)) {
                    Integer value = (Integer) methodO2.invoke(newBean);
                    if (value!=0){
                        Method methodO1 = classType.getMethod("set" + upperName, int.class);
                        methodO1.invoke(oldBean, value);
                        editor.putInt(name,value);
                    }
                } else if (type.equals(type_long)) {
                    Long value = (Long) methodO2.invoke(newBean);
                    if (value!=0){
                        Method methodO1 = classType.getMethod("set" + upperName, long.class);
                        methodO1.invoke(oldBean, value);
                        editor.putLong(name,value);
                    }
                } else if (type.equals(type_double)) {
                    Double value = (Double) methodO2.invoke(newBean);
                    if (value!=0){
                        Method methodO1 = classType.getMethod("set" + upperName, double.class);
                        methodO1.invoke(oldBean, value);

                        String bvalue = String.valueOf(value);
                        editor.putString(name, bvalue);
                    }
                } else if (type.equals(type_float)) {
                    Float value = (Float) methodO2.invoke(newBean);
                    if (value!=0){
                        Method methodO1 = classType.getMethod("set" + upperName, float.class);
                        methodO1.invoke(oldBean, value);
                        editor.putFloat(name, value);
                    }
                }
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
