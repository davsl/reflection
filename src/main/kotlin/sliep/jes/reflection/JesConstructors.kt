@file:Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")

package sliep.jes.reflection

import java.lang.reflect.Constructor
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Modifier
import java.lang.reflect.Proxy
import java.lang.reflect.Array as JArray

fun <T> Class<T>.constructor(vararg paramTypes: Class<*>?): Constructor<T> {
    for (constructor in accessor.constructors(this)) if (JU.isCallableFrom(constructor.parameterTypes, paramTypes))
        return constructor as Constructor<T>
    throw NoSuchMethodException(methodToString(name, "<init>", paramTypes))
}

inline fun <reified T> allocateInstance(): T = T::class.java.allocateInstance()

inline fun <T> Class<T>.allocateInstance(): T = accessor.allocateInstance(this)

inline fun <T> Class<T>.instantiate(vararg params: Any?): T = constructor(*JU.contentTypes(params)).newInstance(*params)

inline fun <T> Class<T>.instantiateArray(length: Int): Array<T> = JArray.newInstance(this, length) as Array<T>

inline fun <T> Class<T>.implement(handler: InvocationHandler): T =
    Proxy.newProxyInstance(classLoader, arrayOf(this), handler) as T

inline fun <reified T : Any> T.cloneNative(): T {
    val instance = allocateInstance<T>()
    this copyTo instance
    return instance
}

inline infix fun <reified T : Any> T.copyTo(dst: T) {
    for (field in accessor.fields(T::class.java)) if (!Modifier.isStatic(field.modifiers))
        field.copy(this, dst)
}