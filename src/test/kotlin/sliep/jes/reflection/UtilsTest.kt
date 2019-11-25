package sliep.jes.reflection

import org.junit.Assert
import org.junit.Test

class UtilsTest {

    @Test
    fun testUtils() {
        val clazz = MyClass::class.java
        var method = clazz.method { it.name.startsWith("get") }
        Assert.assertEquals(true, method.isGetter)
        method = clazz.method { it.name.startsWith("set") }
        Assert.assertEquals(false, method.isGetter)
        Assert.assertEquals(true, method.isSetter)
        method = clazz.method("getSetGet")
        Assert.assertEquals("setGet", method.propName)

        val prop = method.declaringClass.field(method.propName)
        Assert.assertEquals("setSetGet", prop.setterName)
        Assert.assertEquals("getSetGet", prop.getterName)

        val array = arrayOf(arrayOf(arrayOf("")))
        Assert.assertEquals(3, array::class.java.dimensions)
        val types = clazz.field("lll").typeArguments
        Assert.assertArrayEquals(arrayOf(String::class.java), types)
        Assert.assertArrayEquals(
            arrayOf(String::class.java, Int::class.javaObjectType, null, Boolean::class.javaObjectType),
            arrayOf("", 2, null, true).contentTypes
        )
    }
}