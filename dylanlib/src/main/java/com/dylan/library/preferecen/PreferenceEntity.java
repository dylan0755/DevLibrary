package com.dylan.library.preferecen;

import android.content.SharedPreferences;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Dylan
 */
public class PreferenceEntity {

    public static void saveBeanValue(SharedPreferences sp, Object model) {
        String type_string = "class java.lang.String";
        String type_int = "int";
        String type_double = "double";
        String type_long = "long";
        String type_float = "float";
        SharedPreferences.Editor editor = sp.edit();
        Field[] fields = model.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String name = field.getName();
            String upperName = name.substring(0, 1).toUpperCase() + name.substring(1);
            try {
                Method method = model.getClass().getMethod("get" + upperName);
                String type = field.getGenericType().toString();
                if (type.equals(type_string)) {
                    String value = (String) method.invoke(model);
                    editor.putString(name, value);
                } else if (type.equals(type_int)) {
                    Integer value = (Integer) method.invoke(model);
                    editor.putInt(name, value);
                } else if (type.equals(type_long)) {
                    Long value = (Long) method.invoke(model);
                    editor.putLong(name, value);
                    System.out.println(type + "  value " + value);
                } else if (type.equals(type_double)){  //sharedpreference没有保存double类型，所以先保存String，获取的时候再转成double
                    Double bvalue = (Double) method.invoke(model);
                    String value = String.valueOf(bvalue);
                    editor.putString(name, value);
                }else if (type.equals(type_float)) {
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


    public static <T> T getBeanValue(SharedPreferences sp, Class<T> classType) {
        String type_string = "class java.lang.String";
        String type_int = "int";
        String type_double = "double";
        String type_long = "long";
        String type_float = "float";

        Object model=null;
        try {
            model= classType.newInstance();
            Field[] fields = classType.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                String name = field.getName();
                String upperName = name.substring(0, 1).toUpperCase() + name.substring(1);
                try {
                    String type = field.getGenericType().toString();
                    if (type.equals(type_string)) {
                        Method method=classType.getMethod("set" + upperName,String.class);
                        String value = sp.getString(name, "");
                        method.invoke(model, value);
                    } else if (type.equals(type_int)) {
                        Method method=classType.getMethod("set" + upperName,int.class);
                        int value = sp.getInt(name, 0);
                        method.invoke(model, value);
                    } else if (type.equals(type_long)) {
                        Method method=classType.getMethod("set" + upperName,long.class);
                        Long value = sp.getLong(name, 0);
                        method.invoke(model, value);
                    }else if (type.equals(type_double)){
                        Method method=classType.getMethod("set" + upperName,double.class);
                        String value = sp.getString(name, "");
                        if (value!=null&&!value.isEmpty()){
                            double bvalue=Double.parseDouble(value);
                            method.invoke(model, bvalue);
                        }
                    } else if (type.equals(type_float)) {
                        Method method=classType.getMethod("set" + upperName,float.class);
                        Float value = sp.getFloat(name, 0);
                        method.invoke(model,value);
                    } else {
                        Log.e(type , "  value ==null");
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

}
