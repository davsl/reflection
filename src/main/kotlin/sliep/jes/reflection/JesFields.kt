@file:Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST", "ReplaceCallWithBinaryOperator")

package sliep.jes.reflection

import java.lang.reflect.Field
import java.lang.reflect.Modifier

inline fun Class<*>.field(name: String): Field {
    for (field in accessor.fields(this)) if (field.name.equals(name)) return field
    throw NoSuchFieldException(name)
}

inline fun Class<*>.field(predicate: (field: Field) -> Boolean): Field {
    for (field in accessor.fields(this)) if (predicate(field)) return field
    throw NoSuchFieldException("Class $name contains no field matching the predicate.")
}

inline fun <R> Class<*>.staticFieldValue(name: String): R = field(name)[null] as R

inline fun Class<*>.setStaticFieldValue(name: String, value: Any?) = field(name).set(null, value)

inline fun <R> Any.fieldValue(name: String): R = this::class.java.field(name)[this] as R

inline fun Any.setFieldValue(name: String, value: Any?) = this::class.java.field(name).set(this, value)

fun <R> Any.fieldValue(vararg names: String, fallback: Boolean = false): R {
    var result: Any? = this
    for (part in names) {
        if (result == null)
            if (fallback) return null as R
            else throw IllegalStateException("$part is null")
        result = result::class.java.field(part)[result]
    }
    return result as R
}

inline fun Field.copy(from: Any?, to: Any?) = set(to, get(from))

inline var Field.isFinal
    get() = Modifier.isFinal(modifiers)
    set(value) = accessor.setFinal(this, value)
