package com.dylan.library.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Author: Dylan
 * Date: 2020/3/28
 * Desc:
 */
public class EntityReflection {
    /**
     * @param newEntity 用于赋值的实体类
     * @param oldEntity 需要待赋值的实体类
     * @param  includeEmptyValue  当新 Enity 某属性值 是null 是否替换旧Enity对应属性
     */
    public static void updateOldEntity(Object newEntity, Object oldEntity,boolean includeEmptyValue) {
       try {
           Class clazz1 = Class.forName(newEntity.getClass().getName());
           Class clazz2 = Class.forName(oldEntity.getClass().getName());
           if (!clazz1.getSimpleName().equals(clazz2.getSimpleName()))return;
           //      获取两个实体类的所有属性
           Field[] fields1 = clazz1.getDeclaredFields();
           Field[] fields2 = clazz2.getDeclaredFields();
           EntityReflection cr = new EntityReflection();
           //      遍历class1Bean，获取逐个属性值，然后遍历class2Bean查找是否有相同的属性，如有相同则赋值
           for (Field f1 : fields1) {
               if(f1.getName().equals("id"))
                   continue;
               Object value = cr.invokeGetMethod(newEntity ,f1.getName(),null);

               if (!includeEmptyValue){
                   if (EmptyUtils.isEmpty(value))return;
               }


               for (Field f2 : fields2) {
                   if(f1.getName().equals(f2.getName())){
                       Object[] obj = new Object[1];
                       obj[0] = value;
                       cr.invokeSetMethod(oldEntity, f2.getName(), obj);
                   }
               }
           }

       }catch (Exception e){
           e.printStackTrace();
       }

    }

    /**
     *
     * 执行某个Field的getField方法
     */
    private Object invokeGetMethod(Object clazz, String fieldName, Object[] args)
    {
        String methodName = fieldName.substring(0, 1).toUpperCase()+ fieldName.substring(1);
        Method method = null;
        try
        {
            method = Class.forName(clazz.getClass().getName()).getDeclaredMethod("get" + methodName);
            return method.invoke(clazz);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }
    }

    /**
     *
     * 执行某个Field的setField方法
     */
    private Object invokeSetMethod(Object clazz, String fieldName, Object[] args)
    {
        String methodName = fieldName.substring(0, 1).toUpperCase()+ fieldName.substring(1);
        Method method = null;
        try
        {
            Class[] parameterTypes = new Class[1];
            Class c = Class.forName(clazz.getClass().getName());
            Field field = c.getDeclaredField(fieldName);
            parameterTypes[0] = field.getType();
            method = c.getDeclaredMethod("set" + methodName,parameterTypes);
            return method.invoke(clazz,args);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }
    }
}
