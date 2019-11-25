@file:Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")

package sliep.jes.reflection

import java.lang.reflect.Method

inline fun Class<*>.method(name: String, vararg paramTypes: Class<*>?): Method {
    for (method in accessor.methods(this))
        if (method.name == name && JU.isCallableFrom(method.parameterTypes, paramTypes)) return method
    throw NoSuchMethodException(methodToString(this.name, name, paramTypes))
}

inline fun Class<*>.method(predicate: (method: Method) -> Boolean): Method {
    for (method in accessor.methods(this)) if (predicate(method)) return method
    throw NoSuchFieldException("Class $name contains no method matching the predicate.")
}

inline fun <R> Class<*>.invokeStatic(method: String, vararg params: Any?): R =
    method(method, *JU.contentTypes(params)).invoke(null, *params) as R

inline fun <R> Any.invoke(method: String, vararg params: Any?): R =
    this::class.java.method(method, *JU.contentTypes(params)).invoke(this, *params) as R

inline fun <R> Any.invokeGetter(field: String): R {
    val name = if (field.startsWith("is") && field[2].isUpperCase()) field
    else "get${field[0].toUpperCase()}${field.substring(1)}"
    return this::class.java.method(name).invoke(this) as R
}

inline fun Any.invokeSetter(field: String, value: Any?) {
    val name = field[0].toUpperCase() + field.substring(1)
    this::class.java.method("set$name", value?.let { it::class.java }).invoke(this, value)
}