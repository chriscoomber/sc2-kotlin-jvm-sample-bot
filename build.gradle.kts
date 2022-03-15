plugins {
    // We are building java code to the JVM
    java

    // We are also building kotlin code to the JVM
    kotlin("jvm") version "1.6.10"

    // This plugin lets us run the bot outside of an IDE using `gradle run`.
    application
}

// Configuration for `gradle run`
application {
    // The JVM needs every function to be in a class, so the main function in Bot.kt
    // is automatically placed in a class called BotKt when compiled to JVM bytecode.
    // If the main function were a static function within the Bot class, this would just
    // be `org.example.kotlinjvmbot.Bot`.
    mainClassName = "org.example.kotlinjvmbot.BotKt"
}

// This is used if you export artifacts from your/ code, such as a .jar.
// Convention is that it matches the package names that your code uses.
group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.github.ocraft:ocraft-s2client-bot:0.4.9")  // From mavenCentral()

    // We don't currently have unit tests, but you might want to use these libraries if you write some.
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}