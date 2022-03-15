package org.example.kotlinjvmbot.utils;

// You can have as much or as little of your code in java as you like. There are some things to know when calling code
// in one language from the other - see the Kotlin docs.
public class ExampleJavaClass {
    public static void helloFromJava() {
        System.out.printf("Hello, this code was written in Java. Here's a random number: %d",
                // Kotlin called from Java code.
                ExampleKotlinFileKt.getRandomInt(10));
    }
}
