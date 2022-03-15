# Sample Kotlin bot targeting the JVM

This is an example of how you would start writing a bot with Kotlin and/or Java code. This uses gradle as the main build
tool. I recommend using IntelliJ IDEA as your main IDE, as it has very good support for Kotlin (JetBrains developed 
Kotlin). However, this should all work from the command line too.

The implementation here is simply the tutorial bot given in https://ocraft.github.io/ocraft-s2client. You are free to
write your bot however you like, this hopefully just helps you get started with building and organising your code. 

## Prerequisites

You need a Java JDK installed, of a version equal to or greater than 1.9, and your environment variable JAVA_HOME should point to this. I've tested with JDK 11.

You need Starcraft II retail installed, and you need to run it and ensure the map "2000 Atmospheres LE" is downloaded (e.g. host a custom game lobby with that map).

## Running the bot

First, acquire this codebase. The easiest way is via `git clone` (requires `git` to be installed, obviously).

Simply the `run` task with gradle. This is most easily done from the command line / terminal with the gradle wrapper:

- On Linux/Mac/Un\*x, this is `./gradlew run` from the root directory of this codebase.
- On Windows, this is `.\gradlew run` from the root folder of this codebase.

### Recommendations

If you plan to use this example as a base for your bot, you probably want to fork this project in GitHub, and work on the fork which you will have complete ownership over.

I recommend IntelliJ IDEA with the Kotlin plugin as the main IDE. You shouldn't need to do any setup other than opening the folder/directory as an existing project. Make sure your
Kotlin plugin is up to date.

## Where to go from here?

I recommend the Starcraft 2 AI discord channel. Since you are compiling both Kotlin and Java to the JVM (Java Virtual Machine), you can probably pretend you are writing a Java bot for all intents and purposes.
