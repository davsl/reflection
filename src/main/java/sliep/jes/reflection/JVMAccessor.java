package sliep.jes.reflection;

import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;

@SuppressWarnings("all")
final class JVMAccessor implements ReflectAccessor {
    private final Field modifiers;
    private final Unsafe unsafe;
    private final HashMap<Class<?>, Field[]> cachedFields = new HashMap<>();
    private final HashMap<Class<?>, Constructor<?>[]> cachedConstructors = new HashMap<>();
    private final HashMap<Class<?>, Method[]> cachedMethods = new HashMap<>();

    public JVMAccessor() {
        Field modifiers = null;
        try {
            modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
        } catch (NoSuchFieldException e) {
            try {
                modifiers = Field.class.getDeclaredField("accessFlags");
                modifiers.setAccessible(true);
            } catch (NoSuchFieldException ignored) {
            }
        }
        this.modifiers = modifiers;
        Unsafe unsafe = null;
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) theUnsafe.get(null);
        } catch (Exception ignored) {
        }
        this.unsafe = unsafe;
    }

    @NotNull
    @Override
    public Field[] fields(@NotNull Class<?> clazz) {
        Field[] result = cachedFields.get(clazz);
        if (result == null) {
            LinkedHashSet<Field> allFields = new LinkedHashSet<>();
            fieldsInternal(clazz, allFields, false);
            result = allFields.toArray(new Field[allFields.size()]);
            cachedFields.put(clazz, result);
        }
        return result;
    }

    @NotNull
    @Override
    public Constructor<?>[] constructors(@NotNull Class<?> clazz) {
        Constructor<?>[] result = cachedConstructors.get(clazz);
        if (result == null) {
            result = clazz.getDeclaredConstructors();
            for (Constructor<?> constructor : result)
                if (!constructor.isAccessible()) constructor.setAccessible(true);
            cachedConstructors.put(clazz, result);
        }
        return result;
    }

    @NotNull
    @Override
    public Method[] methods(@NotNull Class<?> clazz) {
        Method[] result = cachedMethods.get(clazz);
        if (result == null) {
            HashMap<MethodKey, Method> allMethods = new HashMap<>();
            methodsInternal(clazz, allMethods, false);
            result = allMethods.values().toArray(new Method[allMethods.size()]);
            cachedMethods.put(clazz, result);
        }
        return result;
    }

    @Override
    public void setFinal(@NotNull Field field, boolean isFinal) {
        int mod = field.getModifiers();
        if (Modifier.isFinal(mod) != isFinal) try {
            mod = isFinal ? (mod | Modifier.FINAL) : (mod & ~Modifier.FINAL);
            modifiers.set(field, mod);
        } catch (IllegalAccessException e) {
            throw new UnsupportedOperationException("JVMAccessor is not available on this environment.", e);
        }
    }

    @Override
    public <T> T allocateInstance(@NotNull Class<T> clazz) {
        try {
            return (T) unsafe.allocateInstance(clazz);
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void fieldsInternal(@NotNull Class<?> clazz, LinkedHashSet<Field> allFields, boolean instanceOnly) {
        Field[] declared = clazz.getDeclaredFields();
        for (Field field : declared) {
            int mod = field.getModifiers();
            if (instanceOnly && Modifier.isStatic(mod)) continue;
            if (!field.isAccessible()) field.setAccessible(true);
            if (Modifier.isFinal(mod)) try {
                modifiers.set(field, mod & ~Modifier.FINAL);
            } catch (IllegalAccessException e) {
                throw new UnsupportedOperationException("JVMAccessor is not available on this environment.", e);
            }
            allFields.add(field);
        }
        for (Class<?> iFace : clazz.getInterfaces()) fieldsInternal(iFace, allFields, true);
        Class<?> parent = clazz.getSuperclass();
        if (parent != null) fieldsInternal(parent, allFields, true);
    }

    private void methodsInternal(@NotNull Class<?> clazz, HashMap<MethodKey, Method> allMethods, boolean instanceOnly) {
        Method[] declared = clazz.getDeclaredMethods();
        for (Method method : declared) {
            if (instanceOnly && Modifier.isStatic(method.getModifiers())) continue;
            MethodKey key = new MethodKey(method);
            if (!allMethods.containsKey(key)) {
                if (!method.isAccessible()) method.setAccessible(true);
                allMethods.put(key, method);
            }
        }
        for (Class<?> iFace : clazz.getInterfaces()) methodsInternal(iFace, allMethods, true);
        Class<?> parent = clazz.getSuperclass();
        if (parent != null) methodsInternal(parent, allMethods, true);
    }

    private static final class MethodKey {
        private final String name;
        private final Class<?>[] params;

        private MethodKey(Method method) {
            name = method.getName();
            params = method.getParameterTypes();
        }

        @Override
        public int hashCode() {
            return name.hashCode() + 31 * Arrays.hashCode(params);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof MethodKey && ((MethodKey) obj).name.equals(name) && Arrays.equals(((MethodKey) obj).params, params);
        }
    }
}
