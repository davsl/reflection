package sliep.jes.reflection

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

@PublishedApi
internal interface ReflectAccessor {
    fun fields(clazz: Class<*>): Array<Field>
    fun constructors(clazz: Class<*>): Array<Constructor<*>>
    fun methods(clazz: Class<*>): Array<Method>
    fun setFinal(field: Field, isFinal: Boolean)
    fun <T> allocateInstance(clazz: Class<T>): T
}