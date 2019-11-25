@file:Suppress("unused", "UNUSED_PARAMETER")

package sliep.jes.reflection


class MyClass {
    var constructorCalled = false

    init {
        constructorCalled = true
    }

    fun invokeMe(v: Boolean, ff: Int, ss: String): String {
        return "AAA"
    }

    fun invokeMe(v: Boolean, ff: Int): String {
        return "BBB"
    }

    private var hello = "World"
    @JvmField
    val one = 1
    @JvmSynthetic
    var lol = 2345678L
    internal var world = "Hello"
    private val w = MyClass1()
    private val lll: List<String>? = null

    @PublishedApi
    internal var setGet = "-_-"

    companion object {
        const val CONSTANT = 23456
        private const val HIDDEN = "Shh"
        var companionVar = "What"

        @JvmStatic
        fun invokeMe(): String {
            return "SSSS"
        }

    }
}

class MyClass1 {
    private val wowowo = "wedfvb"
}

inline val Long.ms get() = (this / 1000000f).toString().substring(0, 4) + "ms "

inline fun bm(message: String? = "", repeatCount: Int = 1, block: () -> Unit) {
    var total = 0L
    for (index in 0 until repeatCount) {
        val start = System.nanoTime()
        block()
        val partial = System.nanoTime() - start
        total += partial
    }
    val avg = total / repeatCount
    System.err.println("Test [$message] Repeat count: $repeatCount, total time: ${total.ms}, avg: ${avg.ms}")
}


inline fun bmg(message: String? = "", repeatCount: Int = 1, block: () -> Unit) {
    var total = 0L
    val partials = ArrayList<Long>()
    var max = 0L
    var min = 0L
    val group = repeatCount / 18 //num of columns
    for (index in 0 until repeatCount) {
        val start = System.nanoTime()
        block()
        val partial = System.nanoTime() - start
        total += partial
        if (index % group == 0) partials.add(partial)
        if (partial > max) max = partial
        if (partial < min) min = partial
    }
    val height = 100
    val cutTop = 90
    repeat(height) { row ->
        if (row < cutTop) return@repeat
        for (partial in partials) {
            val score = (partial * height) / max
            if (score >= (height - row)) System.err.print("  .  ")
        }
        System.err.println()
    }
    for (partial in partials) System.err.print(partial.ms)
    val avg = total / repeatCount
    System.err.println()
    System.err.println("Test [$message] Repeat count: $repeatCount, total time: ${total.ms}, avg: ${avg.ms}")
}