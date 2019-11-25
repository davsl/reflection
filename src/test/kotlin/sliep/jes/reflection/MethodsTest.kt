package sliep.jes.reflection

import org.junit.Assert
import org.junit.Test

class MethodsTest {

    @Test
    fun testInvoke() {
        val instance = MyClass()
        val clazz = MyClass::class.java
        var result: String = instance.invoke("invokeMe", true, 234)
        Assert.assertEquals("BBB", result)
        result = instance.invoke("invokeMe", true, 234, "")
        Assert.assertEquals("AAA", result)
        clazz.method { it.name == "toString" }.invoke(instance)
        Assert.assertEquals("SSSS", clazz.invokeStatic("invokeMe"))

        result = instance.invokeGetter("setGet")
        Assert.assertEquals("-_-", result)
        instance.invokeSetter("setGet", "<[-_-]>")
        result = instance.invokeGetter("setGet")
        Assert.assertEquals("<[-_-]>", result)
    }
}