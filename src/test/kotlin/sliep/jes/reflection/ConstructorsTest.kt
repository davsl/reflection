package sliep.jes.reflection

import org.junit.Assert
import org.junit.Test
import java.lang.reflect.InvocationHandler

class ConstructorsTest {

    @Test
    fun testConstructor() {
        val clazz = MyClass::class.java
        val instance = clazz.instantiate()
        Assert.assertEquals(true, instance.constructorCalled)
        val array = clazz.instantiateArray(20)
        Assert.assertEquals(clazz, array::class.java.componentType)
    }

    @Test
    fun testProxy() {
        val clazz = KLKL::class.java
        val hack = "I'm gonna kill humans"
        val instance = clazz.implement(InvocationHandler { _, _, _ -> hack })
        Assert.assertEquals(hack, instance.killPeople())
    }

    @Test
    fun testClone() {
        val clazz = MyClass::class.java
        val instance = clazz.allocateInstance()
        Assert.assertEquals(false, instance.constructorCalled)
        val ni = clazz.instantiate()
        ni copyTo instance
        Assert.assertEquals(true, instance.constructorCalled)
        Assert.assertEquals(ni.lol, instance.lol)
        Assert.assertEquals(ni.one, instance.one)
        Assert.assertEquals(ni.world, instance.world)
        val clone = instance.cloneNative()
        Assert.assertEquals(clone.lol, instance.lol)
        Assert.assertEquals(clone.one, instance.one)
        Assert.assertEquals(clone.world, instance.world)

    }


    private interface KLKL {
        fun killPeople(): String = throw UnsupportedOperationException("1st rule: machines cannot kill humans")
    }
}