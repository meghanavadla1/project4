package com.example.demo;

import java.lang.reflect.Field;

public class TestUtils {


    public  static void injectObjects(Object target,String fieldName,Object toInject)  {
        boolean wasPrivte=false;
        try {
            Field f=target.getClass().getDeclaredField(fieldName);
if (!f.isAccessible()){
    f.setAccessible(true);
    wasPrivte=true;
}
f.set(target,toInject);
if (wasPrivte){
    f.setAccessible(false);
}
        }catch (NoSuchFieldException e){
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }

    }



























}
