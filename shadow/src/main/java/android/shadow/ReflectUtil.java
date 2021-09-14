package android.shadow;

import android.content.Context;
import android.content.ContextWrapper;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class ReflectUtil {
    private static Field field_ContextWrapper_setBase = getContextWrapperSetBaseField();

    public static void setContextWrapper(Context contextWrapper, Context baseContext) {
        if (contextWrapper instanceof ContextWrapper) {
            try {
                if (field_ContextWrapper_setBase == null) {
                    field_ContextWrapper_setBase = getContextWrapperSetBaseField();
                }

                field_ContextWrapper_setBase.set(contextWrapper, baseContext);
            } catch (Throwable e) {
                ShadowLog.e("fail set ContextWrapper=" + contextWrapper + " mBase=" + baseContext, e);
            }
        }
    }

    public static Field getContextWrapperSetBaseField() {
        Field field_setBase = null;
        try {
            field_setBase = ContextWrapper.class.getDeclaredField("mBase");
            return (Field) makeAccessible(field_setBase);
        } catch (Throwable e) {
            ShadowLog.e("cannot get ContextWrapper mBase Field", e);
        }
        return null;
    }

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

    private static final Map<Class<?>, Object> PRIMITIVE_TYPE_DEFAULT_VALUE_MAP = new HashMap<Class<?>, Object>() {
        {
            put(boolean.class, false);
            put(byte.class, (byte) 0);
            put(char.class, (char) 0);
            put(short.class, (short) 0);
            put(int.class, 0);
            put(long.class, 0);
            put(float.class, 0);
            put(double.class, 0);
        }
    };

    public static Object wrapReturnValue(Object returnValue, Class<?> returnType) {
        return (returnValue == null ? PRIMITIVE_TYPE_DEFAULT_VALUE_MAP.get(returnType) : returnValue);
    }

    public static final Set<Method> OBJECT_METHODS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(Object.class.getDeclaredMethods())));

    public static boolean isObjectMethod(Method method) {
        return OBJECT_METHODS.contains(method);
    }
}
