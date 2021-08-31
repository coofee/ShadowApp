package android.shadow;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class ReflectUtil {

    public static AccessibleObject makeAccessible(AccessibleObject accessibleObject) {
        accessibleObject.setAccessible(true);
        return accessibleObject;
    }

    public static void printFields(Class<?> clazz) {
        try {
            Field[] declaredFields = clazz.getDeclaredFields();
            if (declaredFields != null) {
                for (Field f : declaredFields) {
                    ShadowLog.e("field=" + f);
                }
            }
        } catch (Throwable e) {
            ShadowLog.e("fail printFields", e);
        }
    }

    public static void printConstructor(Class<?> clazz) {
        try {
            Constructor<?>[] constructors = clazz.getConstructors();
            if (constructors != null) {
                for (Constructor constructor : constructors) {
                    ShadowLog.e("constructor=" + constructor);
                }
            }
        } catch (Throwable e) {
            ShadowLog.e("fail printConstructor", e);
        }
    }
}
