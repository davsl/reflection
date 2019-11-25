package sliep.jes.reflection

import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

@JvmField
var accessor: ReflectAccessor = JVMAccessor()

inline val Method.isGetter get() = JU.isGetter(this)
inline val Method.isSetter get() = JU.isSetter(this)
inline val Method.propName get() = JU.propName(this)
inline val Field.getterName get() = JU.getterName(name)
inline val Field.setterName get() = JU.setterName(name)

inline val Class<*>.dimensions get() = name.lastIndexOf('[') + 1
inline val Array<*>.contentTypes: Array<Class<*>?> get() = JU.contentTypes(this)
inline val Field.typeArguments: Array<Type>
    get() = (genericType as? ParameterizedType)?.actualTypeArguments ?: arrayOf()

@PublishedApi
internal fun methodToString(clazz: String, method: String, params: Array<out Class<*>?>): String {
    val builder = StringBuilder()
    for (param in params) builder.append(", ").append(param?.name ?: "[?]")
    builder.delete(0, 2)
    return "$clazz.$method($builder)"
}