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

    public static Field getField(String className, String fieldName) {
        try {
            Class<?> clazz = Class.forName(className);
            return getField(clazz, fieldName);
        } catch (Throwable e) {
            ShadowLog.e("cannot getField className=" + className + ", fieldName=" + fieldName, e);
        }
        return null;
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        try {
            Field declaredField = clazz.getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            return declaredField;
        } catch (Throwable e) {
            ShadowLog.e("cannot getField clazz=" + clazz + ", fieldName=" + fieldName, e);
        }
        return null;
    }

    public static Object getFieldValue(Object instance, Field field) {
        try {
            return field.get(instance);
        } catch (Throwable e) {
            ShadowLog.e("cannot getFieldValue field=" + field + ", instance=" + instance, e);
        }

        return null;
    }

    public static void setFieldValue(Object instance, Field field, Object value) {
        try {
            field.set(instance, value);
        } catch (Throwable e) {
            ShadowLog.e("cannot setFieldValue field=" + field + ", instance=" + instance + ", value=" + value, e);
        }
    }

    public static void printFields(Class<?> clazz) {
        printFields(0, clazz);
    }

    public static void printMethods(Class<?> clazz) {
        printMethods(0, clazz);
    }

    public static void printFields(int depth, Class<?> clazz) {
        try {
            Field[] declaredFields = clazz.getDeclaredFields();
            if (declaredFields != null) {
                for (Field f : declaredFields) {
                    ShadowLog.e(repeat("\t", depth) + "field=" + f);
                }
            }
        } catch (Throwable e) {
            ShadowLog.e("fail printFields", e);
        }
    }

    public static void printMethods(int depth, Class<?> clazz) {
        try {
            Method[] declaredMethods = clazz.getDeclaredMethods();
            if (declaredMethods != null) {
                for (Method f : declaredMethods) {
                    ShadowLog.e(repeat("\t", depth) + "method=" + f);
                }
            }
        } catch (Throwable e) {
            ShadowLog.e("fail printMethods", e);
        }
    }

    public static void printConstructor(Class<?> clazz) {
        try {
            Constructor<?>[] constructors = clazz.getConstructors();
            if (constructors != null) {
                for (Constructor constructor : constructors) {
                    ShadowLog.e("\tconstructor=" + constructor);
                }
            }
        } catch (Throwable e) {
            ShadowLog.e("fail printConstructor", e);
        }
    }

    public static void print(Class<?> clazz) {
        print(0, clazz);
    }

    public static void print(int depth, Class<?> clazz) {
        ShadowLog.e(repeat("\t", depth) + "clazz=" + clazz);
        printMethods(depth + 1, clazz);
        printFields(depth + 1, clazz);

        Class<?> superClass = clazz;
        int superClassDepth = depth;
        while ((superClass = superClass.getSuperclass()) != null) {
            if (superClass != Object.class) {
                print(++superClassDepth, superClass);
            }
        }

        int interfaceDepth = depth;
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces != null) {
            for (Class<?> inter : interfaces) {
                print(++interfaceDepth, inter);
            }
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

    public static String repeat(String str, int repeat) {
        StringBuilder stringBuilder = new StringBuilder(str.length() * repeat);
        for (int i = 0; i < repeat; i++) {
            stringBuilder.append(str);
        }
        return stringBuilder.toString();
    }
}
