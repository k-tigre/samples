package by.tigre.samples.data.bisness

import java.util.*

interface IdGenerator {
    fun generate(): String

    class Impl : IdGenerator {
        override fun generate(): String = UUID.randomUUID().toString()
    }
}
