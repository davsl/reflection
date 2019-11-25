package sliep.jes.reflection

import org.junit.Assert
import org.junit.Test

class FieldsTest {

    @Test
    fun testField() {
        val instance = MyClass()
        val clazz = MyClass::class.java
        val hello = clazz.field { it.get(instance) == "World" }
        Assert.assertEquals("hello", hello.name)
        bm("Get fields", 10) {
            Assert.assertEquals("World", instance.fieldValue("hello"))

            Assert.assertEquals(instance.one, instance.fieldValue("one"))
            Assert.assertEquals(2345678L, instance.fieldValue("lol"))
            Assert.assertEquals("Hello", instance.fieldValue("world"))

            Assert.assertEquals(23456, clazz.staticFieldValue("CONSTANT"))
            Assert.assertEquals("Shh", clazz.staticFieldValue("HIDDEN"))
            Assert.assertEquals("What", clazz.staticFieldValue("companionVar"))
        }
    }

    @Test
    fun testSetField() {
        val instance = MyClass()
        val clazz = MyClass::class.java
        val hello = clazz.field("hello")
        Assert.assertEquals("World", hello[instance])
        hello[instance] = "hello"
        Assert.assertEquals("hello", hello[instance])
        val one = clazz.field("one")
        Assert.assertEquals(1, one[instance])
        one[instance] = 12345
        Assert.assertEquals(12345, one[instance])
        instance.setFieldValue("lol", 12L)
        Assert.assertEquals(12L, instance.fieldValue("lol"))
        Assert.assertEquals("wedfvb", instance.fieldValue("w", "wowowo"))
        clazz.setStaticFieldValue("CONSTANT", 666)
        Assert.assertEquals(666, clazz.staticFieldValue("CONSTANT"))
        Assert.assertNotEquals(666, MyClass.CONSTANT) //Compile time hard coded
    }
}