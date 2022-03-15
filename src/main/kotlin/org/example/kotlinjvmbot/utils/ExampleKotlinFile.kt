package org.example.kotlinjvmbot.utils

import java.util.*
import java.util.concurrent.ThreadLocalRandom

// An example of how you can use code from one file in another. Unlike Java, Kotlin doesn't require each file contain
// one top-level class/interface.

/** Get a random float uniformly distributed between -1 and 1. */
fun getRandomScalar(): Float = ThreadLocalRandom.current().nextFloat() * 2 - 1

/** Get a random int uniformly distributed between 0 and n-1. */
fun getRandomInt(n: Int): Int = ThreadLocalRandom.current().nextInt(n)

/**
 *  Extension function to conveniently convert a Java `Optional` to a nullable, which Kotlin solves as a language
 *  feature rather than with a dedicated class.
 */
fun <T> Optional<T>.orNull(): T? = orElse(null)