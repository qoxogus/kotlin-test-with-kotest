package com.kotlin.test.util

import java.util.*

inline fun <T> T?.alsoIfTrue(block: () -> Unit): T? {
    when (this) {
        is Optional<*> -> this.get()
        else -> this
    }?.also {
        if (it as Boolean) block()
    }
    return this@alsoIfTrue
}

inline fun <T> T?.alsoIfFalse(block: () -> Unit): T? {
    when (this) {
        is Optional<*> -> this.get()
        else -> this
    }?.also {
        if (!(it as Boolean)) block()
    }
    return this
}

fun CharSequence?.isNotNullOrNotBlank(): Boolean {
    return !this.isNullOrBlank()
}

fun <T> Collection<T>?.isNotNullOrNotEmpty(): Boolean {
    return !this.isNullOrEmpty()
}