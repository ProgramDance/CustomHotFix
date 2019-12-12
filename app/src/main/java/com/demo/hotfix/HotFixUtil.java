package com.demo.hotfix;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class HotFixUtil {
    public static class Constant {
        public static final String PATCH_DIR_NAME_IN_APP = "patch";
    }

    public static void startFix(Context context, String patchPath) {
        if (context == null) {
            return;
        }

        File file = new File(patchPath);
        if(!file.exists()){
            Toast.makeText(context, "补丁文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("tag", "startFix>>patchPath:" + patchPath);

        File patchFileInApp = context.getDir(Constant.PATCH_DIR_NAME_IN_APP, Context.MODE_PRIVATE);
        if (!patchFileInApp.exists()) {
            patchFileInApp.mkdirs();
        }

        //获取应用内部的类加载器
        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
        //实例化dexClassLoader用于加载补丁dex
        DexClassLoader dexClassLoader = new DexClassLoader(patchPath, patchFileInApp.getAbsolutePath(), null, pathClassLoader);

        try {
            //获取dexclassloader和pathclassloader的dexpathlist
            Object dexPathList = getPathList(dexClassLoader);
            Object pathPathList = getPathList(pathClassLoader);
            //获取补丁的elements数组
            Object dexElements = getDexElements(dexPathList);
            //获取程序的elements
            Object pathElements = getDexElements(pathPathList);
            //合并两个数组
            Object resultElements = combineArray(dexElements, pathElements);
            //将合并后的数组设置给PathClassLoader
            setField(pathPathList, pathPathList.getClass(), "dexElements", resultElements);
            Toast.makeText(context, "热修复完成", Toast.LENGTH_SHORT).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "热修复失败", Toast.LENGTH_SHORT).show();
        }
    }

    private static void setField(Object pathPathList, Class<?> clazz, String fieldName, Object resultElements) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(pathPathList, resultElements);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static Object combineArray(Object arrayLhs, Object arrayRhs) {
        Class<?> clazz = arrayLhs.getClass().getComponentType();
        int i = Array.getLength(arrayLhs);
        int j = Array.getLength(arrayRhs) + i;
        Object result = Array.newInstance(clazz, j);
        for (int k = 0; k < j; ++k) {
            if (k < i) {
                Array.set(result, k, Array.get(arrayLhs, k));
            } else {
                Array.set(result, k, Array.get(arrayRhs, k - i));
            }
        }
        return result;
    }

    //获得dexElements
    private static Object getDexElements(Object dexPathList) {
        return getField(dexPathList, dexPathList.getClass(), "dexElements");
    }

    //获得DexPathList
    private static Object getPathList(Object classLoader) throws ClassNotFoundException {
        return getField(classLoader, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
    }

    //通过反射获取一个类私有属性的值
    private static Object getField(Object obj, Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
