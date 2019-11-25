package sliep.jes.reflection;

import kotlin.jvm.JvmSynthetic;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class JU {
    private static final Map<Class<?>, Class<?>> PRIMITIVES_TO_WRAPPERS = new HashMap<>();

    static {
        PRIMITIVES_TO_WRAPPERS.put(Boolean.TYPE, Boolean.class);
        PRIMITIVES_TO_WRAPPERS.put(Character.TYPE, Character.class);
        PRIMITIVES_TO_WRAPPERS.put(Byte.TYPE, Byte.class);
        PRIMITIVES_TO_WRAPPERS.put(Short.TYPE, Short.class);
        PRIMITIVES_TO_WRAPPERS.put(Integer.TYPE, Integer.class);
        PRIMITIVES_TO_WRAPPERS.put(Long.TYPE, Long.class);
        PRIMITIVES_TO_WRAPPERS.put(Float.TYPE, Float.class);
        PRIMITIVES_TO_WRAPPERS.put(Double.TYPE, Double.class);
        PRIMITIVES_TO_WRAPPERS.put(Void.TYPE, Void.class);
    }

    private JU() {
    }

    @JvmSynthetic
    public static boolean isCallableFrom(Class<?>[] actual, Class<?>[] expected) {
        if (actual.length != expected.length) return false;
        for (int i = 0; i < actual.length; i++) {
            Class<?> exp = expected[i];
            if (exp == null) continue;
            Class<?> act = actual[i];
            boolean ep = exp.isPrimitive();
            boolean ap = act.isPrimitive();
            if (ep != ap)
                if (ep) exp = PRIMITIVES_TO_WRAPPERS.get(exp);
                else act = PRIMITIVES_TO_WRAPPERS.get(act);
            if (!act.isAssignableFrom(exp)) return false;
        }
        return true;
    }

    @JvmSynthetic
    public static boolean isGetter(Method method) {
        if (method.getParameterTypes().length != 0) return false;
        String name = method.getName();
        return name.startsWith("get") || name.startsWith("is");
    }

    @JvmSynthetic
    public static boolean isSetter(Method method) {
        if (method.getParameterTypes().length != 1) return false;
        return method.getName().startsWith("set");
    }

    @NotNull
    @JvmSynthetic
    public static String propName(Method method) {
        String name = method.getName();
        if (name.startsWith("is") && Character.isUpperCase(name.charAt(2))) return name;
        else return Character.toLowerCase(name.charAt(3)) + name.substring(4);
    }

    @NotNull
    @JvmSynthetic
    public static String getterName(String field) {
        if (field.startsWith("is") && Character.isUpperCase(field.charAt(2))) return field;
        else return "get" + Character.toUpperCase(field.charAt(0)) + field.substring(1);
    }

    @NotNull
    @JvmSynthetic
    public static String setterName(String field) {
        return "set" + Character.toUpperCase(field.charAt(0)) + field.substring(1);
    }

    @NotNull
    @JvmSynthetic
    public static Class<?>[] contentTypes(Object[] objects) {
        Class<?>[] types = new Class<?>[objects.length];
        for (int i = 0; i < types.length; i++) {
            Object item = objects[i];
            if (item != null) types[i] = item.getClass();
        }
        return types;
    }
}
