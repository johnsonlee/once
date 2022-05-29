package io.johnsonlee.once

import org.junit.Test
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals

class OnceTest {

    @Test
    fun `execute only once in single thread`() {
        val once = Once<Int>()
        val v1 = once {
            0
        }
        val v2 = once {
            1
        }
        val v3: Int? by once
        assertEquals(0, v1)
        assertEquals(v1, v2)
        assertEquals(v1, v3)
    }

    @Test
    fun `execute only once in fixed thread pool`() {
        runInThreadPool(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()))
    }

    @Test
    fun `execute only once in cached thread pool`() {
        runInThreadPool(Executors.newCachedThreadPool())
    }

    @Test
    fun `execute only once in work stealing pool`() {
        runInThreadPool(Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors()))
    }

    private fun runInThreadPool(executor: ExecutorService) {
        val count = AtomicInteger(0)
        val once = Once<Int>()
        val values = (0..10000).map { i ->
            executor.submit<Int> {
                once {
                    count.incrementAndGet()
                    i
                }
            }
        }

        try {
            values.forEach {
                it.get()
            }
        } finally {
            executor.shutdown()
            executor.awaitTermination(1, TimeUnit.MINUTES)
        }

        val value = values.map(Future<Int>::get).distinct()
        assertEquals(1, value.size)
        assertEquals(values.first().get(), value.single())
        assertEquals(1, count.get())
    }

}