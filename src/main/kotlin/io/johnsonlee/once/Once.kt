package io.johnsonlee.once

import kotlin.reflect.KProperty

private object UNDEFINED

class Once<T : Any> {

    private var _value: Any? = UNDEFINED

    @Suppress("UNCHECKED_CAST")
    val value: T?
        get() = _value as? T

    @Suppress("UNCHECKED_CAST")
    operator fun invoke(action: () -> T): T {
        val v1 = _value
        if (v1 !== UNDEFINED) {
            return v1 as T
        }

        return synchronized(this) {
            val v2 = _value
            if (v2 !== UNDEFINED) {
                v2
            } else {
                val v3 = action()
                _value = v3
                v3
            }
        } as T
    }

}

operator fun <T : Any> Once<T>.getValue(thisRef: Any?, property: KProperty<*>): T? = value